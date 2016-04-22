package tictactoe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.minidev.json.JSONArray;

@SuppressWarnings("restriction")
// http://www.rgagnon.com/javadetails/java-have-a-simple-http-server.html
public class BotServer {
	
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
		
		public String parseRequest(String requestString) {
			
			JSONRPC2Request reqIn = null;
			try {
				reqIn = JSONRPC2Request.parse(requestString);
			} catch (JSONRPC2ParseException e) {
				return e.getMessage();
			}
			
			JSONRPC2Response respOut = null;

			if (reqIn.getMethod().equals("Status.Ping")) {
				respOut = new JSONRPC2Response(handleStatusPing(reqIn), reqIn.getID());
			} else if (reqIn.getMethod().equals("TicTacToe.NextMove")) {
				respOut = new JSONRPC2Response(handleTicTacToeNextMove(reqIn), reqIn.getID());
			} else if (reqIn.getMethod().equals("TicTacToe.Complete")) {
				respOut = new JSONRPC2Response(handleTicTacToeComplete(reqIn), reqIn.getID());
			} else if (reqIn.getMethod().equals("TicTacToe.Error")) {
				respOut = new JSONRPC2Response(handleTicTacToeError(reqIn), reqIn.getID());
			} else {
				respOut = new JSONRPC2Response("Not Recognised: " + reqIn.getMethod(), reqIn.getID());
			}
			
			return respOut.toString();
			
		}
		
		public void handle(HttpExchange t) throws IOException {
			
			InputStream requestBodyStream = t.getRequestBody();
			StringWriter writer = new StringWriter();
			IOUtils.copy(requestBodyStream, writer);
			String requestBody = writer.toString();
			
			String responseString = parseRequest(requestBody);
			
			t.sendResponseHeaders(200, responseString.length());
			OutputStream os = t.getResponseBody();
			os.write(responseString.getBytes());
			os.close();
			
		}
	}
	
	HttpServer server;
	
	public BotServer() throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(8000), 0);
		this.server.createContext("/bot", new BotHandler());
		this.server.setExecutor(null);
	}
	
	public static void main(String args[]) throws IOException {
		BotServer bs = new BotServer();
		
		System.out.println("Starting server on port 8000");
		bs.server.start();
	}

}
