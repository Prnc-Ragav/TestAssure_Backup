package com.testing;

public class BrokenLinkResults {
	
	
	

	private String testCaseName = "Broken Link Test";
	private String expectedOutput = "Page loaded successfully";
	private String actualOutput;
	String url;
	private int responseCode;
	private String result;
	
	
	
	public BrokenLinkResults(String url,String actualOutput, int responseCode,String result){
		
		this.actualOutput = actualOutput;
		this.responseCode = responseCode;
		this.url = url;
		this.result = result;
	}
	
	
	public String toJSON() {
		String output;
		output = "{"
                + "\"testCaseName\": \"" + testCaseName + "\","
                + "\"Url\": \"" + url + "\","
                + "\"expectedOutput\": \"" + expectedOutput + "\","
                + "\"actualOutput\": \"" + actualOutput + "\","
                + "\"responseCode\": \"" + responseCode + "\","
                + "\"result\": \"" + result + "\""
                + "}";
		return output;
	 }
	
	

}
