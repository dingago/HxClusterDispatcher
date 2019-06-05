<HTML>
<HEAD>
	<TITLE>QtLogging Package</TITLE>
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
	<META HTTP-EQUIV="Expires" CONTENT="-1">
	<LINK REL="stylesheet" TYPE="text/css" HREF="/WmRoot/webMethods.css">
	<SCRIPT SRC="/WmRoot/webMethods.js.txt"></SCRIPT>
</HEAD>
<BODY>
<TABLE width="100%">
<TR>
	<TD class="menusection-Settings" colspan="2">
	HxClusterDispatcher &gt;
	Service Dispatch List </TD>
</TR>
<TR>
	<TD colspan="2">
		<UL>
			<LI><A HREF="service-dispatch-list.dsp">Refresh</A></LI>
			<LI><A HREF="service-dispatch-detail.dsp?mode=new">Create</A></LI>
			<LI><A HREF="service-dispatch-list.dsp?action=check">Self-Check</A></LI>
		</UL>
	</TD>
</TR>

%ifvar action%
%switch action%
%case 'new'%
	%invoke HxClusterDispatcher.ui:saveServiceDispatch%
		%ifvar messages%
			<TR><TD class="message" colspan="2">%value%
			%loop messages%
			%value%<BR>
			%endloop%
			</TD></TR>
		%endif%
	%endinvoke%
%case 'edit'%
	%invoke HxClusterDispatcher.ui:saveServiceDispatch%
		%ifvar messages%
			<TR><TD class="message" colspan="2">%value%
			%loop messages%
			%value%<BR>
			%endloop%
			</TD></TR>
		%endif%
	%endinvoke%
%case 'remove'%
	%invoke HxClusterDispatcher.ui:deleteServiceDispatch%
		%ifvar messages%
			<TR><TD class="message" colspan="2">%value%
			%loop messages%
			%value%<BR>
			%endloop%
			</TD></TR>
		%endif%
	%endinvoke%
%case 'enable'%
	%invoke HxClusterDispatcher.ui:enableServiceDispatch%
		%ifvar messages%
			<TR><TD class="message" colspan="2">%value%
			%loop messages%
			%value%<BR>
			%endloop%
			</TD></TR>
		%endif%
	%endinvoke%
%case 'disable'%
	%invoke HxClusterDispatcher.ui:disableServiceDispatch%
		%ifvar messages%
			<TR><TD class="message" colspan="2">%value%
			%loop messages%
			%value%<BR>
			%endloop%
			</TD></TR>
		%endif%
	%endinvoke%
%case 'check'%
	%invoke HxClusterDispatcher.ui:selfCheck%
		%ifvar messages%
			<TR><TD class="message" colspan="2">%value%
			%loop messages%
			%value%<BR>
			%endloop%
			</TD></TR>
		%endif%
	%endinvoke%
%endswitch%
%endif%

<TR>
	<TD><IMG SRC="/WmRoot/images/blank.gif" height=10 width=10></TD>
	
	<TD>
	<TABLE class="tableView" width=100%>
	<TR>
		<TD class="heading" colspan=5>Service Dispatch List</TD>
	</TR>
	%invoke HxClusterDispatcher.ui:getServiceDispatches%
	<TR>
		<TD class="oddcol">Target Service</TD>
		<TD class="oddcol">Dispatch Mode</TD>
		<TD class="oddcol">Top Service Only</TD>
		<TD class="oddcol">Enabled</TD>
		<TD class="oddcol">Action</TD>
	</TR>
	%loop serviceDispatches%
	<TR>
		<script>writeTD("rowdata");</script>%value targetService%</TD>
		<script>writeTD("rowdata");</script>%value dispatchMode%</TD>
		<script>writeTD("rowdata");</script>%value topServiceOnly%</TD>
		<script>writeTD("rowdata");</script>
			%ifvar enabled equals('true')%
			<A class="submodal" href="service-dispatch-list.dsp?targetService=%value -urlencode targetService%&action=disable"
				onclick="return confirm('OK to disable service dispatch for %value targetService%?')">true</A>
			%else%
			<A class="submodal" href="service-dispatch-list.dsp?targetService=%value -urlencode targetService%&action=enable"
				onclick="return confirm('OK to enable service dispatch for %value targetService%?')">false</A>
			%end%
		</TD>
		<script>writeTD("rowdata");</script>
			<A class="submodal" href="service-dispatch-detail.dsp?targetService=%value -urlencode targetService%&mode=view"><IMG border=0 align="bottom" alt="view" src="/WmRoot/icons/file.gif"/></A>
			<A class="submodal" href="service-dispatch-detail.dsp?targetService=%value -urlencode targetService%&mode=edit"><IMG border=0 align="bottom" alt="edit" src="/WmRoot/icons/copy.gif"/></A>
			<A class="submodal" href="service-dispatch-list.dsp?targetService=%value -urlencode targetService%&action=remove"
				onclick="return confirm('OK to remove service dispatch for %value targetService%?')"><IMG border=0 align="bottom" alt="remove" src="/WmRoot/icons/delete.gif"/></A>
		</TD>
	</TR>
	<script>swapRows();</script>
	%endloop%
	%endinvoke%
	</TABLE>
	</TD>
</TR>
</TABLE>
</BODY>
</HTML>
