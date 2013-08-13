package com.neatocode.mymonitor;

public interface Analyzer {

	public String getLabel();

	public Boolean isConditionGood();

	public Integer getDrawable();

	public void didDisplay();

	public boolean clearBackground();

}
