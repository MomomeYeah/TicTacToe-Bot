package tictactoe;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Message;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.minidev.json.JSONArray;

@SuppressWarnings("restriction")
public class BotHandler implements HttpHandler {
	
	private class Response {
		private boolean respond;
		private String responseString;
		
		public Response(boolean respond) {
			this.respond = respond;
		}
		
		public void setResponseString(String responseString) {
			this.responseString = responseString;
		}
		
		public boolean getRespond() {
			return this.respond;
		}
		
		public String getResponseString() {
			return this.responseString;
		}
	}
	
	public Object handleStatusPing(JSONRPC2Request reqIn) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ping", "OK");
		
		return params;
	}
	
	public Object handleTicTacToeNextMove(JSONRPC2Request reqIn) {
		Map<String,Object> namedParams = reqIn.getNamedParams();
		Side side = ((String) namedParams.get("mark")).equals("X") ? Side.CROSS : Side.NOUGHT;
		
		JSONArray ja = (JSONArray) namedParams.get("gamestate");
		ArrayList<String> gameState = new ArrayList<String>();
		for (int i = 0; i < ja.size(); i++) {
			gameState.add((String) ja.get(i));
		}
		
		Solver solver = new Solver(side, gameState);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("position", solver.getMove());
		
		return params;
	}
	
	public Object handleTicTacToeComplete(JSONRPC2Request reqIn) {
		return null;
	}
	
	public Object handleTicTacToeError(JSONRPC2Request reqIn) {
		return null;
	}
	
	public Response parseRequest(String requestString) {
		JSONRPC2Response respOut = null;
		Response response = null;
		
		JSONRPC2Message msgIn = null;
		JSONRPC2Request reqIn = null;
		JSONRPC2Notification notIn = null;
		try {
			msgIn = JSONRPC2Message.parse(requestString);
		} catch (JSONRPC2ParseException e) {
			response = new Response(true);
			response.setResponseString(e.getMessage());
		}
		
		boolean isRequest = false;
		String method = null;
		if (msgIn instanceof JSONRPC2Notification) {
			response = new Response(false);
			notIn = (JSONRPC2Notification) msgIn;
			method = notIn.getMethod();
		} else if (msgIn instanceof JSONRPC2Request) {
			response = new Response(true);
			reqIn = (JSONRPC2Request) msgIn;
			method = reqIn.getMethod();
			isRequest = true;
		}

		if (method.equals("Status.Ping")) {
			respOut = new JSONRPC2Response(handleStatusPing(reqIn), reqIn.getID());
		} else if (method.equals("TicTacToe.NextMove")) {
			respOut = new JSONRPC2Response(handleTicTacToeNextMove(reqIn), reqIn.getID());
		} else if (method.equals("TicTacToe.Complete")) {
			if (isRequest) {
				respOut = new JSONRPC2Response(handleTicTacToeComplete(reqIn), reqIn.getID());
			}
		} else if (method.equals("TicTacToe.Error")) {
			if (isRequest) {
				respOut = new JSONRPC2Response(handleTicTacToeError(reqIn), reqIn.getID());
			}
		} else {
			respOut = new JSONRPC2Response("", reqIn.getID());
			respOut.setError(new JSONRPC2Error(0, "Not Recognised: " + reqIn.getMethod()));
		}
		
		if (isRequest) {
			response.setResponseString(respOut.toString());
		}
		
		return response;	
	}
	
	public void handle(HttpExchange t) throws IOException {
		
		// get request body
		InputStream requestBodyStream = t.getRequestBody();
		StringWriter writer = new StringWriter();
		IOUtils.copy(requestBodyStream, writer);
		String requestBody = writer.toString();
		
		// get response
		Response response = parseRequest(requestBody);
		boolean respond = response.getRespond();
		String responseString = response.getResponseString();
		
		// respond if necessary
		if (respond) {
			t.sendResponseHeaders(200, responseString.length());
			OutputStream os = t.getResponseBody();
			os.write(responseString.getBytes());
			os.close();
		}
		
	}
	
}
