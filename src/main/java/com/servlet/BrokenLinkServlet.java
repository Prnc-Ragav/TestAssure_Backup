package com.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.testing.*;
import com.google.gson.Gson;

/**
 * Servlet implementation class BrokenLinkServlet
 */
@WebServlet("/BrokenLinkServlet")
public class BrokenLinkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("request sent");
        
        String url = request.getParameter("url");

        if (url == null || url.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"URL is required\"}");
            return;
        }

        try {
            System.out.println("Checking broken links for: " + url);
            List<BrokenLinkResults> testResults = BrokenLinkChecker.testURL(url);
            response.setContentType("application/json"); 
            if(testResults.isEmpty())
            {
            	response.getWriter().write("[]");
            }
            else {
            	String jsonResponse = new Gson().toJson(testResults);
                response.getWriter().write(jsonResponse);
                System.out.println(jsonResponse.toString());
                System.out.println("Response sent successfully.");
            }
 
            
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"An internal error occurred. Check server logs.\"}");
        }
        
    }

}
