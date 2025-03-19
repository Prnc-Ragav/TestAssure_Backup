package com.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openqa.selenium.WebElement;

import com.google.gson.Gson;
import com.testing.FieldExtractor;
import com.testing.TestCase;
import com.testing.TestResult;

/**
 * Servlet implementation class ValidationServlet
 */
@WebServlet("/extractFields")
public class FieldExtractingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public FieldExtractingServlet() {
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
		String url= request.getParameter("url");
		
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
//		String testResults = FieldExtractor.runTests(url); 
//		
//		response.setContentType("application/json");
//        response.getWriter().write(testResults);
		
        if (url == null || url.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"URL is required\"}");
            return;
        }
		
		try {
			FieldExtractor fieldExtractor = new FieldExtractor();
			
			List<TestCase> testCases = fieldExtractor.fetchVisibleFormFields(url); 
//			List<String> executedMethods = fieldExtractor.executedMethods;

			if (testCases == null || testCases.isEmpty()) {
                response.getWriter().write("[]"); // Return an empty array if no results
            } 
			
			else {
				
//				HttpSession session = request.getSession();
//    			session.setAttribute("testResults", testResults);
//    			session.setAttribute("executedMethods", executedMethods);
//    			session.setAttribute("url", url);
//    			
    			HttpSession session = request.getSession();
    			session.setAttribute("fieldExtractor", fieldExtractor);
    			session.setAttribute("tesyURL", url);
//    			session.setAttribute("extractedFields", testCases);
				
                String jsonResponse = new Gson().toJson(testCases);
                response.getWriter().write(jsonResponse);
                System.out.println(jsonResponse);
            }
			
			
	        
//	        StringBuilder json = new StringBuilder("[");
//	        for (TestResult result : testResults) {
//	            json.append(result.toJSON()).append(",");
//	        }
//	        if (!testResults.isEmpty()) json.deleteCharAt(json.length() - 1); 
//	        json.append("]");
		}
		catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			String errorMessage = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Unknown error";
            response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
            e.printStackTrace();
		}
		
	}

}
