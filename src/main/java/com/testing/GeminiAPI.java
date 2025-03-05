package com.testing;
import okhttp3.*;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GeminiAPI {
	
    private static final String API_KEY = "AIzaSyA50EThyQtS-LyYp-qYLcGGa3Jvbl7Cwgc";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static List<String> generateBodyFromSchema(JsonNode schema) throws IOException {
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("contents", new JSONObject()
                .put("role", "user")
                .put("parts", new JSONObject().put("text", "give valid and invalid testBody for this schema" + schema.asText() +  "as json[] give only json[] not any other things in cludeing the the first word json"))
        );

       
        String response = sendRequest(requestBody.toString());
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        
        String textContent = rootNode.path("candidates").get(0)
                                    .path("content").path("parts").get(0)
                                    .path("text").asText();
        
        textContent = textContent.replaceAll("```json", "").replaceAll("```", "").trim();
        
        
		return convertJsonToList(textContent);
        
    }
    
    
    
    
    public static List<String> convertJsonToList(String json) {
        List<String> resultList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            
            List<Map<String, Object>> list = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});

            
            for (Map<String, Object> map : list) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    sb.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
                }
                
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2);
                }
                resultList.add(sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
    
    
    

    public static String sendRequest(String jsonPayload) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Connection timeout
                .readTimeout(60, TimeUnit.SECONDS)     // Read timeout
                .writeTimeout(60, TimeUnit.SECONDS)    // Write timeout
                .build();

        // Create request body
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));

        // Build request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();

            return response.body().string();
        }
    }
    
}
