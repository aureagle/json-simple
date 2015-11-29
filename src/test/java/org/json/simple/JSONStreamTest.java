package org.json.simple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;

public class JSONStreamTest {

	private static class DelayedChunkWriter implements Runnable {

		PrintWriter writer;
		String msg;
		long delay;
		int chunkSize;

		public DelayedChunkWriter(PrintWriter writer, String msg, 
				long delay,
				int chunkSize) {
			this.delay = delay;
			this.writer = writer;
			this.msg = msg;
			this.chunkSize = chunkSize;
		}

		@Override
		public void run() {
			// sleep for delay

			int l = 0;
			while (l < msg.length()) {

				// write chunk
				int chunkLength = Math.min(chunkSize, msg.length()-l);
				String chunk = msg.substring(l, l+chunkLength);

				// delay
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
				}

				// write chunk
				writer.print(chunk);
				writer.flush();

				l += chunkLength;
			}
		}

	}

	private static class SocketConnector implements Callable<Socket> {

		ServerSocket server;

		public SocketConnector(ServerSocket server) {
			this.server = server;
		}

		@Override
		public Socket call() throws Exception {
			return server.accept();
		}

	}
	
	public static String getJSONSampleMedium() {
		String json = "{\"widget\": {\n"+
				"    \"debug\": \"on\",\n"+
				"    \"window\": {\n"+
				"        \"title\": \"Sample Konfabulator Widget\",\n"+
				"        \"name\": \"main_window\",\n"+
				"        \"width\": 500,\n"+
				"        \"height\": 500\n"+
				"    },\n"+
				"    \"image\": { \n"+
				"        \"src\": \"Images/Sun.png\",\n"+
				"        \"name\": \"sun1\",\n"+
				"        \"hOffset\": 250,\n"+
				"        \"vOffset\": 250,\n"+
				"        \"alignment\": \"center\"\n"+
				"    },\n"+
				"    \"text\": {\n"+
				"        \"data\": \"Click Here\",\n"+
				"        \"size\": 36,\n"+
				"        \"style\": \"bold\",\n"+
				"        \"name\": \"text1\",\n"+
				"        \"hOffset\": 250,\n"+
				"        \"vOffset\": 100,\n"+
				"        \"alignment\": \"center\",\n"+
				"        \"onMouseUp\": \"sun1.opacity = (sun1.opacity / 100) * 90;\"\n"+
				"    }\n"+
				"}} \n";
		return json;
	}
	
	public static String getJSONSampleLong() {
		String json = "{\"web-app\": {\n"+
				"  \"servlet\": [   \n"+
				"    {\n"+
				"      \"servlet-name\": \"cofaxCDS\",\n"+
				"      \"servlet-class\": \"org.cofax.cds.CDSServlet\",\n"+
				"      \"init-param\": {\n"+
				"        \"configGlossary:installationAt\": \"Philadelphia, PA\",\n"+
				"        \"configGlossary:adminEmail\": \"ksm@pobox.com\",\n"+
				"        \"configGlossary:poweredBy\": \"Cofax\",\n"+
				"        \"configGlossary:poweredByIcon\": \"/images/cofax.gif\",\n"+
				"        \"configGlossary:staticPath\": \"/content/static\",\n"+
				"        \"templateProcessorClass\": \"org.cofax.WysiwygTemplate\",\n"+
				"        \"templateLoaderClass\": \"org.cofax.FilesTemplateLoader\",\n"+
				"        \"templatePath\": \"templates\",\n"+
				"        \"templateOverridePath\": \"\",\n"+
				"        \"defaultListTemplate\": \"listTemplate.htm\",\n"+
				"        \"defaultFileTemplate\": \"articleTemplate.htm\",\n"+
				"        \"useJSP\": false,\n"+
				"        \"jspListTemplate\": \"listTemplate.jsp\",\n"+
				"        \"jspFileTemplate\": \"articleTemplate.jsp\",\n"+
				"        \"cachePackageTagsTrack\": 200,\n"+
				"        \"cachePackageTagsStore\": 200,\n"+
				"        \"cachePackageTagsRefresh\": 60,\n"+
				"        \"cacheTemplatesTrack\": 100,\n"+
				"        \"cacheTemplatesStore\": 50,\n"+
				"        \"cacheTemplatesRefresh\": 15,\n"+
				"        \"cachePagesTrack\": 200,\n"+
				"        \"cachePagesStore\": 100,\n"+
				"        \"cachePagesRefresh\": 10,\n"+
				"        \"cachePagesDirtyRead\": 10,\n"+
				"        \"searchEngineListTemplate\": \"forSearchEnginesList.htm\",\n"+
				"        \"searchEngineFileTemplate\": \"forSearchEngines.htm\",\n"+
				"        \"searchEngineRobotsDb\": \"WEB-INF/robots.db\",\n"+
				"        \"useDataStore\": true,\n"+
				"        \"dataStoreClass\": \"org.cofax.SqlDataStore\",\n"+
				"        \"redirectionClass\": \"org.cofax.SqlRedirection\",\n"+
				"        \"dataStoreName\": \"cofax\",\n"+
				"        \"dataStoreDriver\": \"com.microsoft.jdbc.sqlserver.SQLServerDriver\",\n"+
				"        \"dataStoreUrl\": \"jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon\",\n"+
				"        \"dataStoreUser\": \"sa\",\n"+
				"        \"dataStorePassword\": \"dataStoreTestQuery\",\n"+
				"        \"dataStoreTestQuery\": \"SET NOCOUNT ON;select test='test';\",\n"+
				"        \"dataStoreLogFile\": \"/usr/local/tomcat/logs/datastore.log\",\n"+
				"        \"dataStoreInitConns\": 10,\n"+
				"        \"dataStoreMaxConns\": 100,\n"+
				"        \"dataStoreConnUsageLimit\": 100,\n"+
				"        \"dataStoreLogLevel\": \"debug\",\n"+
				"        \"maxUrlLength\": 500}},\n"+
				"    {\n"+
				"      \"servlet-name\": \"cofaxEmail\",\n"+
				"      \"servlet-class\": \"org.cofax.cds.EmailServlet\",\n"+
				"      \"init-param\": {\n"+
				"      \"mailHost\": \"mail1\",\n"+
				"      \"mailHostOverride\": \"mail2\"}},\n"+
				"    {\n"+
				"      \"servlet-name\": \"cofaxAdmin\",\n"+
				"      \"servlet-class\": \"org.cofax.cds.AdminServlet\"},\n"+
				" \n"+
				"    {\n"+
				"      \"servlet-name\": \"fileServlet\",\n"+
				"      \"servlet-class\": \"org.cofax.cds.FileServlet\"},\n"+
				"    {\n"+
				"      \"servlet-name\": \"cofaxTools\",\n"+
				"      \"servlet-class\": \"org.cofax.cms.CofaxToolsServlet\",\n"+
				"      \"init-param\": {\n"+
				"        \"templatePath\": \"toolstemplates/\",\n"+
				"        \"log\": 1,\n"+
				"        \"logLocation\": \"/usr/local/tomcat/logs/CofaxTools.log\",\n"+
				"        \"logMaxSize\": \"\",\n"+
				"        \"dataLog\": 1,\n"+
				"        \"dataLogLocation\": \"/usr/local/tomcat/logs/dataLog.log\",\n"+
				"        \"dataLogMaxSize\": \"\",\n"+
				"        \"removePageCache\": \"/content/admin/remove?cache=pages&id=\",\n"+
				"        \"removeTemplateCache\": \"/content/admin/remove?cache=templates&id=\",\n"+
				"        \"fileTransferFolder\": \"/usr/local/tomcat/webapps/content/fileTransferFolder\",\n"+
				"        \"lookInContext\": 1,\n"+
				"        \"adminGroupID\": 4,\n"+
				"        \"betaServer\": true}}],\n"+
				"  \"servlet-mapping\": {\n"+
				"    \"cofaxCDS\": \"/\",\n"+
				"    \"cofaxEmail\": \"/cofaxutil/aemail/*\",\n"+
				"    \"cofaxAdmin\": \"/admin/*\",\n"+
				"    \"fileServlet\": \"/static/*\",\n"+
				"    \"cofaxTools\": \"/tools/*\"},\n"+
				" \n"+
				"  \"taglib\": {\n"+
				"    \"taglib-uri\": \"cofax.tld\",\n"+
				"    \"taglib-location\": \"/WEB-INF/tlds/cofax.tld\"}}}\n";
		return json;
	}
	
	@Test
	public void testJSONStreamer() throws Exception {

		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();

		Socket rsocket;
		Socket wsocket;

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Socket> ss = executor.submit(new SocketConnector(server));

		wsocket = new Socket("localhost", port);
		rsocket = ss.get();

		// send message locally 
		BufferedReader reader = new BufferedReader(new InputStreamReader(rsocket.getInputStream()));
		PrintWriter writer = new PrintWriter(wsocket.getOutputStream());

		String jsonSample = getJSONSampleMedium();

		DelayedChunkWriter chunkWriter = new DelayedChunkWriter(writer, jsonSample, 10, 10);
		executor.execute(chunkWriter);

		JSONParser parser = new JSONParser();
		// entire string at once
		JSONObject reference = (JSONObject)parser.parse(jsonSample);
		// from streaming input
		JSONObject obj = (JSONObject)(parser.parse(reader));
		
		Assert.assertEquals("Parsed JSON equal", reference, obj);

		// close communication
		rsocket.close();
		wsocket.close();
		server.close();
	}
	
	@Test
	public void testJSONMultipleStreamer() throws Exception {

		ServerSocket server = new ServerSocket(0);
		int port = server.getLocalPort();

		Socket rsocket;
		Socket wsocket;

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Socket> ss = executor.submit(new SocketConnector(server));

		wsocket = new Socket("localhost", port);
		rsocket = ss.get();

		// send message locally 
		BufferedReader reader = new BufferedReader(new InputStreamReader(rsocket.getInputStream()));
		PrintWriter writer = new PrintWriter(wsocket.getOutputStream());

		String jsonFirst = getJSONSampleMedium();
		String jsonSecond = getJSONSampleLong();
		
		String jsonSample = jsonFirst + "\n" + jsonSecond;
		DelayedChunkWriter chunkWriter = new DelayedChunkWriter(writer, jsonSample, 10, 10);
		executor.execute(chunkWriter);

		JSONParser parser = new JSONParser();
		// entire string at once
		JSONObject reference = (JSONObject)(parser.parse(jsonFirst));
		// from streaming input
		JSONObject obj = (JSONObject)(parser.parse(reader));
		
		Assert.assertEquals("First parsed JSON equal", reference, obj);
		
		// second streamed object

		// entire string at once
		reference = (JSONObject)(parser.parse(jsonSecond));
		// from streaming input
		obj = (JSONObject)(parser.parse(reader));

		
		Assert.assertEquals("Second parsed JSON equal", reference, obj);
		
		// close communication
		rsocket.close();
		wsocket.close();
		server.close();
	}

}
