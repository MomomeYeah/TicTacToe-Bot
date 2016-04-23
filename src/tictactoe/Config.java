package tictactoe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Config {
	
	HashMap<String,String> configItems;
	
	public Config(String configFile) throws ParseException, FileNotFoundException {
		this.configItems = new HashMap<String,String>();
		
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		
		JSONObject jo = (JSONObject) parser.parse(new FileReader(configFile));
		Set<Entry<String,Object>> configSet = jo.entrySet();
		for (Entry<String,Object> e : configSet) {
			if (e.getValue() instanceof String) {
				this.configItems.put(e.getKey(), (String) e.getValue());
			}
		}
	}
	
	public Config() throws FileNotFoundException, ParseException {
		this("config.json");
	}
	
	public String getConfigValue(String key) {
		return this.configItems.get(key);
	}
	
	public static void main(String args[]) throws ParseException, FileNotFoundException {
		//Config config = new Config("config.json");
		Config config = new Config();
		for (Entry<String,String> e : config.configItems.entrySet()) {
			System.out.format("Key: %s, Value: %s\n", e.getKey(), e.getValue());
		}
	}

}
