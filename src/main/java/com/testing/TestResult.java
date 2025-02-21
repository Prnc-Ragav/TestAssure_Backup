package com.testing;

public class TestResult {
	
	static int count=0;
	
	private int testCaseNumber;
	
    private String testCaseName;
    private String fieldType;
    private String fieldName;
    private String givenInput;
    private String expectedOutput;
    private String actualOutput;
    private String result;

    public TestResult(String testCaseName, String fieldType, String fieldName, 
                      String givenInput, String expectedOutput, String actualOutput, String result) {
        this.testCaseName = testCaseName;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.givenInput = givenInput;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.result = result;
        
        this.count++;
        this.testCaseNumber=count;
    }

    // Convert to JSON format
    public String toJSON() {
        return "{"
        		+ "\"testCaseNumber\": \"" + testCaseNumber + "\","
                + "\"testCaseName\": \"" + testCaseName + "\","
                + "\"fieldType\": \"" + fieldType + "\","
                + "\"fieldName\": \"" + fieldName + "\","
                + "\"givenInput\": \"" + givenInput + "\","
                + "\"expectedOutput\": \"" + expectedOutput + "\","
                + "\"actualOutput\": \"" + actualOutput + "\","
                + "\"result\": \"" + result + "\""
                + "}";
    }

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getGivenInput() {
		return givenInput;
	}

	public void setGivenInput(String givenInput) {
		this.givenInput = givenInput;
	}

	public String getExpectedOutput() {
		return expectedOutput;
	}

	public void setExpectedOutput(String expectedOutput) {
		this.expectedOutput = expectedOutput;
	}

	public String getActualOutput() {
		return actualOutput;
	}

	public void setActualOutput(String actualOutput) {
		this.actualOutput = actualOutput;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
    
    
}