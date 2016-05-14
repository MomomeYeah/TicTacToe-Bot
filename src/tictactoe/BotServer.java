package tictactoe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

import config.ConfigFactory;
import config.IConfig;
import net.minidev.json.parser.ParseException;

@SuppressWarnings("restriction")
// http://www.rgagnon.com/javadetails/java-have-a-simple-http-server.html
public class BotServer {
	
	IConfig config;
	HttpServer server;
	
	public BotServer() throws IOException, ParseException {
		this.config = ConfigFactory.getConfig();
		
		this.server = HttpServer.create(new InetSocketAddress(this.config.getInteger("PORT")), 0);
		this.server.createContext("/bot", new BotHandler());
		this.server.setExecutor(null);
	}
	
	public String getRegistrationString() {
		String method = "RegistrationService.Register";
		int id = 1;
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("token", config.getString("token"));
		params.put("botname", config.getString("botname"));
		params.put("botversion", config.getString("botversion"));
		params.put("game", "TICTACTOE");
		params.put("rpcendpoint", config.getString("endpointURL"));
		params.put("programminglanguage", "Java");
		params.put("website", config.getString("website"));
		params.put("description", config.getString("description"));
		
		return Utils.JSONRPC2RequestString(method, params, id);
	}
	
	public String register() throws IOException {
		String registrationString = getRegistrationString();
		return Utils.getJSONRPCResponse(config.getString("merkneraURL"), registrationString);
	}
	
	public static void main(String args[]) throws IOException, ParseException {
		BotServer bs = new BotServer();
		
		System.out.println("Starting server on port " + bs.config.getInteger("PORT") + "...");
		bs.server.start();
		
		System.out.println("Registering bot on " + bs.config.getString("merkneraURL") + "...");
		try {
			System.out.println(bs.register());
		}
		catch (IOException e) {
			System.out.println("Unable to register bot!");
			System.out.println(e.getMessage());
			bs.server.stop(0);
		}
		
	}

}
