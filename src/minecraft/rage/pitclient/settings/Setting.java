package rage.pitclient.settings;

import java.util.ArrayList;

import rage.pitclient.module.Module;

public class Setting {
	
	private String name;
	private Module parent;
	private String mode;
	private String tooltip;
	
	private String sval;
	private ArrayList<String> options;
	
	private boolean bval;
	
	private double dval;
	private double min;
	private double max;

	private boolean onlyint = false;
	

	public Setting(String name, Module parent, String sval, ArrayList<String> options, String tooltip){
		this.name = name;
		this.parent = parent;
		this.sval = sval;
		this.options = options;
		this.mode = "Combo";
		this.tooltip = tooltip;
	}
	
	public Setting(String name, Module parent, boolean bval, String tooltip){
		this.name = name;
		this.parent = parent;
		this.bval = bval;
		this.mode = "Check";
		this.tooltip = tooltip;
	}
	
	public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint, String tooltip){
		this.name = name;
		this.parent = parent;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = onlyint;
		this.mode = "Slider";
		this.tooltip = tooltip;
	}
	
	public String getName(){
		return name;
	}
	
	public Module getParentMod(){
		return parent;
	}
	
	public String getValString(){
		return this.sval;
	}
	
	public void setValString(String in){
		this.sval = in;
	}
	
	public ArrayList<String> getOptions(){
		return this.options;
	}
	
	public boolean getValBoolean(){
		return this.bval;
	}
	
	public void setValBoolean(boolean in){
		this.bval = in;
	}
	
	public double getValDouble(){
		if(this.onlyint){
			this.dval = (int)dval;
		}
		return this.dval;
	}

	public void setValDouble(double in){
		this.dval = in;
	}
	
	public double getMin(){
		return this.min;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public double getMax(){
		return this.max;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public boolean isCombo(){
		return this.mode.equalsIgnoreCase("Combo") ? true : false;
	}
	
	public boolean isCheck(){
		return this.mode.equalsIgnoreCase("Check") ? true : false;
	}
	
	public boolean isSlider(){
		return this.mode.equalsIgnoreCase("Slider") ? true : false;
	}
	
	public boolean onlyInt(){
		return this.onlyint;
	}
	
	public String getTooltip(){
		return tooltip;
	}
	
	public void setTooltip(String tooltip){
		this.tooltip = tooltip;
	}
}
