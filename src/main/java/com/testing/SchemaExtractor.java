package com.testing;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class SchemaExtractor {
   

    public static JsonNode extractSchema(JsonNode node) {
        if (node.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                // Check for common schema-related keywords
                if (key.equalsIgnoreCase("schema") || key.equalsIgnoreCase("body") ||
                    key.equalsIgnoreCase("response") || key.equalsIgnoreCase("request") ||
                    key.equalsIgnoreCase("bodySchema")) {
                    return value; // Return the first schema found
                }

                // Recursively search in the next level
                JsonNode foundSchema = extractSchema(value);
                if (foundSchema != null) return foundSchema;
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                JsonNode foundSchema = extractSchema(element);
                if (foundSchema != null) return foundSchema;
            }
        }
        return null; // No schema found
    }
}
