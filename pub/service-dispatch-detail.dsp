<HTML>
<HEAD>
	<TITLE>QtLogging Package</TITLE>
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
	<META HTTP-EQUIV="Expires" CONTENT="-1">
	<LINK REL="stylesheet" TYPE="text/css" HREF="/WmRoot/webMethods.css">
	<SCRIPT SRC="/WmRoot/webMethods.js.txt"></SCRIPT>
	<SCRIPT LANGUAGE="JavaScript">
		function validateForm(mode){
			if (!verifyRequiredField('form', 'targetService')){
				alert("Target Service is required");
				return false;
			}
			return true;
		}
	</SCRIPT>
</HEAD>
<BODY>
<FORM id="form" action="service-dispatch-list.dsp" method="POST" onSubmit="return validateForm('%value mode%')">
<TABLE width="100%">
<TR>
	<TD class="menusection-Settings" colspan="2">
	HxClusterDispatcher &gt;
	%ifvar mode equals('view')%
	View Service Dispatch</TD>
	%endif%
	%ifvar mode equals('edit')%
	Update Service Dispatch</TD>
	%endif%
	%ifvar mode equals('new')%
	Create Service Dispatch</TD>
	%endif%
</TR>
<TR>
	<TD colspan="2">
		<UL>
			<LI><A HREF="service-dispatch-list.dsp">Return</A></LI>
			%ifvar mode equals('view')%
			<LI><A HREF="service-dispatch-detail.dsp?targetService=%value targetService%&mode=edit">Edit</A></LI>
			%endif%
		</UL>
	</TD>
</TR>

%ifvar mode%
%switch mode%
%case 'view'%
	%invoke HxClusterDispatcher.ui:getServiceDispatch%
	%endinvoke%
%case 'edit'%
	%invoke HxClusterDispatcher.ui:getServiceDispatch%
	%endinvoke%
%endswitch%
%endif%
<TR>
	<TD><IMG SRC="/WmRoot/images/blank.gif" height=10 width=10></TD>
	
	<TD>
	<TABLE class="tableView" width=100%>
	<TR>
		<TD class="heading" colspan=2>Basic</TD>
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Target Service*</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/targetService%</TD>
		%endif%
		%ifvar mode equals('edit')%
		%value serviceDispatch/targetService%</TD>
		<input type="hidden" name="targetService" value="%value serviceDispatch/targetService%"></input>
		%endif%
		%ifvar mode equals('new')%
		<input type="text" name="targetService" style="width:400" value=""/></TD>
		%endif%
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Dispatch Mode*</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/dispatchMode%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<select name="dispatchMode">
			<option value="STRICT"%ifvar serviceDispatch/dispatchMode equals('STRICT')% selected="selected"%endif%>STRICT</option>
			<option value="LAX"%ifvar serviceDispatch/dispatchMode equals('LAX')% selected="selected"%endif%>LAX</option>
		</select></TD>
		%endif%
		%ifvar mode equals('new')%
		<select name="dispatchMode">
			<option value="STRICT">STRICT</option>
			<option value="LAX" selected="selected">LAX</option>
		</select></TD>
		%endif%
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Top Service Only*</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/topServiceOnly%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<select name="topServiceOnly">
			<option value="true"%ifvar serviceDispatch/topServiceOnly equals('true')% selected="selected"%endif%>true</option>
			<option value="false"%ifvar serviceDispatch/topServiceOnly equals('false')% selected="selected"%endif%>false</option>
		</select></TD>
		%endif%
		%ifvar mode equals('new')%
		<select name="topServiceOnly">
			<option value="true">true</option>
			<option value="false" selected="selected">false</option>
		</select></TD>
		%endif%
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Enabled*</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/enabled%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<select name="enabled">
			<option value="true"%ifvar serviceDispatch/enabled equals('true')% selected="selected"%endif%>true</option>
			<option value="false"%ifvar serviceDispatch/enabled equals('false')% selected="selected"%endif%>false</option>
		</select></TD>
		%endif%
		%ifvar mode equals('new')%
		<select name="enabled">
			<option value="true" selected="selected">true</option>
			<option value="false">false</option>
		</select></TD>
		%endif%
	</TR>
	<TR>
		<TD class="heading" colspan=2>Addtional</TD>
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Dispatch Flag Field</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/dispatchFlagField%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<input type="text" name="dispatchFlagField" style="width:400" value="%value serviceDispatch/dispatchFlagField%"/></TD>
		%endif%
		%ifvar mode equals('new')%
		<input type="text" name="dispatchFlagField" style="width:400" value="$clusterDispatchingInitiator"/></TD>
		%endif%
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Failure Signal Expression</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/failureSignalExpression%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<input type="text" name="failureSignalExpression" style="width:400" value="%value serviceDispatch/failureSignalExpression%"/></TD>
		%endif%
		%ifvar mode equals('new')%
		<input type="text" name="failureSignalExpression" style="width:400" value=""/></TD>
		%endif%
	</TR>
	<TR>
		<TD class="heading" colspan=2>Logging</TD>
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Logigng Service</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/loggingService%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<input type="text" name="loggingService" style="width:400" value="%value serviceDispatch/loggingService%"/></TD>
		%endif%
		%ifvar mode equals('new')%
		<input type="text" name="loggingService" style="width:400" value="pub.flow:debugLog"/></TD>
		%endif%
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Log Pipeline</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/logPipeline%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<select name="logPipeline">
			<option value="true"%ifvar serviceDispatch/logPipeline equals('true')% selected="selected"%endif%>true</option>
			<option value="false"%ifvar serviceDispatch/logPipeline equals('false')% selected="selected"%endif%>false</option>
		</select></TD>
		%endif%
		%ifvar mode equals('new')%
		<select name="logPipeline">
			<option value="true">true</option>
			<option value="false" selected="selected">false</option>
		</select></TD>
		%endif%
	</TR>
	<TR>
		<TD class="heading" colspan=2>Rollback</TD>
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Rollback Service</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/rollbackService%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<input type="text" name="rollbackService" style="width:400" value="%value serviceDispatch/rollbackService%"/></TD>
		%endif%
		%ifvar mode equals('new')%
		<input type="text" name="rollbackService" style="width:400" value=""/></TD>
		%endif%
	</TR>
	<TR>
		<script>writeTDWidth('row','50%');</script>Rollback Failure Signal Expression</TD>
		<script>writeTDWidth('rowdata-l','50%');swapRows();</script>
		%ifvar mode equals('view')%
		%value serviceDispatch/rollbackFailureSignalExpression%</TD>
		%endif%
		%ifvar mode equals('edit')%
		<input type="text" name="rollbackFailureSignalExpression" style="width:400" value="%value serviceDispatch/rollbackFailureSignalExpression%"/></TD>
		%endif%
		%ifvar mode equals('new')%
		<input type="text" name="rollbackFailureSignalExpression" style="width:400" value=""/></TD>
		%endif%
	</TR>
	%ifvar mode equals('new')%
	<TR>
		<TD class="action" colspan="2">
			<input type="submit" name="SUBMIT" value="Create" width=100></input>
			<input type="hidden" name="action" value="new"></input>
		</TD>
	</TR>
	%endif%
	%ifvar mode equals('edit')%
	<TR>
		<TD class="action" colspan="2">
			<input type="submit" name="SUBMIT" value="Update" width=100></input>
			<input type="hidden" name="action" value="edit"></input>
		</TD>
	</TR>
	%endif%
	</TABLE>
	</TD>
</TR>
</TABLE>
</FORM>
</BODY>
</HTML>
