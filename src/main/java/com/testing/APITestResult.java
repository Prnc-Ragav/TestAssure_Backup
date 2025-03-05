package com.testing;


import com.google.gson.Gson;

public class APITestResult {
    private String baseUrl;
    private String endpoint;
    private String method;
    private String requestBody;
    private int responseCode;
    private String responseBody;
    private boolean isSuccess;
    private String errorMessage;

    
    
    public APITestResult(String baseUrl, String endpoint, String method, String requestBody, int responseCode, 
                         String responseBody, boolean isSuccess, String errorMessage) {
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.method = method;
        this.requestBody = requestBody;
        this.responseCode = responseCode;
        this.responseBody = responseBody; 
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
    }

    
    public static APITestResult success(String baseUrl, String endpoint, String method, String requestBody, 
                                        int responseCode, String responseBody) {
        return new APITestResult(baseUrl, endpoint, method, requestBody, responseCode, responseBody, true, null);
    }

    
    public static APITestResult failure(String baseUrl, String endpoint, String method, String requestBody, 
                                        int responseCode, String responseBody, String errorMessage) {
        return new APITestResult(baseUrl, endpoint, method, requestBody, responseCode, responseBody, false, errorMessage);
    }
    
    
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    
    public int getResponceCode()
    {
    	return this.responseCode;
    }

    

}
