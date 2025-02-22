package com.testing;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FieldExtractor {
	
	public List<String> executedMethods;
	
	public List<TestResult> extractFields(String url) {
		WebDriverManager.chromedriver().setup(); // Automatically downloads the correct driver
	    WebDriver driver = new ChromeDriver();
	    
	    driver.get(url);
	    
	    new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete")
        );
	    
	    List<WebElement> inputFields = driver.findElements(By.tagName("input"));
        inputFields.addAll(driver.findElements(By.tagName("select")));
        inputFields.addAll(driver.findElements(By.tagName("textarea")));
        inputFields.addAll(driver.findElements(By.tagName("a")));
        System.out.println("Input Fields Count : ============================>"+inputFields.size());
        
        List<WebElement> anchorTags = driver.findElements(By.tagName("a"));
        System.out.println("Anchor tag Count : ============================>"+anchorTags.size());
	    
        System.out.println("=================>"+inputFields);
//        if (inputFields.isEmpty()) {
//            driver.quit();
//            return "{\"error\": \"No input fields found!\"}";
//        }
        
        TestCase testCases = new TestCase(driver);
        List<TestResult> results = testCases.runTests(inputFields);
        
        executedMethods = testCases.getExecutedMethods();

        driver.quit();
        return results;
	    
//	    TestResult result = TestCase.testTextField(inputFields.get(0));
//        driver.quit();
//
//        return result.toJSON();
	}
	
	
	public static String runTests(String url) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.get(url);

        List<WebElement> inputFields = driver.findElements(By.tagName("input"));
        if (inputFields.isEmpty()) {
            driver.quit();
            return new JSONObject().put("error", "No input fields found!").toString();
        }

        // Test the first field (we'll extend this to all fields later)
        TestResult testResult = TestCase.testTextField(inputFields.get(0));

        driver.quit();

        return new JSONObject(testResult).toString();
    }
	
}
