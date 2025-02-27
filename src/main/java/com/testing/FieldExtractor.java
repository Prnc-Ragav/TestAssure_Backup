package com.testing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
	WebDriver driver;
	
	public List<String> executedMethods;
	
	public List<WebElement> fetchedFields;
	public Map<String, WebElement> testCaseFieldMap = new LinkedHashMap<>();
	
	public List<TestResult> runTests(Map<String, WebElement> testCaseAndField, Map<String, List<String>> inputs){
		TestCase testCases = new TestCase(driver);
        List<TestResult> results = testCases.runTests(testCaseAndField, inputs);
        
        executedMethods = testCases.getExecutedMethods();

        driver.quit();
        return results;
	}
	
	public List<TestCase> extractFields(String url) {
		WebDriverManager.chromedriver().setup(); // Automatically downloads the correct driver
	    driver = new ChromeDriver();
	    
	    driver.get(url);
	    
	    new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete")
        );
	    
	    List<WebElement> inputFields = driver.findElements(By.tagName("input"));
        inputFields.addAll(driver.findElements(By.tagName("select")));
        inputFields.addAll(driver.findElements(By.tagName("textarea")));
//        inputFields.addAll(driver.findElements(By.tagName("a")));
        System.out.println("Input Fields Count : ============================>"+inputFields.size());
        
//        List<WebElement> anchorTags = driver.findElements(By.tagName("a"));
//        System.out.println("Anchor tag Count : ============================>"+anchorTags.size());
	    
        System.out.println("=================>"+inputFields);
        
        fetchedFields = inputFields;
        
        List<TestCase> testCases = new ArrayList<>();
        int count = 1;

        for (WebElement field : inputFields) {
            String label = "";
            String placeholder = field.getAttribute("placeholder");
            String fieldName = field.getAttribute("name");
            String fieldType = field.getAttribute("type");

            // Try to find a label using 'for' attribute
            String fieldId = field.getAttribute("id");
            if (fieldId != null && !fieldId.isEmpty()) {
                List<WebElement> labels = driver.findElements(By.tagName("label"));
                for (WebElement lbl : labels) {
                    if (fieldId.equals(lbl.getAttribute("for"))) {
                        label = lbl.getText();
                        break;
                    }
                }
            }

            // If no label is found, use placeholder or name
            String testCaseName = "Validate " + (!label.isEmpty() ? label : (placeholder != null ? placeholder : fieldName)) 
                                  + " field with value " + count;
            
            System.out.println("Test Case Name : "+testCaseName+"  "+"Field Type : "+fieldType);

            List<String> predefinedInputs = TestCase.getPredefinedInputs(fieldType);
            
            testCaseFieldMap.put(testCaseName, field);
            
            testCases.add(new TestCase(testCaseName, predefinedInputs));
            count++;
        }
        
        return testCases;
	}
	
	
	
	
	
	
	
	
	
	
	
	public List<TestCase> fetchVisibleFormFields(String url) {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
        
		driver.get(url);
		
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete")
        );
		
		System.out.println("1.>>>>>>>Entered");

        List<TestCase> testCases = new ArrayList<>();

        // Step 1: Check for iframes containing forms
        boolean formFoundInIframe = checkIframesForForms(driver, testCases);
        System.out.println(">>>>>>>Entered");
        // Step 2: If no form found in iframes, extract from the main page
        if (!formFoundInIframe) {
            System.out.println("No form found in iframes. Checking the main page...");
            extractAllVisibleFields(driver, testCases);
        }

        return testCases;
    }
	
	
	private boolean checkIframesForForms(WebDriver driver, List<TestCase> testCases) {
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        System.out.println("2>>>>>>>Entered");
        for (WebElement iframe : iframes) {
            driver.switchTo().frame(iframe);
            
            if (!driver.findElements(By.tagName("form")).isEmpty()) {
                System.out.println("Form found inside an iframe. Extracting fields...");
                extractAllVisibleFields(driver, testCases);
//                driver.switchTo().defaultContent(); // Return to main page
                return true; // Stop searching after the first iframe with a form
            }

            // Recursively check nested iframes
            if (checkIframesForForms(driver, testCases)) {
//                driver.switchTo().defaultContent();
                return true;
            }

            driver.switchTo().defaultContent();
        }
        return false;
    }

	
	private void extractAllVisibleFields(WebDriver driver, List<TestCase> testCases) {
        List<WebElement> inputFields = driver.findElements(By.tagName("input"));
        extractVisibleFields(inputFields, testCases, driver);

        List<WebElement> textAreas = driver.findElements(By.tagName("textarea"));
        extractVisibleFields(textAreas, testCases, driver);

        List<WebElement> selectFields = driver.findElements(By.tagName("select"));
        extractVisibleSelectFields(selectFields, testCases, driver);
    }
	
	
	private void extractVisibleFields(List<WebElement> elements, List<TestCase> testCases, WebDriver driver) {
        for (WebElement field : elements) {
            if (field.isDisplayed()) { 
                String identifier = getFieldIdentifier(field, driver);
                String type = field.getTagName().equals("textarea") ? "textarea" : field.getAttribute("type");
                List<String> predefinedInputs = TestCase.getPredefinedInputs(type);

                String testCaseName = "Validate " + identifier + " field";
                testCases.add(new TestCase(testCaseName, predefinedInputs));
                
                testCaseFieldMap.put(testCaseName, field);
            }
        }
    }

    private void extractVisibleSelectFields(List<WebElement> selectFields, List<TestCase> testCases, WebDriver driver) {
        for (WebElement field : selectFields) {
            if (field.isDisplayed()) {
                String identifier = getFieldIdentifier(field, driver);

                String testCaseName = "Validate dropdown " + identifier;
                List<String> predefinedInputs = new ArrayList<>();
                
                List<WebElement> options = field.findElements(By.tagName("option"));
                if (!options.isEmpty()) { // Ensure there are options
                    Collections.shuffle(options); // Shuffle for randomness

                    int limit = Math.min(options.size(), 5); // Get either 5 or the available count
                    for (int i = 0; i < limit; i++) {
                        predefinedInputs.add(options.get(i).getText());
                    }
                }

                testCases.add(new TestCase(testCaseName, predefinedInputs));
                
                testCaseFieldMap.put(testCaseName, field);
            }
        }
    }

    private String getFieldIdentifier(WebElement field, WebDriver driver) {
        String placeholder = field.getAttribute("placeholder");
        if (placeholder != null && !placeholder.isEmpty()) return placeholder;

        String id = field.getAttribute("id");
        if (id != null && !id.isEmpty()) {
            List<WebElement> labels = driver.findElements(By.xpath("//label[@for='" + id + "']"));
            if (!labels.isEmpty()) return labels.get(0).getText();
        }

        String name = field.getAttribute("name");
        if (name != null && !name.isEmpty()) return name;

        return field.getTagName().equals("textarea") ? "text area" : field.getAttribute("type");
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
