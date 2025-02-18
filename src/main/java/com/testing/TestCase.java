package com.testing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestCase {
	
	private WebDriver driver;

    public TestCase(WebDriver driver) {
        this.driver = driver;
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
                    results.add(testTextField(field, fieldType, fieldName));
                } 
                else if (fieldType.equals("email")) {
                	System.out.println("=================>"+2);
                    results.add(testEmailField(field, fieldType, fieldName));
                    System.out.println(results);
                } 
                else if (fieldType.equals("password")) {
                	System.out.println("=================>"+3);
                    results.add(testPasswordField(field, fieldType, fieldName));
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
    
    
    private TestResult testTextField(WebElement field, String fieldType, String fieldName) {
        return validateField(field, fieldType, fieldName, "Test123", "Test123", "Text field should store entered text.");
    }

    private TestResult testEmailField(WebElement field, String fieldType, String fieldName) {
        return validateField(field, fieldType, fieldName, "invalid-email", "Please enter a valid email address.", "Email should show an error for invalid input.");
    }

    private TestResult testPasswordField(WebElement field, String fieldType, String fieldName) {
        return validateField(field, fieldType, fieldName, "WeakPass", "Password must contain a special character.", "Password validation should enforce strong passwords.");
    }
    
    private TestResult validateField(WebElement field,String fieldType, String fieldName, String givenInput, String expectedError, String description) {
    	String testCaseName = "Validate " + fieldType + " Field";
    	
    	field.clear();
        field.sendKeys(givenInput);
        field.sendKeys(Keys.ENTER);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='"+fieldName+"']/ancestor::*[1]//*[contains(@class, 'error') or contains(@class, 'invalid')]")));

//            String actualError = errorElement.getText();
//            String result = actualError.equals(expectedError) ? "Passed" : "Failed";
            
            String actualError = "Error is not shown";
            String result = "Failed";
            if (errorElement.isDisplayed()) {
                result = "Passed";
                actualError = "Error is not shown";
            }
            return new TestResult(testCaseName, fieldType, fieldName, givenInput, expectedError, actualError, result);
        } 
        catch (TimeoutException e) {
            return new TestResult(testCaseName, fieldType, fieldName,  givenInput, expectedError, "No error displayed", "Failed");
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
