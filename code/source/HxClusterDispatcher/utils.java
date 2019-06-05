package HxClusterDispatcher;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2019-06-04 22:49:27 MDT
// -----( ON-HOST: 192.168.241.223

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;
import com.wm.app.b2b.server.*;
import com.wm.lang.ns.*;
// --- <<IS-END-IMPORTS>> ---

public final class utils

{
	// ---( internal utility methods )---

	final static utils _instance = new utils();

	static utils _newInstance() { return new utils(); }

	static utils _cast(Object o) { return (utils)o; }

	// ---( server methods )---




	public static final void copyFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(copyFile)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required sourceFilename
		// [i] field:0:required targetFilename
		// [i] field:0:optional overwrite {"true","false"}
		IDataMap pipelineMap = new IDataMap(pipeline);
		String	sourceFilename = pipelineMap.getAsString("sourceFilename" );
		String	targetFilename = pipelineMap.getAsString("targetFilename" );
		String	overwrite = pipelineMap.getAsString("overwrite" );
				
		File sourceFile = new File(sourceFilename);
		File targetFile = new File(targetFilename);
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		if (sourceFile.exists() && ("true".equals(overwrite) || (!targetFile.exists()))){
			try{
				inStream = new FileInputStream(sourceFile);
				outStream = new FileOutputStream(targetFilename, false);
				byte[] buffer = new byte[4096];
				int n = 0;
				while (-1 != (n = inStream.read(buffer))){
					outStream.write(buffer, 0, n);
				}
			}catch(IOException e){
				throw new ServiceException (e);
			}finally{
				if (inStream != null){
					try {
						inStream.close();
					} catch (IOException e) {}
				}
				if (outStream != null){
					try {
						outStream.close();
					} catch (IOException e) {}
				}
			}
		}
		// --- <<IS-END>> ---

                
	}



	public static final void deleteFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(deleteFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [o] field:0:required result
		IDataMap pipelineMap = new IDataMap(pipeline);
		String	filename = pipelineMap.getAsString("filename" );
		
		pipelineMap.put("result", String.valueOf(new File(filename).delete()));
		// --- <<IS-END>> ---

                
	}



	public static final void fileExists (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(fileExists)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [o] field:0:required fileExists
		IDataMap pipelineMap = new IDataMap(pipeline);
		String	filename = pipelineMap.getAsString("filename" );
		
		pipelineMap.put( "fileExists", String.valueOf(new File(filename).exists()));
		// --- <<IS-END>> ---

                
	}



	public static final void filename2ServiceName (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(filename2ServiceName)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [o] field:0:required serviceName
		IDataMap pipelineMap = new IDataMap(pipeline);
		String filename = pipelineMap.getAsString("filename");
		
		String serviceName = filename.substring(0, filename.lastIndexOf('.'));
		char[]  chars = serviceName.toCharArray();
		chars[serviceName.lastIndexOf('.')] = ':';
		serviceName = new String(chars);
		pipelineMap.put("serviceName", serviceName);
			
		// --- <<IS-END>> ---

                
	}



	public static final void getAliasList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getAliasList)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] field:1:required aliases
		IDataMap pipelineMap = new IDataMap(pipeline);
		
		String[] aliases = new String[0];
		String aliasList = System.getProperty("watt.server.cluster.aliasList", null);
		if ((aliasList != null) && (aliasList.length() > 0)) {
			StringTokenizer st = new StringTokenizer(aliasList, ",");
			aliases = new String[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()) {
				aliases[(i++)] = st.nextToken().trim();
			}
		}
		
		pipelineMap.put("aliases", aliases );
		// --- <<IS-END>> ---

                
	}



	public static final void getOtherNodes (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getOtherNodes)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required hostname
		// [i] field:1:required aliases
		// [o] field:1:required otherNodes
		IDataMap pipelineMap = new IDataMap(pipeline);
		String	hostname = pipelineMap.getAsString("hostname" );
		String[]	aliases = pipelineMap.getAsStringArray("aliases" );
		
		ArrayList<String> otherNodes = new ArrayList<String>();
		for (String alias : aliases){
			if (!alias.equals(hostname)){
				otherNodes.add(alias);
			}
		}
		
		pipelineMap.put("otherNodes", otherNodes.toArray(new String[otherNodes.size()]) );
		// --- <<IS-END>> ---

                
	}



	public static final void getPackagesDir (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPackagesDir)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] field:0:required packagesDir
		IDataMap pipelineMap = new IDataMap(pipeline);
		
		String packagesDir;
		try{
			packagesDir = Server.getResources().getPackagesDir().getCanonicalPath();
		}catch(IOException e){
			packagesDir = Server.getResources().getPackagesDir().getAbsolutePath();
		}
		
		pipelineMap.put("packagesDir", packagesDir );
		// --- <<IS-END>> ---

                
	}



	public static final void getSeparator (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getSeparator)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [o] field:0:required separator
		IDataMap pipelineMap = new IDataMap(pipeline);
		pipelineMap.put("separator", File.separator);
			
		// --- <<IS-END>> ---

                
	}



	public static final void listFiles (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(listFiles)>> ---
		// @sigtype java 3.5
		// [i] field:0:required directory
		// [i] field:0:optional filter
		// [o] field:1:required fileList
		// [o] field:0:required numFiles
		IDataMap pipelineMap = new IDataMap(pipeline);
		String directory = pipelineMap.getAsString("directory");
		String filter = pipelineMap.getAsString("filter");
		
		File directoryFile = new File(directory);
		String[] fileList = new String[0];
		if (directoryFile.exists() && directoryFile.isDirectory()){
			if (filter != null){
				FilenameFilter filenameFilter = new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						boolean accept = false;
						StringBuffer regexPattern = new StringBuffer("^");
						for(int i = 0; i < filter.length(); i++){
							char c = filter.charAt(i);
							switch(c){
								case '*': regexPattern.append(".*"); break;
								case '?': regexPattern.append("."); break;
								case '.': regexPattern.append("\\."); break;
								case '\\': regexPattern.append("\\\\"); break;
								default: regexPattern.append(c);
							}
						}
						regexPattern.append("$");
						accept = Pattern.matches(regexPattern.toString(), name);
						return accept;
					}
				};
				fileList = directoryFile.list(filenameFilter);
			}else{
				fileList = directoryFile.list();
			}
		}
		
		pipelineMap.put("fileList", fileList);
		pipelineMap.put("numFiles", fileList.length);
		// --- <<IS-END>> ---

                
	}



	public static final void obj2IData (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(obj2IData)>> ---
		// @sigtype java 3.5
		// [i] object:0:required object
		// [o] record:0:required idata
		IDataMap pipelineMap = new IDataMap(pipeline);
		Object object = pipelineMap.get("object");
		
		IData idata = null;
		if (object != null){
			if (object instanceof IData){
				idata = (IData)object;
			}else if (object instanceof IDataPortable){
				idata = ((IDataPortable)object).getAsData();
			}
		}
		
		pipelineMap.put("idata", idata);
			
		// --- <<IS-END>> ---

                
	}



	public static final void serviceName2Filename (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(serviceName2Filename)>> ---
		// @sigtype java 3.5
		// [i] field:0:required serviceName
		// [o] field:0:required filename
		IDataMap pipelineMap = new IDataMap(pipeline);
		String serviceName = pipelineMap.getAsString("serviceName");
		
		String filename = serviceName.replace(':', '.') + ".xml";
		pipelineMap.put("filename", filename);
		// --- <<IS-END>> ---

                
	}



	public static final void stringToFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(stringToFile)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:required data
		// [i] field:0:required append {"false","true"}
		// [i] field:0:optional encoding
		IDataMap pipelineMap = new IDataMap(pipeline);
		String	filename = pipelineMap.getAsString("filename");
		String	data = pipelineMap.getAsString("data");
		String	append = pipelineMap.getAsString("append");
		String	encoding = pipelineMap.getAsString("encoding");
		
		Reader reader = null;
		Writer writer = null;
		try {
			reader = new StringReader(data);
			if (encoding != null) {
				writer = new OutputStreamWriter(new FileOutputStream(new File(filename), Boolean.valueOf(append)), encoding);
			} else {
				writer = new FileWriter(new File(filename), Boolean.valueOf(append));
			}
			char[] buffer = new char[4096];
			int n = 0;
			while (-1 != (n = reader.read(buffer))){
			      writer.write(buffer, 0, n);
			}
		}  catch (IOException e) {
			throw new ServiceException (e);
		}finally{
			if (writer != null) {
				try{
					writer.close();
				}catch (IOException e){}
			}
			if (reader != null) {
				try{
					reader.close();
				}catch (IOException e){}
			}
		}
		// --- <<IS-END>> ---

                
	}
}

