package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.testing.TestCase_JavaFile_Generator;

/**
 * Servlet implementation class JavaFileGeneratingServlet
 */
@WebServlet("/downloadJavaFile")
public class JavaFileGeneratingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JavaFileGeneratingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> executedMethods = (List<String>) request.getSession().getAttribute("executedMethods");
		List<String> mainMethod = (List<String>) request.getSession().getAttribute("mainMethod");
		List<String> testResultClassMethods = (List<String>) request.getSession().getAttribute("testResultClassMethod");
        String testUrl = (String) request.getSession().getAttribute("testUrl"); 
        
        File file = TestCase_JavaFile_Generator.generateTestCaseFile(executedMethods, mainMethod, testResultClassMethods, testUrl);
        
        response.setContentType("text/x-java-source");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setContentLength((int) file.length());
        
        FileInputStream fileInputStream = new FileInputStream(file);
        OutputStream responseOutputStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            responseOutputStream.write(buffer, 0, bytesRead);
        }

        fileInputStream.close();
        responseOutputStream.flush();
        responseOutputStream.close();

        // Delete the file after sending it
        file.delete();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
