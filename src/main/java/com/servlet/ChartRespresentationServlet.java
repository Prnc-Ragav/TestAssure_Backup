package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.testing.TestResult;

/**
 * Servlet implementation class ChartRespresentationServlet
 */
@WebServlet("/getChartRepresentation")
public class ChartRespresentationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChartRespresentationServlet() {
    	super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<TestResult> storedResults = (List<TestResult>) request.getSession().getAttribute("testResults");
		
		
		Map<String, Integer> resultCounts = new LinkedHashMap<>();
	    Map<String, Integer> failedReasons = new LinkedHashMap<>();
	    Map<String, Integer> fieldTypeCounts = new LinkedHashMap<>();
	    
	    // Initialize result status counts
	    resultCounts.put("Passed", 0);
	    
	    // Count results and field types
	    for (TestResult result : storedResults) {
	        if (result.getResult().equalsIgnoreCase("Pass")) {
	            resultCounts.put("Passed", resultCounts.get("Passed") + 1);
	        } else {
	            String failureReason = result.getActualOutput();
	            failedReasons.put(failureReason, failedReasons.getOrDefault(failureReason, 0) + 1);
	        }

	        // Count field types
	        String fieldType = result.getFieldType();
	        fieldTypeCounts.put(fieldType, fieldTypeCounts.getOrDefault(fieldType, 0) + 1);
	    }

	    // Combine both maps
	    Map<String, Object> responseData = new LinkedHashMap<>();
	    responseData.put("Passed", resultCounts.get("Passed"));
	    responseData.put("Failed Reasons", failedReasons);
	    responseData.put("Field Types", fieldTypeCounts);

	    // Convert to JSON
	    String json = new Gson().toJson(responseData);
	    response.getWriter().print(json);
		
//		Map<String, Integer> resultCounts = new LinkedHashMap<>();
//        Map<String, Integer> categoryCounts = new LinkedHashMap<>();
//
//        // Initialize result status counts
//        resultCounts.put("Pass", 0);
//        resultCounts.put("Fail", 0);
//        resultCounts.put("Not Selectable", 0);
//        resultCounts.put("Validation Error", 0);
//
//        // Count results and test categories
//        for (TestResult result : storedResults) {
//            String status = result.getResult();
//            resultCounts.put(status, resultCounts.getOrDefault(status, 0) + 1);
//
//            // Categorize based on test case names
//            String category = categorizeTestCase(result.getTestCaseName());
//            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
//        }
//
//        // Combine both maps
//        Map<String, Object> responseData = new LinkedHashMap<>();
//        responseData.putAll(resultCounts);
//        responseData.put("Category Breakdown", categoryCounts);
//
//        // Convert to JSON
//        String json = new Gson().toJson(responseData);
//        response.getWriter().print(json);
    }
	
	
	
	private String categorizeTestCase(String testCaseName) {
        if (testCaseName.contains("Required")) return "Required Field Validation";
        if (testCaseName.contains("Max Length")) return "Max Length Check";
        if (testCaseName.contains("XSS")) return "XSS Injection";
        return "Other Tests";
    }
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
