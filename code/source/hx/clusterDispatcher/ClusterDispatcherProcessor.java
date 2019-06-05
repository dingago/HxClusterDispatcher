package hx.clusterDispatcher;

import hx.clusterDispatcher.ServiceDispatch.DispatchMode;

import java.io.*;
import java.util.*;

import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceThread;
import com.wm.app.b2b.server.invoke.InvokeChainProcessor;
import com.wm.app.b2b.server.invoke.ServiceStatus;
import com.wm.data.IData;
import com.wm.data.IDataFactory;
import com.wm.lang.flow.ExpressionEvaluator;
import com.wm.lang.flow.MalformedExpressionException;
import com.wm.lang.ns.NSName;
import com.wm.util.ServerException;

/**
 * 
 * @author Xiaowei Wang
 * @version 1.0
 * @since 2019/05/30
 * 
 *        This class would be registered as InvokeChainProcessor after package
 *        is enabled. It would be invoked before any service is invoked
 *        automatically by IntegrationServer and that's why this is a
 *        non-invasive solution.
 */
public class ClusterDispatcherProcessor implements InvokeChainProcessor {
	/**
	 * Single instance.
	 */
	private static ClusterDispatcherProcessor instance;

	/**
	 * A hashtable contains registered ServiceDispatch info.
	 */
	private Hashtable<String, ServiceDispatch> serviceDispatchesHashtable;
	/**
	 * Remote server alias of other nodes in cluster.
	 */
	private String[] clusterNodes;

	/**
	 * A private construction method to stop user to create new instance.
	 */
	private ClusterDispatcherProcessor() {
		serviceDispatchesHashtable = new Hashtable<String, ServiceDispatch>();
	}

	public static ClusterDispatcherProcessor getInstance() {
		if (instance == null) {
			instance = new ClusterDispatcherProcessor();
		}
		return instance;
	}

	public static void registerServiceDispatch(String targetService,
			String dispatchMode, boolean topServiceOnly, String loggingService,
			boolean logPipeline, String dispatchFlagField,
			String failureSignalExpression, String rollbackService,
			String rollbackFailureSignalExpression, boolean enabled) {
		if (targetService != null) {
			getInstance().serviceDispatchesHashtable.put(targetService,
					new ServiceDispatch(targetService, dispatchMode,
							topServiceOnly, loggingService, logPipeline,
							dispatchFlagField, failureSignalExpression,
							rollbackService, rollbackFailureSignalExpression,
							enabled));
		}
	}

	public static void unregisterServiceDispatch(String targetService) {
		if (targetService != null
				&& getInstance().serviceDispatchesHashtable
						.containsKey(targetService)) {
			getInstance().serviceDispatchesHashtable.remove(targetService);
		}
	}

	public static void enableServiceDispatch(String targetService) {
		if (targetService != null
				&& getInstance().serviceDispatchesHashtable
						.containsKey(targetService)) {
			getInstance().serviceDispatchesHashtable.get(targetService)
					.setEnabled(true);
			;
		}
	}

	public static void disableServiceDispatch(String targetService) {
		if (targetService != null
				&& getInstance().serviceDispatchesHashtable
						.containsKey(targetService)) {
			getInstance().serviceDispatchesHashtable.get(targetService)
					.setEnabled(false);
			;
		}
	}

	public static void clearServiceDispatch() {
		getInstance().serviceDispatchesHashtable.clear();
	}

	public static void setClusterNodes(String[] clusterNodes) {
		getInstance().clusterNodes = clusterNodes;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void process(Iterator chain, BaseService service, IData pipeline,
			ServiceStatus status) throws ServerException {
		ServiceDispatch serviceDispatch = null;
		String serviceName = service.getNSName().getFullName();
		if (serviceDispatchesHashtable.containsKey(serviceName)) {
			serviceDispatch = serviceDispatchesHashtable.get(serviceName);
		}

		String jobId = null;
		IData clonedInput = null;

		try {
			if (serviceDispatch != null
					&& serviceDispatch.isEnabled()
					&& clusterNodes != null
					&& clusterNodes.length > 0
					&& !triggeredByClusterDispatcher(serviceDispatch, pipeline)
					&& (!serviceDispatch.isTopServiceOnly() || status
							.isTopService())) {

				logMessage(serviceDispatch,
						"Trying to do cluster dispatching for service "
								+ serviceDispatch.getTargetService(), "info",
						jobId);
				/**
				 * Create a dispatch job if all conditions are met.
				 */
				jobId = UUID.randomUUID().toString();
				logMessage(serviceDispatch, "Cloning input pipeline", "info",
						jobId);
				/**
				 * Clone the pipeline before service is invoked, in case it
				 * would be modified
				 */
				clonedInput = deepClone(pipeline);

				/**
				 * Log pipeline data in JSON format
				 */
				if (serviceDispatch.isLogPipeline()) {
					logMessage(serviceDispatch, "Target input pipeline: "
							+ pipelineToJson(pipeline), "info", jobId);
				}
			}
		} catch (Exception e) {
			logMessage(
					serviceDispatch,
					"Failed to clone input pipeline, got exception \""
							+ e.getMessage() + "\"", "error", jobId);
		}

		/**
		 * Make sure the processor chain would be processed no matter what.
		 */
		if (chain.hasNext()) {
			((InvokeChainProcessor) chain.next()).process(chain, service,
					pipeline, status);
		}

		Hashtable<String, ServiceThread> threadsHashtable = null;
		boolean hasError = false;
		/**
		 * If there is exception thrown during the chain processing, the service
		 * dispatching below won't happen.
		 */
		try {
			if (clonedInput != null) {
				if (serviceDispatch.isLogPipeline()) {
					logMessage(serviceDispatch, "Target output pipeline: "
							+ pipelineToJson(pipeline), "info", jobId);
				}

				hasError = hasError(serviceDispatch,
						serviceDispatch.getFailureSignalExpression(), pipeline,
						jobId);

				if (hasError) {
					logMessage(
							serviceDispatch,
							"Stop dispatching service because a failure signal is found in service result",
							"warn", jobId);
					return;
				}

				logMessage(serviceDispatch, "Dispatching service on nodes "
						+ Arrays.toString(clusterNodes), "info", jobId);
				threadsHashtable = dispatch(jobId, serviceDispatch, clonedInput);
			}
		} catch (Exception e) {
			logMessage(
					serviceDispatch,
					"Failed to dispatch service, got exception \""
							+ e.getMessage() + "\"", "error", jobId);
			hasError = true;
		}

		/**
		 * A list of failed nodes to invoke rollback service if necessary.
		 */
		if (clonedInput != null) {
			List<String> rollbackNodes = new ArrayList<String>();
			for (String nodeName : clusterNodes) {
				try {
					ServiceThread serviceThread = threadsHashtable
							.get(nodeName);
					if (serviceThread == null) {
						logMessage(serviceDispatch,
								"Failed to dispatch on node " + nodeName,
								"error", jobId);
						hasError = true;
					} else {
						IData output = serviceThread.getIData();

						if (serviceDispatch.isLogPipeline()) {
							logMessage(serviceDispatch,
									"Target output pipeline on node "
											+ nodeName + ": "
											+ pipelineToJson(output), "info",
									jobId);
						}

						boolean hasErrorOnNode = hasError(serviceDispatch,
								serviceDispatch.getFailureSignalExpression(),
								output, jobId);
						if (hasErrorOnNode) {
							logMessage(serviceDispatch,
									"Found failure signal on node " + nodeName,
									"error", jobId);
							hasError = true;
						} else {
							logMessage(serviceDispatch,
									"Successful to dispatch on node "
											+ nodeName, "info", jobId);
							rollbackNodes.add(nodeName);
						}
					}
				} catch (Exception e) {
					logMessage(
							serviceDispatch,
							"Failed to check service dispatching status on node "
									+ nodeName + ", got exception \""
									+ e.getMessage() + "\"", "error", jobId);
					hasError = true;
				}
			}

			if (hasError
					&& serviceDispatch.getDispatchMode() == DispatchMode.STRICT) {
				rollback(
						serviceDispatch,
						clonedInput,
						rollbackNodes.toArray(new String[rollbackNodes.size()]),
						jobId);
			}
		}
	}

	/**
	 * Invoke logging service to log message.
	 * 
	 * @param ServiceDispatch
	 *            Service Dispatch task.
	 * @param message
	 *            The message to log.
	 */
	private static void logMessage(ServiceDispatch serviceDispatch,
			String message, String level, String jobId) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("message", "[" + jobId + "] " + message);
		inputMap.put("function", "HxClusterDispatcher");
		inputMap.put("level", level);
		try {
			Service.doInvoke(
					NSName.create(serviceDispatch.getLoggingService()), input);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(message);
		}
	}

	private static IData deepClone(IData pipeline) throws IOException,
			ClassNotFoundException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(pipeline);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(
					bos.toByteArray());
			ois = new ObjectInputStream(bin);
			return (IData) ois.readObject();
		} finally {
			if (oos != null) {
				oos.close();
			}

			if (ois != null) {
				ois.close();
			}
		}
	}

	/**
	 * Dispatch service on other nodes in cluster. The service is dispatched by
	 * remote invoke.
	 * 
	 * @param jobId
	 *            A string job Id to provide more detail in log.
	 * @param serviceDispatch
	 *            Service dispatch task.
	 * @param pipeline
	 *            The pipeline to invoke service.
	 * @return A Hashtable object contains node name as key, and ServiceThread
	 *         object for remote invoke as value.
	 */
	private static Hashtable<String, ServiceThread> dispatch(String jobId,
			ServiceDispatch serviceDispatch, IData pipeline) {
		Hashtable<String, ServiceThread> threadsHashtable = new Hashtable<String, ServiceThread>();
		for (String nodeName : getInstance().clusterNodes) {
			logMessage(serviceDispatch, "Dispatching on node " + nodeName,
					"info", jobId);
			try {
				IData input = deepClone(pipeline);
				IDataMap inputMap = new IDataMap(input);
				inputMap.put("$scope", "SESSION");
				inputMap.put("$close", "true");
				inputMap.put("$clusterRetry", "false");
				inputMap.put("$alias", nodeName);
				inputMap.put("$service", serviceDispatch.getTargetService());
				/**
				 * Insert variable to mark this service is invoked remotely by
				 * Cluster Dispatcher, so it won't trigger another dispatching
				 * on target node.
				 */
				inputMap.put(serviceDispatch.getDispatchFlagField(),
						"dispatched");

				ServiceThread serviceThread = Service.doThreadInvoke(
						NSName.create("pub.remote:invoke"), input, 0L);
				threadsHashtable.put(nodeName, serviceThread);
			} catch (ClassNotFoundException | IOException e) {
				logMessage(serviceDispatch,
						"Failed to clone input pipeline for service dispatching, got exception \""
								+ e.getMessage() + "\"", "error", jobId);
			} catch (Exception e) {
				logMessage(serviceDispatch, "Failed to dispatch on node \""
						+ nodeName + "\", got exception \"" + e.getMessage()
						+ "\"", "error", jobId);
			}
		}
		return threadsHashtable;
	}

	/**
	 * Check whether the service is invoked by Cluster Dispatcher on other node.
	 * 
	 * @param serviceDispatch
	 *            The service dispatch task.
	 * @param pipeline
	 *            The pipeline to invoke service.
	 * @return Return true if service is triggered by Cluster Dispatcher,
	 *         otherwise false.
	 */
	private static boolean triggeredByClusterDispatcher(
			ServiceDispatch serviceDispatch, IData pipeline) {
		IDataMap pipelineMap = new IDataMap(pipeline);
		/**
		 * It's triggered by Cluster Dispatcher on other node if dispatch flag
		 * field could be found in pipeline.
		 */
		return pipelineMap.containsKey(serviceDispatch.getDispatchFlagField());
	}

	/**
	 * Try to invoke rollback service on local and successful nodes. The
	 * rollback operation is not guaranteed.
	 * 
	 * @param serviceDispatch
	 *            Service Dispatch task.
	 * @param pipeline
	 *            The same input to invoke target service.
	 * @param rollbackNodes
	 *            A list of node names on which target service is dispatched
	 *            successfully.
	 * @param jobId
	 *            A string job Id to provide more detail in log.
	 */
	private static void rollback(ServiceDispatch serviceDispatch,
			IData pipeline, String[] rollbackNodes, String jobId) {
		if (serviceDispatch.getRollbackService() != null
				&& !serviceDispatch.getRollbackService().isEmpty()) {
			/**
			 * Invoke rollback service on local.
			 */
			logMessage(serviceDispatch, "Trying to rollback on local", "info",
					jobId);
			try {
				IData output = Service.doInvoke(
						NSName.create(serviceDispatch.getRollbackService()),
						pipeline);

				if (serviceDispatch.isLogPipeline()) {
					logMessage(serviceDispatch, "Rollback output pipeline: "
							+ pipelineToJson(output), "info", jobId);
				}

				if (hasError(serviceDispatch,
						serviceDispatch.getRollbackFailureSignalExpression(),
						output, jobId)) {
					logMessage(
							serviceDispatch,
							"Failed to rollback on local, found failure signal in service result",
							"error", jobId);
					return;
				} else {
					logMessage(serviceDispatch,
							"Successful to rollback on local", "info", jobId);
				}
			} catch (Exception e) {
				logMessage(
						serviceDispatch,
						"Failed to rollback on local, got exception \""
								+ e.getMessage() + "\"", "error", jobId);
				return;
			}

			/**
			 * Invoke rollback service on all successful nodes remotely.
			 */
			Hashtable<String, ServiceThread> threadsHashtable = new Hashtable<String, ServiceThread>();
			logMessage(
					serviceDispatch,
					"Trying to rollback on nodes "
							+ Arrays.toString(rollbackNodes), "info", jobId);
			for (String nodeName : rollbackNodes) {
				logMessage(serviceDispatch, "Trying to rollback on node "
						+ nodeName, "info", jobId);
				try {
					IData input = deepClone(pipeline);
					IDataMap inputMap = new IDataMap(input);
					inputMap.put("$scope", "SESSION");
					inputMap.put("$close", "true");
					inputMap.put("$clusterRetry", "false");
					inputMap.put("$alias", nodeName);
					inputMap.put("$service",
							serviceDispatch.getRollbackService());

					ServiceThread serviceThread = Service.doThreadInvoke(
							NSName.create("pub.remote:invoke"), input, 0L);
					threadsHashtable.put("nodeName", serviceThread);
				} catch (ClassNotFoundException | IOException e) {
					logMessage(serviceDispatch,
							"Failed to clone input pipeline for rollback, got exception \""
									+ e.getMessage() + "\"", "error", jobId);
				} catch (Exception e) {
					logMessage(serviceDispatch,
							"Failed to rollback on node \"" + nodeName
									+ "\", got exception \"" + e.getMessage()
									+ "\"", "error", jobId);
				}
			}

			/**
			 * Check rollback status.
			 */
			for (String nodeName : rollbackNodes) {
				ServiceThread serviceThread = threadsHashtable.get(nodeName);
				if (serviceThread == null) {
					logMessage(serviceDispatch, "Failed to rollback on node "
							+ nodeName, "error", jobId);
				} else {
					try {
						IData output = serviceThread.getIData();

						if (serviceDispatch.isLogPipeline()) {
							logMessage(serviceDispatch,
									"Rollback output pipeline on node "
											+ nodeName + ": "
											+ pipelineToJson(output), "info",
									jobId);
						}

						if (hasError(serviceDispatch,
								serviceDispatch
										.getRollbackFailureSignalExpression(),
								output, jobId)) {
							logMessage(
									serviceDispatch,
									"Failed to rollback on node "
											+ nodeName
											+ ", found failure signal in service result",
									"error", jobId);
						} else {
							logMessage(serviceDispatch,
									"Successful to rollback on node "
											+ nodeName, "info", jobId);
						}
					} catch (Exception e) {
						logMessage(serviceDispatch,
								"Failed to rollback on node " + nodeName
										+ ", got exception \"" + e.getMessage()
										+ "\"", "error", jobId);
					}

				}
			}
		} else {
			logMessage(serviceDispatch,
					"No rollback service defined for target service "
							+ serviceDispatch.getTargetService(), "warn", jobId);
		}
	}

	/**
	 * Check whether there is a failure signal in service result.
	 * 
	 * @param serviceDispatch
	 *            Service Dispatch task.
	 * @param pipeline
	 *            The service result.
	 * @return Return true if failureSignalExpression is configured and a
	 *         failure signal could be found in service result. Otherwise return
	 *         false.
	 */
	private static boolean hasError(ServiceDispatch serviceDispatch,
			String failureSignalExpression, IData pipeline, String jobId) {
		if (failureSignalExpression != null
				&& !failureSignalExpression.isEmpty()) {
			try {
				return ExpressionEvaluator.evalToBoolean(
						failureSignalExpression, pipeline);
			} catch (MalformedExpressionException e) {
				logMessage(serviceDispatch,
						"Incorrect failure signal expression "
								+ failureSignalExpression
								+ " defined for target service"
								+ serviceDispatch.getTargetService(), "error",
						jobId);
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert IData to String by invoking service
	 * "pub.json:documentToJSONString".
	 * 
	 * @param pipeline
	 *            The IData object to convert.
	 * @return A String value translated from pipeline to JSON format, so error
	 *         message if there is any.
	 */
	private static String pipelineToJson(IData pipeline) {
		IData input = IDataFactory.create();
		IDataMap inputMap = new IDataMap(input);
		inputMap.put("document", pipeline);
		try {
			IData output = Service.doInvoke(
					NSName.create("pub.json:documentToJSONString"), input);
			IDataMap outputMap = new IDataMap(output);
			return outputMap.getAsString("jsonString");
		} catch (Exception e) {
			return e.getMessage();
		}

	}
}
