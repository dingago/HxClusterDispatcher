package hx.clusterDispatcher;

/**
 * @author Xiaowei Wang
 * @version 1.0
 * @since 2019/05/28
 * 
 *        The class represents a service dispatch task. The tasks are persisted
 *        on file system as XML files. XML files would be load into memory as
 *        instance of ServiceDispatch object for better performance.
 */
public class ServiceDispatch {
	/**
	 * The name of service to dispatch on other nodes in cluster.
	 */
	private String targetService;
	/**
	 * An optional service would be invoked if targetService is failed to run.
	 * The rollback service has to be able to function correctly with the same
	 * input as target service.
	 */
	private String rollbackService;
	/**
	 * Indicates the dispatch mode. For mode STRICT, the targetService has to be
	 * invoked in all nodes, otherwise it's considered as a failure and rollback
	 * service would be invoked on any successful node. For mode LAX, the
	 * targetService would be invoked on as many nodes as possible.
	 */
	private DispatchMode dispatchMode;
	/**
	 * An optional logging service to record the activities of current service
	 * dispatch job. The logging service must has the same signature as service
	 * pub.flow:debugLog.
	 */
	private String loggingService;
	/**
	 * Indicates whether to log pipeline content.
	 */
	private boolean logPipeline;
	/**
	 * Indicates whether service dispatch would be triggered only when the
	 * targetService is a top level service.
	 */
	private boolean topServiceOnly;
	/**
	 * An optional webMethods conditional expression to evaluate if the target
	 * service is failed to run. The expression would evaluated based on service
	 * output.
	 */
	private String failureSignalExpression;
	/**
	 * An optional field name to indicate whether service is invoked by Cluster
	 * Dispatcher. By default, it's "$clusterDispatchingInitiator". Just make
	 * sure it won't make any conflict with target service.
	 */
	private String dispatchFlagField;
	/**
	 * Indicates whether service dispatch task is enabled.
	 */
	private boolean enabled;

	/**
	 * An optional webMethods conditional expression to evaluate if the rollback
	 * service is failed to run. The expression would evaluated based on service
	 * output.
	 */
	private String rollbackFailureSignalExpression;

	public ServiceDispatch(String targetService, String dispatchMode,
			boolean topServiceOnly, String loggingService, boolean logPipeline,
			String dispatchFlagField, String failureSignalExpression,
			String rollbackService, String rollbackFailureSignalExpression, boolean enabled) {
		super();
		this.targetService = targetService;
		this.dispatchMode = (dispatchMode != null && dispatchMode
				.equalsIgnoreCase(DispatchMode.STRICT.name())) ? DispatchMode.STRICT
				: DispatchMode.LAX;
		this.topServiceOnly = topServiceOnly;
		this.loggingService = (loggingService != null && !loggingService
				.isEmpty()) ? loggingService : "pub.flow:debugLog";
		this.logPipeline = logPipeline;
		this.dispatchFlagField = (dispatchFlagField != null && !dispatchFlagField
				.isEmpty()) ? dispatchFlagField
				: "$clusterDispatchingInitiator";
		this.failureSignalExpression = failureSignalExpression;
		this.rollbackService = rollbackService;
		this.rollbackFailureSignalExpression = rollbackFailureSignalExpression;
		this.enabled = enabled;
	}

	public String getTargetService() {
		return targetService;
	}

	public String getRollbackService() {
		return rollbackService;
	}

	public DispatchMode getDispatchMode() {
		return dispatchMode;
	}

	public boolean isLogPipeline() {
		return logPipeline;
	}

	public String getLoggingService() {
		return loggingService;
	}

	public boolean isTopServiceOnly() {
		return topServiceOnly;
	}

	public String getFailureSignalExpression() {
		return failureSignalExpression;
	}

	public String getDispatchFlagField() {
		return dispatchFlagField;
	}
	
	public String getRollbackFailureSignalExpression(){
		return rollbackFailureSignalExpression;
	}
	
	public boolean isEnabled(){
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public enum DispatchMode {
		STRICT, LAX
	}
}
