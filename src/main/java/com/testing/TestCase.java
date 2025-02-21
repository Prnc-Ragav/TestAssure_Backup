package com.testing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestCase {
	
	private WebDriver driver;
	
	private List<String> executedMethods = new ArrayList<>();
	private List<String> mainMethod = new ArrayList<>();

    public TestCase(WebDriver driver) {
        this.driver = driver;
    }
    
    public List<String> getExecutedMethods() {
        return executedMethods;
    }
    
    public List<TestResult> runTests(List<WebElement> fields) {
    	List<TestResult> results = new ArrayList<>();
    	
    	System.out.println(">>>>>>>>>>>>>>>>Size"+fields.size());
    	for (WebElement field : fields) {
            String fieldType = field.getAttribute("type");
            String fieldName = field.getAttribute("name");
            
            if(!fieldName.trim().isEmpty()) {
            	System.out.println("=================>FieldName : "+fieldName+" +++++++++++> Field Type : "+fieldType);
                
                if (fieldType.equals("text")) {
                	System.out.println("=================>"+1);
                    results.add(testTextField(field, fieldType, fieldName, true));
                } 
                else if (fieldType.equals("email")) {
                	System.out.println("=================>"+2);
                    results.add(testEmailField(field, fieldType, fieldName, true));
                    System.out.println(results);
                } 
                else if (fieldType.equals("password")) {
                	System.out.println("=================>"+3);
                    results.add(testPasswordField(field, fieldType, fieldName, true));
                } 
                else if (fieldType.equals("checkbox")) {
                	System.out.println("=================>"+4);
                    results.add(testCheckbox(field, fieldType, fieldName));
                } 
                else if (field.getTagName().equals("select")) {
                	System.out.println("=================>"+5);
                    results.add(testDropdown(field, fieldType, fieldName));
                }
            }
        }
    	
    	return results;
    }
    
    
    private TestResult testTextField(WebElement field, String fieldType, String fieldName, boolean isExecutedForFirst) {
    	
    	if(isExecutedForFirst) {
    		String method = "    public TestResult test_" + fieldName + "(WebElement field, String fieldType, String fieldName) {\n"
    		        + "        return validateField(field, fieldType, fieldName, \"Test123\", \"Test123\", \"Text field should store entered text.\");\n"
    		        + "    }\n";
    		executedMethods.add(method);
    	}
    	
        return validateField(field, fieldType, fieldName, "Test123", "Test123", "Text field should store entered text.", isExecutedForFirst);
    }

    private TestResult testEmailField(WebElement field, String fieldType, String fieldName, boolean isExecutedForFirst) {
    	
    	if(isExecutedForFirst) {
    		String method = "    public TestResult test_" + fieldName + "(WebElement field, String fieldType, String fieldName) {\n"
    		        + "        return validateField(field, fieldType, fieldName, \"Test123\", \"Test123\", \"Text field should store entered text.\");\n"
    		        + "    }\n";
    		executedMethods.add(method);
    	}
    	
        return validateField(field, fieldType, fieldName, "invalid-email", "Please enter a valid email address.", "Email should show an error for invalid input.", isExecutedForFirst);
    }

    private TestResult testPasswordField(WebElement field, String fieldType, String fieldName, boolean isExecutedForFirst) {
    	
    	if(isExecutedForFirst) {
    		String method = "    public TestResult testPasswordField(WebElement field, String fieldType, String fieldName) {\n"
    		        + "        return validateField(field, fieldType, fieldName, \"Test123\", \"Test123\", \"Text field should store entered text.\");\n"
    		        + "    }\n";
    		executedMethods.add(method);
    	}
    	
        return validateField(field, fieldType, fieldName, "WeakPass", "Password must contain a special character.", "Password validation should enforce strong passwords.", isExecutedForFirst);
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
    
    
    
    private TestResult validateField(WebElement field, String fieldType, String fieldName, String givenInput, String expectedError, String description, boolean isExecutedForFirst) {
        
    	if(isExecutedForFirst) {
    		String method = "    public TestResult test_" + fieldName + "(WebDriver driver) {\n"
    		        + "        String testCaseName = \"Validate " + fieldType + " Field\";\n"
    		        + "        WebElement field = driver.findElement(By.name(\"" + fieldName + "\"));\n"
    		        + "        field.clear();\n"
    		        + "        field.sendKeys(\"" + givenInput + "\");\n"
    		        + "        field.sendKeys(Keys.ENTER);\n\n"
    		        
    		        + "        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));\n"
    		        + "        JavascriptExecutor js = (JavascriptExecutor) driver;\n\n"
    		        
    		        + "        try {\n"
    		        + "            WebElement parentDiv = (WebElement) js.executeScript(\"return arguments[0].closest('div');\", field);\n\n"
    		        
    		        + "            String script = \"var parentDiv = arguments[0];\" +\n"
    		        + "                \"var detectedErrors = [];\" +\n"
    		        + "                \"function isVisible(el) {\" +\n"
    		        + "                \"    var style = window.getComputedStyle(el);\" +\n"
    		        + "                \"    return (style.display !== 'none' && style.visibility !== 'hidden' && el.innerText.trim().length > 2);\" +\n"
    		        + "                \"}\" +\n"
    		        + "                \"function isErrorText(el) {\" +\n"
    		        + "                \"    var style = window.getComputedStyle(el);\" +\n"
    		        + "                \"    var textColor = style.color;\" +\n"
    		        + "                \"    var match = textColor.match(/rgb\\\\((\\\\d+),\\\\s*(\\\\d+),\\\\s*(\\\\d+)\\\\)/);\" +\n"
    		        + "                \"    if (!match) return false;\" +\n"
    		        + "                \"    var r = parseInt(match[1]), g = parseInt(match[2]), b = parseInt(match[3]);\" +\n"
    		        + "                \"    return (r > 120 && r > g + 40 && r > b + 40);\" +\n"
    		        + "                \"}\" +\n"
    		        + "                \"function hasErrorAttributes(el) {\" +\n"
    		        + "                \"    return el.hasAttribute('aria-invalid') || el.getAttribute('role') === 'alert' || el.className.includes('error');\" +\n"
    		        + "                \"}\" +\n"
    		        + "                \"function checkElement(el) {\" +\n"
    		        + "                \"    if (isVisible(el) && (isErrorText(el) || hasErrorAttributes(el))) {\" +\n"
    		        + "                \"        detectedErrors.push(el.innerText.trim());\" +\n"
    		        + "                \"    }\" +\n"
    		        + "                \"}\" +\n"
    		        + "                \"function checkForErrors(element) {\" +\n"
    		        + "                \"    if (!element) return;\" +\n"
    		        + "                \"    let allChildren = element.querySelectorAll('span, label, div, p');\" +\n"
    		        + "                \"    allChildren.forEach(checkElement);\" +\n"
    		        + "                \"}\" +\n"
    		        + "                \"checkForErrors(parentDiv);\" +\n"
    		        + "                \"if (detectedErrors.length === 0) {\" +\n"
    		        + "                \"    var siblingDiv = parentDiv.nextElementSibling;\" +\n"
    		        + "                \"    if (siblingDiv) checkForErrors(siblingDiv);\" +\n"
    		        + "                \"}\" +\n"
    		        + "                \"return detectedErrors;\";\n\n"

    		        + "            List<String> errorMessages = (List<String>) js.executeScript(script, parentDiv);\n"
    		        + "            String actualError = errorMessages.isEmpty() ? \"No error displayed\" : \"Error message is displayed\";\n"
    		        + "            String result = !actualError.equals(\"No error displayed\") ? \"Passed\" : \"Failed\";\n"
    		        + "            return new TestResult(testCaseName, \"" + fieldType + "\", \"" + fieldName + "\", \"" + givenInput + "\", \"" + expectedError + "\", actualError, result);\n"
    		        + "        } catch (Exception e) {\n"
    		        + "            return new TestResult(testCaseName, \"" + fieldType + "\", \"" + fieldName + "\", \"" + givenInput + "\", \"" + expectedError + "\", \"No error displayed\", \"Failed\");\n"
    		        + "        }\n"
    		        + "    }\n";
    		
    		executedMethods.add(method);
    	}
    	else {
    		
    	}
    	
    	String testCaseName = "Validate " + fieldType + " Field";
        
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

            return new TestResult(testCaseName, fieldType, fieldName, givenInput, expectedError, actualError, result);
        } 
        catch (Exception e) {
            return new TestResult(testCaseName, fieldType, fieldName, givenInput, expectedError, "No error displayed", "Failed");
        }
    }
    
    
    
    private TestResult testDropdown(WebElement field, String fieldType, String fieldName) {
    	String testCaseName = "Validate Dropdown Selection";
    	
    	Select dropdown = new Select(field);
        List<WebElement> options = dropdown.getOptions();
        if (options.size() > 1) {
            String expectedOption = options.get(1).getText();
            dropdown.selectByIndex(1);
            String actualOption = dropdown.getFirstSelectedOption().getText();
            String result = expectedOption.equals(actualOption) ? "Passed" : "Failed";
            return new TestResult(testCaseName, fieldType, fieldName, "Select Option 2", expectedOption, actualOption, result);
        }
        return new TestResult(testCaseName, fieldType, fieldName, "Select Option", "At least one option", "No options available", "Failed");
    }

    
    private TestResult testCheckbox(WebElement field, String fieldType, String fieldName) {
    	String testCaseName = "Validate Checkbox Selection";
    	
    	boolean initiallySelected = field.isSelected();
        field.click();
        boolean afterClick = field.isSelected();
        String result = initiallySelected != afterClick ? "Passed" : "Failed";
        return new TestResult(testCaseName, fieldType, fieldName, "Click", "Toggle state", afterClick ? "Checked" : "Unchecked", result);
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

        return new TestResult(testCaseName, fieldType, fieldName, givenInput, expectedOutput, actualOutput, result);
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
