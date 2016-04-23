package tictactoe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
// http://www.rgagnon.com/javadetails/java-have-a-simple-http-server.html
public class BotServer {
	
	String registrationURL;
	HttpServer server;
	
	public BotServer(String registrationURL) throws IOException {
		this.registrationURL = registrationURL;
		
		this.server = HttpServer.create(new InetSocketAddress(8000), 0);
		this.server.createContext("/bot", new BotHandler());
		this.server.setExecutor(null);
	}
	
	public String getRegistrationString() {
		String method = "RegistrationService.Register";
		int id = 1;
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("token", "TODO - Get Token From Somewhere");
		params.put("botname", "ROBot");
		params.put("botversion", "v0.1");
		params.put("game", "TICTACTOE");
		params.put("rpcendpoint", "http://4773e163.ngrok.io/bot");
		params.put("programminglanguage", "Java");
		params.put("website", "https://github.com/MomomeYeah/TicTacToe-Bot");
		
		return Utils.JSONRPC2RequestString(method, params, id);
	}
	
	public String register() throws IOException {
		String registrationString = getRegistrationString();
		return Utils.getJSONRPCResponse(this.registrationURL, registrationString);
	}
	
	public static void main(String args[]) throws IOException {
		BotServer bs = new BotServer("https://www.google.com");
		
		System.out.println("Starting server on port 8000...");
		bs.server.start();
		
		System.out.println("Registering bot on " + bs.registrationURL + "...");
		try {
			System.out.println(bs.register());
		}
		catch (IOException e) {
			System.out.println("Unable to register bot!");
			bs.server.stop(0);
		}
		
	}

}
