package tictactoe;

import com.thetransactioncompany.jsonrpc2.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.IOUtils;

public class BotTester {
	
	public static String JSONRPC2RequestString(String method, Map<String,Object> params, int id) {
		// Create a new JSON-RPC 2.0 request
		JSONRPC2Request reqOut = new JSONRPC2Request(method, params, id);

		// Serialize the request to a JSON-encoded string
		return reqOut.toString();
	}
	
	public static String getResponse(String urlEndpoint, String requestString) throws IOException {
		URL url = new URL(urlEndpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.connect();
		
		OutputStream out = null;
		InputStream response = null;
		try {
		    out = connection.getOutputStream();
		    
		    out.write(requestString.getBytes());
		    out.flush();
		    out.close();

		    int statusCode = connection.getResponseCode();
		    System.out.println(statusCode);
		    
		    response = connection.getInputStream();
		    StringWriter writer = new StringWriter();
			IOUtils.copy(response, writer);
			String requestBody = writer.toString();
			
			response.close();
			
			return requestBody;
		    
		} finally {
		    if (out != null) {
		        out.close();
		    }
		    if (response != null) {
		    	response.close();
		    }
		}
	}
	
	public static String ping() {
		String method = "Status.Ping";
		int id = 1;
		
		Map<String,Object> params = new HashMap<String,Object>();
		
		return BotTester.JSONRPC2RequestString(method, params, id);
	}
	
	public static String nextMove() {
		String method = "TicTacToe.NextMove";
		int id = 1;
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gameid", 78);
		params.put("mark", "X");
		
		String board[] = new String[]{"O", "X", "O", null, "O", null, null, "X", null};
		ArrayList<String> gamestate = new ArrayList<String>();
		for (String s : board) {
			gamestate.add(s);
		}
		params.put("gamestate", gamestate);
		
		return BotTester.JSONRPC2RequestString(method, params, id);
	}
	
	public static String complete() {
		String method = "TicTacToe.Complete";
		int id = 1;
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gameid", 21);
		params.put("winner", true);
		params.put("mark", "X");
		
		String board[] = new String[]{"O", "X", "O", null, "O", "X", "O", "X", null};
		ArrayList<String> gamestate = new ArrayList<String>();
		for (String s : board) {
			gamestate.add(s);
		}
		params.put("gamestate", gamestate);
		
		return BotTester.JSONRPC2RequestString(method, params, id);
	}
	
	public static String error() {
		String method = "TicTacToe.Error";
		int id = 1;
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gameid", 21);
		params.put("message", "You played an invalid move, 'X' was already played here.");
		params.put("errorcode", 1548);
		
		return BotTester.JSONRPC2RequestString(method, params, id);
	}
	
	public static void main(String args[]) throws IOException {
		
		String urlEndpoint = "http://720b896b.ngrok.io/bot";
		//String requestString = BotTester.ping();
		String requestString = BotTester.nextMove();
		//String requestString = BotTester.complete();
		//String requestString = BotTester.error();

		System.out.println(requestString);
		
		String responseString = BotTester.getResponse(urlEndpoint, requestString);
		
		System.out.println(responseString);
		
	}

}
