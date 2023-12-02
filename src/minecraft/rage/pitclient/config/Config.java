package rage.pitclient.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Config {

	private final File file;
	  
	  private JsonObject object;
	  
	  public void save() {
	    try {
	      PrintWriter writer = new PrintWriter(this.file);
	      writer.println(this.object.toString());
	      writer.flush();
	      writer.close();
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } 
	  }
	  
	  public void load(boolean saving) {
	    try {
	    	if (saving) {
	    		this.file.delete();
	    		this.file.createNewFile();
	    		this.object = new JsonObject();
	    	}
	      if (!this.file.exists()) {
	        this.file.createNewFile();
	        return;
	      } 
	      BufferedReader reader = new BufferedReader(new FileReader(this.file));
	      try {
	        this.object = (JsonObject)(new JsonParser()).parse(reader);
	      } catch (ClassCastException e2) {
	        this.object = new JsonObject();
	      } 
	    } catch (IOException e) {
	      e.printStackTrace();
	    } 
	  }
	  
	  public String getString(String key) {
	    return this.object.get(key).getAsString();
	  }
	  
	  public double getDouble(String key) {
	    return this.object.get(key).getAsDouble();
	  }
	  
	  public float getFloat(String key) {
	    return this.object.get(key).getAsFloat();
	  }
	  
	  public int getInt(String key) {
	    return this.object.get(key).getAsInt();
	  }
	  
	  public void set(String key, JsonObject value) {
	    this.object.add(key, (JsonElement)value);
	  }
	  
	  public void set(String key, String value) {
	    this.object.addProperty(key, value);
	  }
	  
	  public void set(String key, boolean value) {
	    this.object.addProperty(key, Boolean.valueOf(value));
	  }
	  
	  public void set(String key, double value) {
	    this.object.addProperty(key, Double.valueOf(value));
	  }
	  
	  public void set(String key, float value) {
	    this.object.addProperty(key, Float.valueOf(value));
	  }
	  
	  public void set(String key, int value) {
	    this.object.addProperty(key, Integer.valueOf(value));
	  }
	  
	  public Config(File file) {
	    this.file = file;
	  }
	  
	  public JsonObject getObject() {
	    return this.object;
	  }
}
