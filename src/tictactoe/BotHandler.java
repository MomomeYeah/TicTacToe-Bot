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
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.minidev.json.JSONArray;

@SuppressWarnings("restriction")
public class BotHandler implements HttpHandler {
	
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
			String mark = (String) ja.get(i);
			String finalMark = mark.equals("X") || mark.equals("O") ? mark : null;
			gameState.add(finalMark);
		}
		
		Solver solver = new Solver(side, gameState);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("position", solver.getMove());
		
		return params;
	}
	
	public Object handleTicTacToeComplete(JSONRPC2Request reqIn) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("status", "OK");
		
		return params;
	}
	
	public Object handleTicTacToeError(JSONRPC2Request reqIn) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("status", "OK");
		
		return params;
	}
	
	public String parseRequest(String requestString) {
		JSONRPC2Request reqIn = null;
		try {
			reqIn = JSONRPC2Request.parse(requestString);
		} catch (JSONRPC2ParseException e) {
			return new JSONRPC2Error(0, e.getMessage()).toString();
		}
		
		JSONRPC2Response respOut = null;
		
		String method = reqIn.getMethod();
		if (method.equals("Status.Ping")) {
			respOut = new JSONRPC2Response(handleStatusPing(reqIn), reqIn.getID());
		} else if (method.equals("TicTacToe.NextMove")) {
			respOut = new JSONRPC2Response(handleTicTacToeNextMove(reqIn), reqIn.getID());
		} else if (method.equals("TicTacToe.Complete")) {
			respOut = new JSONRPC2Response(handleTicTacToeComplete(reqIn), reqIn.getID());
		} else if (method.equals("TicTacToe.Error")) {
			respOut = new JSONRPC2Response(handleTicTacToeError(reqIn), reqIn.getID());
		} else {
			return new JSONRPC2Error(0, "Not Recognised: " + reqIn.getMethod()).toString();
		}
		
		return respOut.toString();
	}
	
	public void handle(HttpExchange t) throws IOException {
		
		// get request body
		InputStream requestBodyStream = t.getRequestBody();
		StringWriter writer = new StringWriter();
		IOUtils.copy(requestBodyStream, writer);
		String requestBody = writer.toString();
		
		// get response
		String responseString = parseRequest(requestBody);
		
		// respond if necessary
		t.sendResponseHeaders(200, responseString.length());
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
		
	}
	
}
