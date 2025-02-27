package com.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.testing.FieldExtractor;
import com.testing.TestCase;
import com.testing.TestResult;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class RunTestCases
 */
@WebServlet("/runTestCases")
public class RunTestCasesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RunTestCasesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read and log the raw request for debugging
            BufferedReader reader = request.getReader();
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            System.out.println("Received JSON: " + requestBody.toString());

            // Parse JSON into a Map<String, List<String>>
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
            Map<String, List<String>> allInputs = gson.fromJson(requestBody.toString(), type);
            System.out.println("Parsed user inputs: " + allInputs);
            
            
            FieldExtractor fieldExtractor = (FieldExtractor) request.getSession().getAttribute("fieldExtractor");
//            List<TestCase> extractedFields = (List<TestCase>) request.getSession().getAttribute("extractedFields");
    		
            List<TestResult> results = fieldExtractor.runTests(fieldExtractor.testCaseFieldMap, allInputs);
            
//            getServletContext().setAttribute("testResults", results);
            
            HttpSession session = request.getSession();
			session.setAttribute("testResults", results);
            
            System.out.println("=================>Results"+results);
            String jsonResponse = gson.toJson(results);
            response.getWriter().write(jsonResponse); 
	        }
	        catch (Exception e) {
	            e.printStackTrace(); // Log server-side errors
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
	        }
	}

}
