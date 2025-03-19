package com.testing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TestCase_JavaFile_Generator {
	private static final String FILE_NAME = "GeneratedTestCases.java";
	
	 public static File generateTestCaseFile(List<String> executedMethods, List<String> mainMethod, List<String> testResultClassMethods, String testUrl) {
		 File file = new File(FILE_NAME);

		 try (FileWriter writer = new FileWriter(file)) {
	            // Write package and imports
	            writer.write("import org.openqa.selenium.By;\n");
	            writer.write("import org.openqa.selenium.WebDriver;\n");
	            writer.write("import org.openqa.selenium.WebElement;\n");
	            writer.write("import org.openqa.selenium.chrome.ChromeDriver;\n\n");

	            // Write class definition
	            writer.write("public class GeneratedTestCases {\n");

	            // Write main method
	            writer.write("    public static void main(String[] args) {\n");
	            writer.write("        new GeneratedTestCases().fetchVisibleFormFields(\"" + testUrl + "\");\n\n");
	            
	            
	            for (String method : mainMethod) {
	                writer.write(method + "\n");
	            }

	            writer.write("}\n");
	            
	            for(String method : executedMethods) {
	            	writer.write(method + "\n");
	            }
	            
	            writer.write("}\n");
	            
	            for(String method : testResultClassMethods) {
	            	writer.write(method + "\n");
	            }
	            writer.flush();
	            
		 }
		 catch (IOException e) {
	        e.printStackTrace();
		 }
	
		 return file;
	 }
}
