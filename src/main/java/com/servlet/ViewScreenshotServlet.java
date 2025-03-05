package com.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.testing.TestResult;

/**
 * Servlet implementation class ViewScreenshotServlet
 */
@WebServlet("/viewScreenshot")
public class ViewScreenshotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewScreenshotServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String screenshotId = request.getParameter("screenshotId");

        HttpSession session = request.getSession();
        Map<String, String> screenshotMap = (Map<String, String>) session.getAttribute("screenshotMap");

        if (screenshotMap == null || !screenshotMap.containsKey(screenshotId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Screenshot not found.");
            return;
        }

        File screenshotFile = new File(screenshotMap.get(screenshotId));
        response.setContentType("image/png");

        try (FileInputStream fis = new FileInputStream(screenshotFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
