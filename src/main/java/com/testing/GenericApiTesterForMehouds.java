package com.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GenericApiTesterForMehouds {
    public static List<APITestResult> StartTesting(File file) {
    	List<APITestResult> results = new ArrayList<APITestResult>();
        try {
            File apiDocFile = file;//----------------------------------------------------------------------------------------------------------------------------------------------
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiDocFile);

            Set<String> baseUrls = extractBaseUrls(rootNode);
            List<Map<String, Object>> endpoints = extractEndpointsAndMethods(rootNode);

            System.out.println("✅ Base URLs: " + baseUrls);
            System.out.println("✅ Endpoints with Methods: " + endpoints);

            for (String baseUrl : baseUrls) {
                for (Map<String, Object> endpoint : endpoints) {
                    JsonNode bodySchema = (JsonNode) SchemaExtractor.extractSchema(rootNode);
                    Thread.sleep(1000);
                    List<String> bodyVariations = GeminiAPI.generateBodyFromSchema(bodySchema);

                    for (String requestBody : bodyVariations) {
                        System.out.println("\n📌 Sending request with body: " + requestBody);
                        APITestResult response = sendRequest(baseUrl, endpoint, requestBody);
                        results.add(response);
                        Thread.sleep(1000);
                        System.out.println("🔹 Response: \n" + response.getResponceCode() + "---------------------");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("finished");
        return results;
    }

    private static Set<String> extractBaseUrls(JsonNode node) {
        Set<String> baseUrls = new HashSet<>();
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                JsonNode value = entry.getValue();

                if (value.isTextual() && isUrl(value.asText())) {
                    baseUrls.add(value.asText());
                }
                baseUrls.addAll(extractBaseUrls(value));
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                baseUrls.addAll(extractBaseUrls(element));
            }
        }
        return baseUrls;
    }

    private static List<Map<String, Object>> extractEndpointsAndMethods(JsonNode rootNode) {
        List<Map<String, Object>> endpoints = new ArrayList<>();
        JsonNode endpointsArray = rootNode.get("endpoints");

        if (endpointsArray != null && endpointsArray.isArray()) {
            for (JsonNode endpointNode : endpointsArray) {
                String path = endpointNode.get("path").asText();
                JsonNode methodsNode = endpointNode.get("methods");

                if (methodsNode != null && methodsNode.isObject()) {
                    for (Iterator<Map.Entry<String, JsonNode>> methodIt = methodsNode.fields(); methodIt.hasNext(); ) {
                        Map.Entry<String, JsonNode> methodEntry = methodIt.next();
                        String method = methodEntry.getKey().toUpperCase();

                        Map<String, Object> endpointData = new HashMap<>();
                        endpointData.put("method", method);
                        endpointData.put("path", path);

                        JsonNode requestNode = methodEntry.getValue().get("request");
                        if (requestNode != null) {
                            JsonNode bodySchema = requestNode.get("bodySchema");
                            endpointData.put("bodySchema", bodySchema); // ✅ FIX: Store as JsonNode, not String
                        }
                        endpoints.add(endpointData);
                    }
                }
            }
        }
        return endpoints;
    }


    private static APITestResult sendRequest(String baseUrl, Map<String, Object> endpoint, String requestBody) {
        String method = (String) endpoint.get("method");
        String urlStr = baseUrl + endpoint.get("path");
        System.out.println(requestBody.contains("true") + "---------------------------------");
        int responseCode = 0;
        String line = "";
        
        
        System.out.println("path =========================================" + endpoint.get("path") + endpoint);
        try {
            System.out.println("🔗 Request URL: " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod(method.trim());
            System.out.println( "++++++++++++++++++++++++++++++" + method + "+++++++++++++++++++++++");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            if ((method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) 
                    && requestBody != null && !requestBody.isEmpty()) {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            responseCode = conn.getResponseCode();
            System.out.println("response code : " + responseCode);
            BufferedReader in = (responseCode >= 200 && responseCode < 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder response = new StringBuilder();
            do{
                response.append(line);
            }while ((line = in.readLine()) != null);
            in.close();
            if((requestBody.contains("true") && responseCode < 400 && responseCode >=200) || responseCode == 429)
            {
            	 return APITestResult.success(baseUrl, endpoint.get("path").toString() , method, requestBody, responseCode, response.toString());
            }
            else {
            	return APITestResult.failure(baseUrl, endpoint.get("path").toString() , method, requestBody, responseCode, response.toString() , "try");
            }

            
        } catch (Exception e) {
        	System.out.println(e);
            return APITestResult.failure(baseUrl, endpoint.get("path").toString() , method, requestBody, responseCode, line , "catch");
        }
    }

    private static boolean isUrl(String value) {
        return value.matches("https?://[a-zA-Z0-9.-]+(:\\d+)?(/.*)?");
    }
}
