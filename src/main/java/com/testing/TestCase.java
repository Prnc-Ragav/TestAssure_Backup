package com.testing;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestCase {
	
	HttpSession session;
	
	private String testCaseName;
    private List<String> predefinedInputs;
    
    private Map<String, List<String>> inputsCollection;
	
	private WebDriver driver;

	private Map<String, String> screenshotMap = new LinkedHashMap<>();
	
	
	
	
    public TestCase(WebDriver driver) {
        this.driver = driver;
    }
    
    public TestCase(String testCaseName, List<String> predefinedInputs) {
    	this.testCaseName = testCaseName;
        this.predefinedInputs = predefinedInputs;
    }
    
    public static List<String> getPredefinedInputs(String fieldType) {
    	List<String> inputs = new ArrayList<>();
        switch (fieldType) {
            case "text":
            case "textarea":
                inputs.add("");
                inputs.add("123456");
                inputs.add("Valid input text");
                break;
            case "email":
                inputs.add("test@example.com");
                inputs.add("invalid-email");
                break;
            case "number":
                inputs.add("123");
                inputs.add("-1");
                inputs.add("abcd");
                break;
            case "password":
                inputs.add("P@ssw0rd");
                inputs.add("short");
                break;
            case "url":
                inputs.add("https://example.com");
                inputs.add("invalid_url");
                break;
            case "tel":
                inputs.add("1234567890");
                inputs.add("abc123");
                break;
            case "checkbox":
            case "radio":
                inputs.add("checked");
                inputs.add("unchecked");
                break;
            case "range":
                inputs.add("0");
                inputs.add("50");
                inputs.add("100");
                break;
            default:
                inputs.add("DefaultTestInput");
        }
        return inputs;
    }
    
    private List<String> getXssTestInputs() {
        List<String> xssInputs = new ArrayList<>();
        xssInputs.add("<script>alert('XSS')</script>");
        xssInputs.add("'><script>confirm('XSS')</script>");
        xssInputs.add("<img src=x onerror=alert(1)>");
        xssInputs.add("<svg onload=alert(1)>");
        return xssInputs;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<TestResult> runTests(Map<String, WebElement> testCaseMap, Map<String, List<String>> inputs, HttpSession session) {
        
    	this.session = session;
    	
    	List<TestResult> testResults = new ArrayList<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // JavaScript to detect error messages (including alert messages first)
        
        String script =
        	    "var detectedErrors = [];" +

        	    // Override alert, confirm, and prompt functions to capture messages
        	    "(function() {" +
        	    "    var originalAlert = window.alert;" +
        	    "    var originalConfirm = window.confirm;" +
        	    "    var originalPrompt = window.prompt;" +

        	    "    window.alert = function(message) {" +
        	    "        detectedErrors.push(message);" +
        	    "        originalAlert(message);" +  // Keep default behavior
        	    "    };" +

        	    "    window.confirm = function(message) {" +
        	    "        detectedErrors.push(message);" +
        	    "        return originalConfirm(message);" +  // Keep default behavior
        	    "    };" +

        	    "    window.prompt = function(message, defaultText) {" +
        	    "        detectedErrors.push(message);" +
        	    "        return originalPrompt(message, defaultText);" +  // Keep default behavior
        	    "    };" +
        	    "})();" +

        	    // Function to check visibility of inline errors
        	    "function isVisible(el) {" +
        	    "    var style = window.getComputedStyle(el);" +
        	    "    return (style.display !== 'none' && style.visibility !== 'hidden' && el.innerText.trim().length > 2);" +
        	    "}" +

        	    // Function to detect red error messages
        	    "function isErrorText(el) {" +
        	    "    var style = window.getComputedStyle(el);" +
        	    "    var textColor = style.color;" +
        	    "    var match = textColor.match(/rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)/);" +
        	    "    if (!match) return false;" +
        	    "    var r = parseInt(match[1]), g = parseInt(match[2]), b = parseInt(match[3]);" +
        	    "    return (r > 120 && r > g + 40 && r > b + 40);" +  // Checks for reddish color (error)
        	    "}" +

        	    // Function to check error attributes
        	    "function hasErrorAttributes(el) {" +
        	    "    return el.hasAttribute('aria-invalid') || el.getAttribute('role') === 'alert' || el.className.includes('error');" +
        	    "}" +

        	    // Function to check an element for errors
        	    "function checkElement(el) {" +
        	    "    if (isVisible(el) && (isErrorText(el) || hasErrorAttributes(el))) {" +
        	    "        detectedErrors.push(el.innerText.trim());" +
        	    "    }" +
        	    "}" +

        	    // Function to check for errors within an element
        	    "function checkForErrors(element) {" +
        	    "    if (!element) return;" +
        	    "    let allChildren = element.querySelectorAll('span, label, div, p');" +
        	    "    allChildren.forEach(checkElement);" +
        	    "}" +

        	    // Function to find an error message near an input field
        	    "function findErrorMessage(inputField) {" +
        	    "    let currentElement = inputField.parentElement;" +

        	    "    while (currentElement && currentElement.tagName.toLowerCase() !== 'body') {" +
        	    "        checkForErrors(currentElement);" +
        	    "        if (detectedErrors.length > 0) {" +
        	    "            return detectedErrors;" +  // Stop if error is found
        	    "        }" +

        	    "        if (currentElement.querySelector('input, textarea, select')) {" +
        	    "            break;" +  // Stop if another input field is found
        	    "        }" +

        	    "        currentElement = currentElement.parentElement;" +  // Move up the DOM tree
        	    "    }" +

        	    "    return detectedErrors;" +
        	    "}" +

        	    // Return detected alerts and inline error messages
        	    "return detectedErrors.concat(findErrorMessage(arguments[0]));";
        
        
//        String script =
//                "var detectedErrors = [];" +
//
//                "function isVisible(el) {" +
//                "    var style = window.getComputedStyle(el);" +
//                "    return (style.display !== 'none' && style.visibility !== 'hidden' && el.innerText.trim().length > 2);" +
//                "}" +
//
//                "function isErrorText(el) {" +
//                "    var style = window.getComputedStyle(el);" +
//                "    var textColor = style.color;" +
//                "    var match = textColor.match(/rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)/);" +
//                "    if (!match) return false;" +
//                "    var r = parseInt(match[1]), g = parseInt(match[2]), b = parseInt(match[3]);" +
//                "    return (r > 120 && r > g + 40 && r > b + 40);" +
//                "}" +
//
//                "function hasErrorAttributes(el) {" +
//                "    return el.hasAttribute('aria-invalid') || el.getAttribute('role') === 'alert' || el.className.includes('error');" +
//                "}" +
//
//                // Check for alert messages first
//                "var alertBox = document.querySelector('.alert, .error-alert, .alert-danger');" +
//                "if (alertBox && isVisible(alertBox)) {" +
//                "    detectedErrors.push(alertBox.innerText.trim());" +
//                "} else {" +
//                "    function checkElement(el) {" +
//                "        if (isVisible(el) && (isErrorText(el) || hasErrorAttributes(el))) {" +
//                "            detectedErrors.push(el.innerText.trim());" +
//                "        }" +
//                "    }" +
//
//                "    function checkForErrors(element) {" +
//                "        if (!element) return;" +
//                "        let allChildren = element.querySelectorAll('span, label, div, p');" +
//                "        allChildren.forEach(checkElement);" +
//                "    }" +
//
//                "    checkForErrors(arguments[0]);" +
//
//                "    if (detectedErrors.length === 0) {" +
//                "        var siblingDiv = arguments[0].nextElementSibling;" +
//                "        if (siblingDiv) checkForErrors(siblingDiv);" +
//                "    }" +
//                "}" +
//                "return detectedErrors;";

        for (Map.Entry<String, WebElement> entry : testCaseMap.entrySet()) {
            
        	System.out.println("\nNext Element Testing");
        	
        	String testCaseName = entry.getKey();
        	
        	System.out.println("TestCaseName : "+testCaseName);
        	
        	WebElement field = entry.getValue();
            
//        	By locator = getElementLocator(entry.getValue()); // Get locator before interacting
//            WebElement field = driver.findElement(locator); // Re-locate element
            
            String fieldType = field.getTagName();
            List<String> inputValues = inputs.getOrDefault(testCaseName, new ArrayList<>());
            
            System.out.println("Field : "+field);
            
            for (String input : inputValues) {
                try {
                    TestResult validationResult;
                    TestResult xssResult;

                    switch (fieldType) {
                        case "input":
                            String type = field.getAttribute("type");
                            if ("text".equals(type)) {
                            	
                            	System.out.println("\nTesting text field");
                            	
                                // 1. Validate input (standard validation)
                                validationResult = testTextField(testCaseName, field, input, js, script);
                                testResults.add(validationResult);
                                
                                // 2. Check for XSS vulnerabilities
//                                for (String xssPayload : getXssTestInputs()) {
//                                	
//                                	System.out.println("XSS Input : "+xssPayload);
//                                	
//                                    xssResult = testTextFieldForXSS(testCaseName, field, xssPayload);
//                                    testResults.add(xssResult);
//                                    
//	                                 // Re-locate all elements to prevent stale references
//	                                 testCaseMap = refreshTestCaseMap(testCaseMap);
//                                }
                            } else if ("email".equals(type)) {
                            	
                            	System.out.println("\nTesting email field");
                            	
                                validationResult = testEmailField(testCaseName, field, input, js, script);
                                testResults.add(validationResult);
                            } else if ("number".equals(type)) {
                            	
                            	System.out.println("\nTesting number field");
                            	
                                validationResult = testNumberField(testCaseName, field, input, js, script);
                                testResults.add(validationResult);
                            } 
//                            else {
//                                testResults.add(new TestResult(testCaseName, type, input, "N/A", "Unsupported input type", "Fail"));
//                            }
                            break;
                        case "textarea":
                        	
                        	System.out.println("\nTesting text-area field");
                        	
                            // 1. Validate textarea field
                            validationResult = testTextField(testCaseName, field, input, js, script);
                            testResults.add(validationResult);

//                            // 2. Check for XSS vulnerabilities in textarea
//                            for (String xssPayload : getXssTestInputs()) {
//                                xssResult = testTextFieldForXSS(testCaseName, field, xssPayload);
//                                testResults.add(xssResult);
//                                
//                                // Re-locate all elements to prevent stale references
//                                testCaseMap = refreshTestCaseMap(testCaseMap);
//                            }
                            break;
                        case "select":
                        	
                        	System.out.println("\nTesting select field");
                        	
                            validationResult = testSelectField(testCaseName, field, input);
                            testResults.add(validationResult);
                            break;
                        case "checkbox":
                        	
                        	System.out.println("\nTesting checkbox field");
                        	
                            validationResult = testCheckbox(testCaseName, field, "Checked".equals(input));
                            testResults.add(validationResult);
                            break;
                        case "radio":
                        	
                        	System.out.println("\nTesting radio field");
                        	
                            validationResult = testRadioButton(testCaseName, field);
                            testResults.add(validationResult);
                            break;
//                        default:
//                            testResults.add(new TestResult(testCaseName, fieldType, input, "N/A", "Unsupported field type", "Fail"));
                    }

                } 
                catch (Exception e) {
                    testResults.add(new TestResult(testCaseName, fieldType, input, "N/A", "Error: " + e.getMessage(), "Fail"));
                }
            }
        }
        return testResults;
    }
    
    
    
    
    
    private String takeScreenshot(String screenshotId) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotDirectory = "screenshots/"; // Adjust path as needed
            File destinationFile = new File(screenshotDirectory + screenshotId + ".png");

            Files.createDirectories(destinationFile.toPath().getParent());
            Files.copy(screenshot.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return destinationFile.getAbsolutePath(); // Return screenshot path
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void handleFailedTestCase(TestResult result) {
        if (result.getResult().equals("Fail")) {
            String uniqueKey = UUID.randomUUID().toString();
            result.setScreenshotKey(uniqueKey); // Store key in TestResult

            String screenshotPath = takeScreenshot(uniqueKey);
            if (screenshotPath != null) {
                // Get existing session-based screenshot map or create a new one
                Map<String, String> screenshotMap = (Map<String, String>) session.getAttribute("screenshotMap");
                if (screenshotMap == null) {
                    screenshotMap = new LinkedHashMap<>();
                }
                
                // Store screenshot path with its unique key
                screenshotMap.put(uniqueKey, screenshotPath);
                session.setAttribute("screenshotMap", screenshotMap);
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    public String checkForAlertMessage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1)); // Wait for alert
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertMessage = alert.getText(); // Capture alert message
            alert.accept(); // Accept alert to continue execution
            return alertMessage; // Return captured message
        } catch (TimeoutException e) {
            return null; // No alert found
        }
    }
    
    
    
    
    
    
    public TestResult testTextField(String testCaseName, WebElement field, String input, JavascriptExecutor js, String script) {
        field.clear();
        field.sendKeys(input);
        field.sendKeys(Keys.ENTER);
        
        System.out.println(("Entered text field"));
        
     // Step 1: Check for an alert message
        String alertMessage = checkForAlertMessage();
        boolean passed = alertMessage != null; // If alert is found, test is passed

        System.out.println("After alert check");
        
        List<String> errors = null;
        if (!passed) { // No alert found, check for inline errors
        	try {
        		
        		System.out.println("Befor js execution");
        		
                errors = (List<String>) js.executeScript(script, field);
                passed = errors != null && !errors.isEmpty(); // If errors are found, test is passed
                
                System.out.println("After js execution");
            } catch (UnhandledAlertException e) {
            	
            	System.out.println("Befor unhandles alter exception");
            	
                // Handle unexpected alert exception
                Alert alert = driver.switchTo().alert();
                alertMessage = alert.getText(); // Capture unexpected alert message
                alert.accept(); // Dismiss the alert
                passed = true; // Consider test passed due to the alert
                
                System.out.println("After unhandles alter exception");
            }
        }

        // Step 2: Determine the error message
        String errorMessage = (alertMessage != null) ? alertMessage : 
                              (errors != null && !errors.isEmpty() ? errors.get(0) : "No error");
        	
        System.out.println("End. Error Message : "+ errorMessage);
        
        TestResult result1 = new TestResult(testCaseName, "text", input, "Error Message Expected", passed ? errorMessage  : "No error", passed ? "Pass" : "Fail");
        
        handleFailedTestCase(result1);
        
        return result1;
    }

    public TestResult testNumberField(String testCaseName, WebElement field, String input, JavascriptExecutor js, String script) {
//        field.clear();
//        field.sendKeys(input);
//
//        List<String> errors = (List<String>) js.executeScript(script, field);
//        boolean passed = !errors.isEmpty();
//
//        return new TestResult(testCaseName, "number", input, "Error Message Expected", passed ? errors.get(0) : "No error", passed ? "Pass" : "Fail");
    	
    	field.clear();
        field.sendKeys(input);
        field.sendKeys(Keys.ENTER);

        // Step 1: Check for an alert first
        String alertMessage = checkForAlertMessage();
        boolean passed = alertMessage != null; // If an alert is found, test is considered passed

        List<String> errors = new ArrayList<>();

        if (!passed) { // No alert found, proceed with JavaScript execution
            try {
                errors = (List<String>) js.executeScript(script, field);
                passed = errors != null && !errors.isEmpty(); // If errors are found, test is passed
            } catch (UnhandledAlertException e) {
                // Handle unexpected alert exception
                Alert alert = driver.switchTo().alert();
                alertMessage = alert.getText(); // Capture unexpected alert message
                alert.accept(); // Dismiss the alert
                passed = true; // Consider test passed due to the alert
            }
        }

        // Step 2: Set the error message (alert has priority over inline errors)
        String errorMessage = (alertMessage != null) ? alertMessage : 
                              (!errors.isEmpty() ? errors.get(0) : "No error detected");
        
        
        TestResult result1 = new TestResult(testCaseName, "number", input, "Error Message Expected", errorMessage, passed ? "Pass" : "Fail");

//        if (result1.getResult().equals("Fail")) {
//        	String uniqueKey = UUID.randomUUID().toString();
//            result1.setScreenshotKey(uniqueKey); // Store key in TestResult
//
//            String screenshotPath = takeScreenshot(uniqueKey);
//            if (screenshotPath != null) {
//                // Get existing session-based screenshot map or create a new one
//                Map<String, String> screenshotMap = (Map<String, String>) session.getAttribute("screenshotMap");
//                if (screenshotMap == null) {
//                    screenshotMap = new LinkedHashMap<>();
//                }
//                
//                // Store screenshot path with its unique key
//                screenshotMap.put(uniqueKey, screenshotPath);
//                session.setAttribute("screenshotMap", screenshotMap);
//            }
//        }  
        
        handleFailedTestCase(result1);

        return result1;
    }

    public TestResult testEmailField(String testCaseName, WebElement field, String input, JavascriptExecutor js, String script) {
//        field.clear();
//        field.sendKeys(input);
//
//        List<String> errors = (List<String>) js.executeScript(script, field);
//        boolean passed = !errors.isEmpty();
//
//        return new TestResult(testCaseName, "email", input, "Error Message Expected", passed ? errors.get(0) : "No error", passed ? "Pass" : "Fail");
    	
    	field.clear();
        field.sendKeys(input);
        field.sendKeys(Keys.ENTER);

        // Step 1: Check for an alert first
        String alertMessage = checkForAlertMessage();
        boolean passed = alertMessage != null; // If an alert is found, test is considered passed

        List<String> errors = new ArrayList<>();

        if (!passed) { // No alert found, proceed with JavaScript execution
            try {
                errors = (List<String>) js.executeScript(script, field);
                passed = errors != null && !errors.isEmpty(); // If errors are found, test is passed
            } catch (UnhandledAlertException e) {
                // Handle unexpected alert exception
                Alert alert = driver.switchTo().alert();
                alertMessage = alert.getText(); // Capture unexpected alert message
                alert.accept(); // Dismiss the alert
                passed = true; // Consider test passed due to the alert
            }
        }

        // Step 2: Set the error message (alert has priority over inline errors)
        String errorMessage = (alertMessage != null) ? alertMessage : 
                              (!errors.isEmpty() ? errors.get(0) : "No error");
        
        TestResult result1 = new TestResult(testCaseName, "email", input, "Error Message Expected", errorMessage, passed ? "Pass" : "Fail");
        
        handleFailedTestCase(result1);
        
        return result1;
    }

    public TestResult testSelectField(String testCaseName, WebElement field, String input) {
        Select dropdown = new Select(field);
        try {
            dropdown.selectByVisibleText(input);
            return new TestResult(testCaseName, "select", input, input, input, "Pass");
        } catch (Exception e) {
            return new TestResult(testCaseName, "select", input, input, "Not selectable", "Fail");
        }
        
        
//        Select dropdown = new Select(field);
//
//        // Get all available options as text
//        List<WebElement> options = dropdown.getOptions();
//        
//        if (options.isEmpty()) {
//            return new TestResult(testCaseName, "select", input, input, "No options available", "Fail");
//        }
//
//        // Shuffle options and pick 5 randomly (or fewer if less than 5 exist)
//        Collections.shuffle(options, new Random());
//        int limit = Math.min(5, options.size());
//        List<String> randomFiveOptions = options.subList(0, limit).stream()
//                                                .map(WebElement::getText)
//                                                .collect(Collectors.toList());
//
//        try {
//            // Check if the input exists in the randomly picked 5 options
//            if (randomFiveOptions.contains(input)) {
//                dropdown.selectByVisibleText(input);
//                return new TestResult(testCaseName, "select", input, input, input, "Pass");
//            } else {
//                return new TestResult(testCaseName, "select", input, input, "Not selectable (Not in random 5)", "Fail");
//            }
//        } catch (Exception e) {
//            return new TestResult(testCaseName, "select", input, input, "Error during selection", "Fail");
//        }
    }

    public TestResult testCheckbox(String testCaseName, WebElement field, boolean shouldBeChecked) {
        boolean isChecked = field.isSelected();

        if (shouldBeChecked && !isChecked) {
            field.click();
            isChecked = field.isSelected();
        }

        return new TestResult(testCaseName, "checkbox", shouldBeChecked ? "Checked" : "Unchecked",
                shouldBeChecked ? "Checked" : "Unchecked", isChecked ? "Checked" : "Unchecked",
                isChecked == shouldBeChecked ? "Pass" : "Fail");
    }

    public TestResult testRadioButton(String testCaseName, WebElement field) {
        boolean isSelected = field.isSelected();
        if (!isSelected) {
            field.click();
            isSelected = field.isSelected();
        }

        return new TestResult(testCaseName, "radio", "Selected", "Selected", isSelected ? "Selected" : "Not Selected",
                isSelected ? "Pass" : "Fail");
    }
    
    private TestResult testTextFieldForXSS(String testCaseName, WebElement field, String input) {
    	
    	System.out.println("XSS TESTINGS STARTED   "+field.getAttribute("type"));
    	
    	try {
//            By locator = getElementLocator(field); // Get locator before interacting with the element
            
//            // Re-locate the field before interacting with it
//            field = driver.findElement(locator);
//            
//            driver.switchTo().defaultContent();
            
            field.clear();
            field.sendKeys(input);
            field.sendKeys(Keys.TAB); // Trigger potential execution

            boolean xssDetected = false;
            String actualOutput = "No XSS Detected";

            // Check if alert is triggered
            try {
                Alert alert = driver.switchTo().alert();
                actualOutput = "XSS Alert Triggered";
                alert.accept();
                xssDetected = true;
                
                System.out.println("XSS TERIGERRED");
                
            } catch (NoAlertPresentException ignored) {}
            
            
//            // Re-locate the field before DOM modification check
//            driver.switchTo().defaultContent();
//            field = driver.findElement(locator);
            
            // Check for suspicious DOM changes
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String script = 
                "var suspiciousChanges = false;" +
                "if (document.body.innerHTML.includes('<script') || document.body.innerHTML.includes('onerror')) {" +
                "    suspiciousChanges = true;" +
                "}" +
                "return suspiciousChanges;";
            
            boolean domChanged = (Boolean) js.executeScript(script);
            
            System.out.println("XSS TERIGERRED : DOM CHANGED :"+ domChanged);
            
            if (domChanged) {
                actualOutput = "Potential XSS in DOM";
                xssDetected = true;
            }

            String expectedOutput = "Field should handle input safely";
            String result = xssDetected ? "Fail" : "Pass";
            
            System.out.println("Results : "+result);
            return new TestResult(testCaseName, "XSS text", input, expectedOutput, actualOutput, result);
        } 
    	catch (StaleElementReferenceException e) {
    		System.out.println("Exception Occurred");
            return new TestResult(testCaseName, "XSS text", input, "N/A", "Error: Stale Element", "Fail");
        }
    	
    	
    	
//        field.clear();
//        field.sendKeys(input);
//        field.sendKeys(Keys.TAB);  // Trigger validation
//
//        // Check for XSS execution by detecting alerts
//        boolean xssDetected = false;
//        String actualOutput = "No XSS Detected";
//        		
//        try {
//            driver.switchTo().alert().accept();
//            xssDetected = true;
//        } catch (NoAlertPresentException ignored) {}
//        
//        
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        String script = 
//            "var suspiciousChanges = false;" +
//            "if (document.body.innerHTML.includes('<script') || document.body.innerHTML.includes('onerror')) {" +
//            "    suspiciousChanges = true;" +
//            "}" +
//            "return suspiciousChanges;";
//        
//        boolean domChanged = (Boolean) js.executeScript(script);
//
//        if (domChanged) {
//            actualOutput = "Potential XSS in DOM";
//            xssDetected = true;
//        }
//        
//
//        String expectedOutput = "Field should handle input safely";
//        actualOutput = xssDetected ? "XSS Alert Triggered" : "No XSS Detected";
//        String result = xssDetected ? "Fail" : "Pass";
//
//        return new TestResult(testCaseName, "XSS text", input, expectedOutput, actualOutput, result);
    }
    
    
    private Map<String, WebElement> refreshTestCaseMap(Map<String, WebElement> oldMap) {
        Map<String, WebElement> newMap = new LinkedHashMap<>();
        
        for (Map.Entry<String, WebElement> entry : oldMap.entrySet()) {
            try {
                By locator = getElementLocator(entry.getValue());
                WebElement newElement = driver.findElement(locator);
                newMap.put(entry.getKey(), newElement);
            } catch (Exception e) {
                System.out.println("Warning: Unable to re-locate element for " + entry.getKey());
            }
        }
        return newMap;
    }
    
    
    private By getElementLocator(WebElement element) {
        String tagName = element.getTagName();
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");

        if (id != null && !id.isEmpty()) {
            return By.id(id);
        } else if (name != null && !name.isEmpty()) {
            return By.name(name);
        } else {
            return By.tagName(tagName);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<TestResult> runTests1(Map<String, WebElement> testCaseMap, Map<String, List<String>> inputs) {
        List<TestResult> results = new ArrayList<>();

        for (Map.Entry<String, WebElement> entry : testCaseMap.entrySet()) {
            String testCaseName = entry.getKey();
            WebElement field = entry.getValue();
            String fieldType = field.getAttribute("type");

            // Get the user-provided inputs for this test case
            List<String> testInputs = inputs.getOrDefault(testCaseName, new ArrayList<>());
            
        	System.out.println("Running test for: " + testCaseName + " | Field Type: " + fieldType + " | Inputs: " + testInputs);
            
            for (String input : testInputs) {
                if (fieldType.equals("text")) {
                    results.add(testTextField(field, testCaseName, input, true));
                } 
                else if (fieldType.equals("email")) {
                    results.add(testEmailField(field, testCaseName, input, true));
                } 
                else if (fieldType.equals("password")) {
                    results.add(testPasswordField(field, testCaseName, input, true));
                } 
//                    else if (fieldType.equals("checkbox")) {
//                        results.add(testCheckbox(field, fieldType));
//                    } 
//                    else if (field.getTagName().equals("select")) {
//                        results.add(testDropdown(field, fieldType));
//                    }
            }
        }
        
        return results;
    }
    
    
    
    
    
    
    
    
    
//    public List<TestResult> runTests(List<WebElement> fields, Map<String, List<String>> inputs) {
//    	
//    	this.inputsCollection = inputs;
//    	
//    	List<TestResult> results = new ArrayList<>();
//    	
//    	for (WebElement field : fields) {
//            String fieldType = field.getAttribute("type");
//            String fieldName = field.getAttribute("name");
//            
//            if(!fieldName.trim().isEmpty()) {
//            	System.out.println("=================>FieldName : "+fieldName+" +++++++++++> Field Type : "+fieldType);
//                
//                if (fieldType.equals("text")) {
//                	System.out.println("=================>"+1);
//                    results.add(testTextField(field, fieldType, fieldName, true));
//                } 
//                else if (fieldType.equals("email")) {
//                	System.out.println("=================>"+2);
//                    results.add(testEmailField(field, fieldType, fieldName, true));
//                    System.out.println(results);
//                } 
//                else if (fieldType.equals("password")) {
//                	System.out.println("=================>"+3);
//                    results.add(testPasswordField(field, fieldType, fieldName, true));
//                } 
//                else if (fieldType.equals("checkbox")) {
//                	System.out.println("=================>"+4);
//                    results.add(testCheckbox(field, fieldType, fieldName));
//                } 
//                else if (field.getTagName().equals("select")) {
//                	System.out.println("=================>"+5);
//                    results.add(testDropdown(field, fieldType, fieldName));
//                }
//            }
//            else if(field.getTagName().equals("a") && field.getAttribute("href")!=null) {
//            	results.add(checkLink(field.getAttribute("href")));
//            }
//        }
//    	
//    	return results;
//    }
    
    
    private TestResult testTextField(WebElement field, String fieldType, String testCaseName, boolean isExecutedForFirst) {
    	
    	if(isExecutedForFirst) {
    		String method = "    public TestResult testTextField(WebElement field, String fieldType, String fieldName) {\n"
    		        + "        return validateField(field, fieldType, fieldName, \"Test123\", \"Test123\", \"Text field should store entered text.\");\n"
    		        + "    }\n";
//    		executedMethods.add(method);
    	}
    	
    	List<String> inputs = getPredefinedInputs(fieldType);
    	
    	for(String givenInput : inputs) {
    		return validateField(field, fieldType, testCaseName, givenInput, "Error message should appear", "Text field should store entered text.", isExecutedForFirst);
    	}
    	return null;
    }

    private TestResult testEmailField(WebElement field, String fieldType, String testCaseName, boolean isExecutedForFirst) {
    	
    	if(isExecutedForFirst) {
    		String method = "    public TestResult testEmailField(WebElement field, String fieldType, String fieldName) {\n"
    		        + "        return validateField(field, fieldType, fieldName, \"Test123\", \"Test123\", \"Text field should store entered text.\");\n"
    		        + "    }\n";
//    		executedMethods.add(method);
    	}
    	
    	List<String> inputs = getPredefinedInputs(fieldType);
    	
    	for(String givenInput : inputs) {
    		return validateField(field, fieldType, testCaseName, givenInput, "Error message should appear", "Email should show an error for invalid input.", isExecutedForFirst);
    	}
    	return null;
    }

    private TestResult testPasswordField(WebElement field, String fieldType, String testCaseName, boolean isExecutedForFirst) {
    	
    	if(isExecutedForFirst) {
    		String method = "    public TestResult testPasswordField(WebElement field, String fieldType, String fieldName) {\n"
    		        + "        return validateField(field, fieldType, fieldName, \"Test123\", \"Test123\", \"Text field should store entered text.\");\n"
    		        + "    }\n";
//    		executedMethods.add(method);
    	}
    	
    	List<String> inputs = getPredefinedInputs(fieldType);
    	
    	for(String givenInput : inputs) {
    		return validateField(field, fieldType, testCaseName, givenInput, "Error message should appear", "Password validation should enforce strong passwords.", isExecutedForFirst);
    	}
        return null;
    }
    
//    private TestResult validateField(WebElement field,String fieldType, String fieldName, String givenInput, String expectedError, String description) {
//    	String testCaseName = "Validate " + fieldType + " Field";
//    	
//    	field.clear();
//        field.sendKeys(givenInput);
//        field.sendKeys(Keys.ENTER);
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
//        try {
//            WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='"+fieldName+"']/ancestor::*[1]//*[contains(@class, 'error') or contains(@class, 'invalid')]")));
//
////            String actualError = errorElement.getText();
////            String result = actualError.equals(expectedError) ? "Passed" : "Failed";
//            
//            String actualError = "Error is not shown";
//            String result = "Failed";
//            if (errorElement.isDisplayed()) {
//                result = "Passed";
//                actualError = "Error is not shown";
//            }
//            return new TestResult(testCaseName, fieldType, fieldName, givenInput, expectedError, actualError, result);
//        } 
//        catch (TimeoutException e) {
//            return new TestResult(testCaseName, fieldType, fieldName,  givenInput, expectedError, "No error displayed", "Failed");
//        }
//    }
    
    
    
    private TestResult validateField(WebElement field, String fieldType, String testCaseName, String givenInput, String expectedError, String description, boolean isExecutedForFirst) {
        
//    	if(isExecutedForFirst) {
//    		String method = "    public TestResult test_" + fieldName + "(WebDriver driver) {\n"
//    		        + "        String testCaseName = \"Validate " + fieldType + " Field\";\n"
//    		        + "        WebElement field = driver.findElement(By.name(\"" + fieldName + "\"));\n"
//    		        + "        field.clear();\n"
//    		        + "        field.sendKeys(\"" + givenInput + "\");\n"
//    		        + "        field.sendKeys(Keys.ENTER);\n\n"
//    		        
//    		        + "        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));\n"
//    		        + "        JavascriptExecutor js = (JavascriptExecutor) driver;\n\n"
//    		        
//    		        + "        try {\n"
//    		        + "            WebElement parentDiv = (WebElement) js.executeScript(\"return arguments[0].closest('div');\", field);\n\n"
//    		        
//    		        + "            String script = \"var parentDiv = arguments[0];\" +\n"
//    		        + "                \"var detectedErrors = [];\" +\n"
//    		        + "                \"function isVisible(el) {\" +\n"
//    		        + "                \"    var style = window.getComputedStyle(el);\" +\n"
//    		        + "                \"    return (style.display !== 'none' && style.visibility !== 'hidden' && el.innerText.trim().length > 2);\" +\n"
//    		        + "                \"}\" +\n"
//    		        + "                \"function isErrorText(el) {\" +\n"
//    		        + "                \"    var style = window.getComputedStyle(el);\" +\n"
//    		        + "                \"    var textColor = style.color;\" +\n"
//    		        + "                \"    var match = textColor.match(/rgb\\\\((\\\\d+),\\\\s*(\\\\d+),\\\\s*(\\\\d+)\\\\)/);\" +\n"
//    		        + "                \"    if (!match) return false;\" +\n"
//    		        + "                \"    var r = parseInt(match[1]), g = parseInt(match[2]), b = parseInt(match[3]);\" +\n"
//    		        + "                \"    return (r > 120 && r > g + 40 && r > b + 40);\" +\n"
//    		        + "                \"}\" +\n"
//    		        + "                \"function hasErrorAttributes(el) {\" +\n"
//    		        + "                \"    return el.hasAttribute('aria-invalid') || el.getAttribute('role') === 'alert' || el.className.includes('error');\" +\n"
//    		        + "                \"}\" +\n"
//    		        + "                \"function checkElement(el) {\" +\n"
//    		        + "                \"    if (isVisible(el) && (isErrorText(el) || hasErrorAttributes(el))) {\" +\n"
//    		        + "                \"        detectedErrors.push(el.innerText.trim());\" +\n"
//    		        + "                \"    }\" +\n"
//    		        + "                \"}\" +\n"
//    		        + "                \"function checkForErrors(element) {\" +\n"
//    		        + "                \"    if (!element) return;\" +\n"
//    		        + "                \"    let allChildren = element.querySelectorAll('span, label, div, p');\" +\n"
//    		        + "                \"    allChildren.forEach(checkElement);\" +\n"
//    		        + "                \"}\" +\n"
//    		        + "                \"checkForErrors(parentDiv);\" +\n"
//    		        + "                \"if (detectedErrors.length === 0) {\" +\n"
//    		        + "                \"    var siblingDiv = parentDiv.nextElementSibling;\" +\n"
//    		        + "                \"    if (siblingDiv) checkForErrors(siblingDiv);\" +\n"
//    		        + "                \"}\" +\n"
//    		        + "                \"return detectedErrors;\";\n\n"
//
//    		        + "            List<String> errorMessages = (List<String>) js.executeScript(script, parentDiv);\n"
//    		        + "            String actualError = errorMessages.isEmpty() ? \"No error displayed\" : \"Error message is displayed\";\n"
//    		        + "            String result = !actualError.equals(\"No error displayed\") ? \"Passed\" : \"Failed\";\n"
//    		        + "            return new TestResult(testCaseName, \"" + fieldType + "\", \"" + fieldName + "\", \"" + givenInput + "\", \"" + expectedError + "\", actualError, result);\n"
//    		        + "        } catch (Exception e) {\n"
//    		        + "            return new TestResult(testCaseName, \"" + fieldType + "\", \"" + fieldName + "\", \"" + givenInput + "\", \"" + expectedError + "\", \"No error displayed\", \"Failed\");\n"
//    		        + "        }\n"
//    		        + "    }\n";
//    		
//    		executedMethods.add(method);
//    	}
//    	else {
//    		
//    	}
    	
//    	String testCaseName = "Validate " + fieldType + " Field";
        
        field.clear();
        field.sendKeys(givenInput);
        field.sendKeys(Keys.ENTER);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Locate the parent div of the input field
            WebElement parentDiv = (WebElement) js.executeScript("return arguments[0].closest('div');", field);

            // JavaScript to detect visible error messages in the parent div and adjacent sibling div
            String script =
                "var parentDiv = arguments[0];" +
                "var detectedErrors = [];" +

                "function isVisible(el) {" +
                "    var style = window.getComputedStyle(el);" +
                "    return (style.display !== 'none' && style.visibility !== 'hidden' && el.innerText.trim().length > 2);" +
                "}" +

                "function isErrorText(el) {" +
                "    var style = window.getComputedStyle(el);" +
                "    var textColor = style.color;" +
                "    var match = textColor.match(/rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)/);" +
                "    if (!match) return false;" +
                "    var r = parseInt(match[1]), g = parseInt(match[2]), b = parseInt(match[3]);" +
                "    return (r > 120 && r > g + 40 && r > b + 40); /* Adjusted red color detection */" +
                "}" +

                "function hasErrorAttributes(el) {" +
                "    return el.hasAttribute('aria-invalid') || el.getAttribute('role') === 'alert' || el.className.includes('error');" +
                "}" +

                "function checkElement(el) {" +
                "    if (isVisible(el) && (isErrorText(el) || hasErrorAttributes(el))) {" +
                "        detectedErrors.push(el.innerText.trim());" +
                "    }" +
                "}" +

                "function checkForErrors(element) {" +
                "    if (!element) return;" +
                "    let allChildren = element.querySelectorAll('span, label, div, p');" +
                "    allChildren.forEach(checkElement);" +
                "}" +

                "checkForErrors(parentDiv);" +

                "if (detectedErrors.length === 0) {" +
                "    var siblingDiv = parentDiv.nextElementSibling;" +
                "    if (siblingDiv) checkForErrors(siblingDiv);" +
                "}" +

                "return detectedErrors;";

            // Execute script and retrieve detected errors
            List<String> errorMessages = (List<String>) js.executeScript(script, parentDiv);

            String actualError = errorMessages.isEmpty() ? "No error displayed" : "Error message is displayed";
            String result = !actualError.equals("No error displayed") ? "Passed" : "Failed";

            return new TestResult(testCaseName, fieldType, givenInput, expectedError, actualError, result);
        } 
        catch (Exception e) {
            return new TestResult(testCaseName, fieldType, givenInput, expectedError, "No error displayed", "Failed");
        }
    }
    
    
    
    private TestResult testDropdown(WebElement field, String fieldType) {
    	String testCaseName = "Validate Dropdown Selection";
    	
    	Select dropdown = new Select(field);
        List<WebElement> options = dropdown.getOptions();
        if (options.size() > 1) {
            String expectedOption = options.get(1).getText();
            dropdown.selectByIndex(1);
            String actualOption = dropdown.getFirstSelectedOption().getText();
            String result = expectedOption.equals(actualOption) ? "Passed" : "Failed";
            return new TestResult(testCaseName, fieldType, "Select Option 2", expectedOption, actualOption, result);
        }
        return new TestResult(testCaseName, fieldType, "Select Option", "At least one option", "No options available", "Failed");
    }

    
    private TestResult testCheckbox(WebElement field, String fieldType) {
    	String testCaseName = "Validate Checkbox Selection";
    	
    	boolean initiallySelected = field.isSelected();
        field.click();
        boolean afterClick = field.isSelected();
        String result = initiallySelected != afterClick ? "Passed" : "Failed";
        return new TestResult(testCaseName, fieldType, "Click", "Toggle state", afterClick ? "Checked" : "Unchecked", result);
    }
    
    
    //Sample Piece
	public static TestResult testTextField(WebElement field) {
        String testCaseName = "Validate Text Field";
        String fieldType = field.getAttribute("type");
        String fieldName = field.getAttribute("name");

        String givenInput = "Test123";
        String expectedOutput = givenInput;

        field.clear();
        field.sendKeys(givenInput);
        String actualOutput = field.getAttribute("value");

        String result = expectedOutput.equals(actualOutput) ? "Passed" : "Failed";

        return new TestResult(testCaseName, fieldType, givenInput, expectedOutput, actualOutput, result);
    }
	
	
	private static TestResult checkLink(String linkUrl) {
    	
	    try {
	        URL url = new URL(linkUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
//	        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
//	        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//	        connection.setRequestProperty("Referer", "https://www.google.com/");
//	        connection.setInstanceFollowRedirects(true); 
	        connection.connect();
	        int responseCode = connection.getResponseCode();

	        if (responseCode >= 400) {
	        	
	        	System.out.println("❌ Broken: " + linkUrl + " (Status: " + responseCode + ")");
	        	return new TestResult("Broken Link Test","Link", "Try to laod link","The page loaded", responseCode + "page not found" ,"Failed");
	            
	        } else {
	        	
	        	System.out.println("✅ Working: " + linkUrl);
	        	return new TestResult("Broken Link Test","Link", "Try to laod link","The page loaded", "Page loaded successfully" ,"Passed");
	            
	        }
	    } catch (Exception e) {
	    	
	    	
	        System.out.println("❌ Error: " + linkUrl);
	        return new TestResult("Broken Link Test","Link", "Try to laod link","The page loaded", "page not found" ,"Failed");
	    }
	}
    
    
//    private static TestResult runBasicTest(WebElement field, String givenInput) {
//        String testCaseName = "Validate " + field.getAttribute("type") + " Field";
//        String fieldType = field.getAttribute("type");
//        String fieldName = field.getAttribute("name");
//
//        field.clear();
//        field.sendKeys(givenInput);
//        String actualOutput = field.getAttribute("value");
//
//        String result = givenInput.equals(actualOutput) ? "Passed" : "Failed";
//
//        return new TestResult(testCaseName, fieldType, fieldName, givenInput, givenInput, actualOutput, result);
//    }
    
    
//    WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class, 'error') or contains(@class, 'invalid')]")));
//    																				   By.xpath("//input[@name='yourFieldName']/parent::*//span[contains(@class, 'error') or contains(@class, 'invalid')]") 
//    																				    By.xpath("//input[@name='"+fieldName+"']/ancestor::*[1]//*[self::span or self::div or self::p][contains(@class, 'error') or contains(@class, 'invalid')]")
//    								  ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='"+fieldName+"']/ancestor::*[1]//*[contains(@class, 'error') or contains(@class, 'invalid')]")))

}
