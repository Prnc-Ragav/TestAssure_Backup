package com.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.testing.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
@WebServlet("/APITester")
public class APITestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Get the uploaded file
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(objectMapper.writeValueAsString("{\"error\": \"No file uploaded\"}"));
                return;
            }

            // Save the file temporarily
            File tempFile = File.createTempFile("api_doc", ".json");
            try (InputStream fileContent = filePart.getInputStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileContent.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            // Call your existing method
            List<APITestResult> results = GenericApiTesterForMehouds.StartTesting(tempFile);
            if(results.isEmpty() || results == null)
            {
            	response.getWriter().append("[]");
            }
            else {
            	System.out.println("writeing response");
            	String jsonResponse = new Gson().toJson(results);
                response.getWriter().write(jsonResponse);
                System.out.println(jsonResponse);
            }

            // Respond with success message
            response.setStatus(HttpServletResponse.SC_OK);
//            out.write(objectMapper.writeValueAsString("{\"message\": \"File processed successfully\"}"));

            // Delete the temp file after processing
            tempFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(objectMapper.writeValueAsString("{\"error\": \"Failed to process file\"}"));
        } finally {
            out.flush();
            out.close();
        }
    }
}