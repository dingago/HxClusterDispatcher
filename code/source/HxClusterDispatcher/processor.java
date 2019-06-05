package HxClusterDispatcher;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2019-06-02 12:23:05 MDT
// -----( ON-HOST: WIN-383J01IDJ3M

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.lang.reflect.Field;
import java.util.List;
import com.softwareag.util.IDataMap;
import hx.clusterDispatcher.ClusterDispatcherProcessor;
import com.wm.app.b2b.server.invoke.InvokeManager;
// --- <<IS-END-IMPORTS>> ---

public final class processor

{
	// ---( internal utility methods )---

	final static processor _instance = new processor();

	static processor _newInstance() { return new processor(); }

	static processor _cast(Object o) { return (processor)o; }

	// ---( server methods )---




	public static final void clearServiceDispatch (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(clearServiceDispatch)>> ---
		// @sigtype java 3.5
		ClusterDispatcherProcessor.clearServiceDispatch();
		// --- <<IS-END>> ---

                
	}



	public static final void disableServiceDispatch (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(disableServiceDispatch)>> ---
		// @sigtype java 3.5
		// [i] field:0:required targetService
		IDataMap pipelineMap = new IDataMap(pipeline);
		String targetService = pipelineMap.getAsString("targetService");
		
		if (targetService != null){
			ClusterDispatcherProcessor.disableServiceDispatch(targetService);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void enableServiceDispatch (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(enableServiceDispatch)>> ---
		// @sigtype java 3.5
		// [i] field:0:required targetService
		IDataMap pipelineMap = new IDataMap(pipeline);
		String targetService = pipelineMap.getAsString("targetService");
		
		if (targetService != null){
			ClusterDispatcherProcessor.enableServiceDispatch(targetService);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void getProcessorRegisteredCount (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getProcessorRegisteredCount)>> ---
		// @sigtype java 3.5
		// [o] field:0:required registeredCount
		int registeredCount = 0;
		
		try {
			Class invokeManagerClass = InvokeManager.class;
			Field processorsField = invokeManagerClass.getDeclaredField("_processors");
			processorsField.setAccessible(true);
			List processors = (List) processorsField.get(InvokeManager.getDefault());
			for (Object processor : processors){
				if(processor.getClass().getName().equals(ClusterDispatcherProcessor.class.getName())){
					registeredCount = registeredCount + 1;
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
		IDataMap pipelineMap = new IDataMap(pipeline);
		pipelineMap.put("registeredCount", String.valueOf(registeredCount));
		// --- <<IS-END>> ---

                
	}



	public static final void registerProcessor (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerProcessor)>> ---
		// @sigtype java 3.5
		ClusterDispatcherProcessor processor = ClusterDispatcherProcessor.getInstance();
		InvokeManager.getDefault().registerProcessor(processor);
		// --- <<IS-END>> ---

                
	}



	public static final void registerServiceDispatch (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(registerServiceDispatch)>> ---
		// @sigtype java 3.5
		IDataMap pipelineMap = new IDataMap(pipeline);
		String targetService = pipelineMap.getAsString("targetService");
		String dispatchMode = pipelineMap.getAsString("dispatchMode");
		String topServiceOnly = pipelineMap.getAsString("topServiceOnly");
		String loggingService = pipelineMap.getAsString("loggingService");
		String logPipeline = pipelineMap.getAsString("logPipeline");
		String dispatchFlagField = pipelineMap.getAsString("dispatchFlagField");
		String failureSignalExpression = pipelineMap.getAsString("failureSignalExpression");
		String rollbackService = pipelineMap.getAsString("rollbackService");
		String rollbackFailureSignalExpression = pipelineMap.getAsString("rollbackFailureSignalExpression");
		String enabled = pipelineMap.getAsString("enabled");
		
		boolean bTopServiceOnly = (topServiceOnly != null && topServiceOnly.equalsIgnoreCase("true")) ? true : false;
		boolean bLogPipeline = (logPipeline != null && logPipeline.equals("true")) ? true : false;
		boolean bEnabled =  (enabled != null && enabled.equals("true")) ? true : false;
		
		ClusterDispatcherProcessor.registerServiceDispatch(targetService, dispatchMode, bTopServiceOnly, loggingService, bLogPipeline, dispatchFlagField, failureSignalExpression, rollbackService, rollbackFailureSignalExpression, bEnabled);
		// --- <<IS-END>> ---

                
	}



	public static final void setClusterNodes (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setClusterNodes)>> ---
		// @sigtype java 3.5
		// [i] field:1:required clusterNodes
		IDataMap pipelineMap = new IDataMap(pipeline);
		String[] clusterNodes = pipelineMap.getAsStringArray("clusterNodes");
		ClusterDispatcherProcessor.setClusterNodes(clusterNodes);
		// --- <<IS-END>> ---

                
	}



	public static final void unregisterProcessor (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(unregisterProcessor)>> ---
		// @sigtype java 3.5
		ClusterDispatcherProcessor processor = ClusterDispatcherProcessor.getInstance();
		InvokeManager.getDefault().unregisterProcessor(processor);
		// --- <<IS-END>> ---

                
	}



	public static final void unregisterServiceDispatch (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(unregisterServiceDispatch)>> ---
		// @sigtype java 3.5
		// [i] field:0:required targetService
		IDataMap pipelineMap = new IDataMap(pipeline);
		String targetService = pipelineMap.getAsString("targetService");
		
		if (targetService != null){
			ClusterDispatcherProcessor.unregisterServiceDispatch(targetService);
		}
		// --- <<IS-END>> ---

                
	}
}

