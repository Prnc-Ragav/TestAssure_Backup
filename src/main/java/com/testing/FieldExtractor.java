package com.testing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
	
	private List<String> executedMethods = new ArrayList<>();
	private List<String> mainMethod = new ArrayList<>();
	private List<String> testResultClassMethod = new ArrayList<>();
	
	public List<String> getExecutedMethods() {
		return executedMethods;
	}

	public List<String> getMainMethod() {
		return mainMethod;
	}

	public List<String> getTestResultClassMethod() {
		return testResultClassMethod;
	}


	public List<WebElement> fetchedFields;
	public Map<String, WebElement> testCaseFieldMap = new LinkedHashMap<>();
	
	public List<TestResult> runTests(Map<String, WebElement> testCaseAndField, Map<String, List<String>> inputs, HttpSession session){
		TestCase testCases = new TestCase(driver);
        List<TestResult> results = testCases.runTests(testCaseAndField, inputs, session);
        
        putExecutedMethods();

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
	
	
	
	
	
    
    
    
    
    
    
    public void putExecutedMethods() {
    	String testTextField = "public void testTextField(String testCaseName, WebElement field, String input, JavascriptExecutor js, String script) {\n" +
    			"    field.clear();\n" +
    			"    field.sendKeys(input);\n" +
    			"    field.sendKeys(Keys.ENTER);\n" +
    			"\n" +
    			"    String alertMessage = checkForAlertMessage();\n" +
    			"    boolean passed = alertMessage != null;\n" +
    			"\n" +
    			"    List<String> errors = null;\n" +
    			"    if (!passed) {\n" +
    			"        try {\n" +
    			"            errors = (List<String>) js.executeScript(script, field);\n" +
    			"            passed = errors != null && !errors.isEmpty();\n" +
    			"        } catch (UnhandledAlertException e) {\n" +
    			"            Alert alert = driver.switchTo().alert();\n" +
    			"            alertMessage = alert.getText();\n" +
    			"            alert.accept();\n" +
    			"            passed = true;\n" +
    			"        }\n" +
    			"    }\n" +
    			"\n" +
    			"    String errorMessage = (alertMessage != null) ? alertMessage : (errors != null && !errors.isEmpty() ? errors.get(0) : \"No error\");\n" +
    			"\n" +
    			"    TestResult result1 = new TestResult(testCaseName, \"text\", input, \"Error Message Expected\", passed ? errorMessage : \"No error\", passed ? \"Pass\" : \"Fail\");\n" +
    			"    System.out.println(result1.toString());\n" +
    			"}\n";

    			String testEmailField = "public void testEmailField(String testCaseName, WebElement field, String input, JavascriptExecutor js, String script) {\n" +
    			"    field.clear();\n" +
    			"    field.sendKeys(input);\n" +
    			"    field.sendKeys(Keys.ENTER);\n" +
    			"\n" +
    			"    String alertMessage = checkForAlertMessage();\n" +
    			"    boolean passed = alertMessage != null;\n" +
    			"\n" +
    			"    List<String> errors = new ArrayList<>();\n" +
    			"\n" +
    			"    if (!passed) {\n" +
    			"        try {\n" +
    			"            errors = (List<String>) js.executeScript(script, field);\n" +
    			"            passed = errors != null && !errors.isEmpty();\n" +
    			"        } catch (UnhandledAlertException e) {\n" +
    			"            Alert alert = driver.switchTo().alert();\n" +
    			"            alertMessage = alert.getText();\n" +
    			"            alert.accept();\n" +
    			"            passed = true;\n" +
    			"        }\n" +
    			"    }\n" +
    			"\n" +
    			"    String errorMessage = (alertMessage != null) ? alertMessage : (!errors.isEmpty() ? errors.get(0) : \"No error detected\");\n" +
    			"    TestResult result1 = new TestResult(testCaseName, \"email\", input, \"Error Message Expected\", errorMessage, passed ? \"Pass\" : \"Fail\");\n" +
    			"    handleFailedTestCase(result1);\n" +
    			"    System.out.println(result1.toString());\n" +
    			"}\n";

    			String testSelectField = "public void testSelectField(String testCaseName, WebElement field, String input) {\n" +
    			"    Select dropdown = new Select(field);\n" +
    			"    TestResult result1 = null;\n" +
    			"    try {\n" +
    			"        dropdown.selectByVisibleText(input);\n" +
    			"        result1 = new TestResult(testCaseName, \"select\", input, input, input, \"Pass\");\n" +
    			"    } catch (Exception e) {\n" +
    			"        result1 = new TestResult(testCaseName, \"select\", input, input, \"Not selectable\", \"Fail\");\n" +
    			"    }\n" +
    			"    System.out.println(result1.toString());\n" +
    			"}\n";

    			String testCheckbox = "public void testCheckbox(String testCaseName, WebElement field, boolean shouldBeChecked) {\n" +
    			"    boolean isChecked = field.isSelected();\n" +
    			"    if (shouldBeChecked && !isChecked) {\n" +
    			"        field.click();\n" +
    			"        isChecked = field.isSelected();\n" +
    			"    }\n" +
    			"\n" +
    			"    TestResult result1 = new TestResult(testCaseName, \"checkbox\", shouldBeChecked ? \"Checked\" : \"Unchecked\",\n" +
    			"            shouldBeChecked ? \"Checked\" : \"Unchecked\", isChecked ? \"Checked\" : \"Unchecked\",\n" +
    			"            isChecked == shouldBeChecked ? \"Pass\" : \"Fail\");\n" +
    			"\n" +
    			"    System.out.println(result1.toString());\n" +
    			"}\n";

    			String testRadioButton = "public void testRadioButton(String testCaseName, WebElement field) {\n" +
    			"    boolean isSelected = field.isSelected();\n" +
    			"    if (!isSelected) {\n" +
    			"        field.click();\n" +
    			"        isSelected = field.isSelected();\n" +
    			"    }\n" +
    			"\n" +
    			"    TestResult result1 = new TestResult(testCaseName, \"radio\", \"Selected\", \"Selected\", \n" +
    			"            isSelected ? \"Selected\" : \"Not Selected\", isSelected ? \"Pass\" : \"Fail\");\n" +
    			"\n" +
    			"    System.out.println(result1.toString());\n" +
    			"}\n";
    			
    			String fetchVisibleFormFields = "public void fetchVisibleFormFields(String url) {"
    			        + " WebDriverManager.chromedriver().setup();"
    			        + " driver = new ChromeDriver();"
    			        + " driver.get(url);"
    			        + " new WebDriverWait(driver, Duration.ofSeconds(10)).until("
    			        + " webDriver -> ((JavascriptExecutor) webDriver)"
    			        + " .executeScript(\"return document.readyState\").equals(\"complete\")"
    			        + " );"
    			        + " System.out.println(\"1.>>>>>>>Entered\");"
    			        + " boolean formFoundInIframe = checkIframesForForms(driver);"
    			        + " System.out.println(\">>>>>>>Entered\");"
    			        + " if (!formFoundInIframe) {"
    			        + " System.out.println(\"No form found in iframes. Checking the main page...\");"
    			        + " extractAllVisibleFields(driver);"
    			        + " }"
    			        + " return testCases;"
    			        + "}";

    			String checkIframesForForms = "private boolean checkIframesForForms(WebDriver driver) {"
    			        + " List<WebElement> iframes = driver.findElements(By.tagName(\"iframe\"));"
    			        + " System.out.println(\"2>>>>>>>Entered\");"
    			        + " for (WebElement iframe : iframes) {"
    			        + " driver.switchTo().frame(iframe);"
    			        + " if (!driver.findElements(By.tagName(\"form\")).isEmpty()) {"
    			        + " System.out.println(\"Form found inside an iframe. Extracting fields...\");"
    			        + " extractAllVisibleFields(driver);"
    			        + " return true;"
    			        + " }"
    			        + " if (checkIframesForForms(driver)) {"
    			        + " return true;"
    			        + " }"
    			        + " driver.switchTo().defaultContent();"
    			        + " }"
    			        + " return false;"
    			        + "}";

    			String extractAllVisibleFields = "private void extractAllVisibleFields(WebDriver driver) {"
    			        + " List<WebElement> inputFields = driver.findElements(By.tagName(\"input\"));"
    			        + " extractVisibleFields(inputFields, driver);"
    			        + " List<WebElement> textAreas = driver.findElements(By.tagName(\"textarea\"));"
    			        + " extractVisibleFields(textAreas, driver);"
    			        + " List<WebElement> selectFields = driver.findElements(By.tagName(\"select\"));"
    			        + " extractVisibleSelectFields(selectFields, driver);"
    			        + "}";

    			String extractVisibleFields = "private void extractVisibleFields(List<WebElement> elements, WebDriver driver) {"
    			        + " for (WebElement field : elements) {"
    			        + " if (field.isDisplayed()) {"
    			        + " String identifier = getFieldIdentifier(field, driver);"
    			        + " String type = field.getTagName().equals(\"textarea\") ? \"textarea\" : field.getAttribute(\"type\");"
    			        + " List<String> predefinedInputs = getPredefinedInputs(type);"
    			        + " String testCaseName = \"Validate \" + identifier + \" field\";"
    			        + " testCaseFieldMap.put(testCaseName, field);"
    			        + " }"
    			        + " }"
    			        + "}";

    			String extractVisibleSelectFields = "private void extractVisibleSelectFields(List<WebElement> selectFields, WebDriver driver) {"
    			        + " for (WebElement field : selectFields) {"
    			        + " if (field.isDisplayed()) {"
    			        + " String identifier = getFieldIdentifier(field, driver);"
    			        + " String testCaseName = \"Validate dropdown \" + identifier;"
    			        + " List<String> predefinedInputs = new ArrayList<>();"
    			        + " List<WebElement> options = field.findElements(By.tagName(\"option\"));"
    			        + " if (!options.isEmpty()) {"
    			        + " Collections.shuffle(options);"
    			        + " int limit = Math.min(options.size(), 5);"
    			        + " for (int i = 0; i < limit; i++) {"
    			        + " predefinedInputs.add(options.get(i).getText());"
    			        + " }"
    			        + " }"
    			        + " testCaseFieldMap.put(testCaseName, field);"
    			        + " }"
    			        + " }"
    			        + "}";

    			String getFieldIdentifier = "private String getFieldIdentifier(WebElement field, WebDriver driver) {"
    			        + " String placeholder = field.getAttribute(\"placeholder\");"
    			        + " if (placeholder != null && !placeholder.isEmpty()) return placeholder;"
    			        + " String id = field.getAttribute(\"id\");"
    			        + " if (id != null && !id.isEmpty()) {"
    			        + " List<WebElement> labels = driver.findElements(By.xpath(\"//label[@for='\" + id + \"']\"));"
    			        + " if (!labels.isEmpty()) return labels.get(0).getText();"
    			        + " }"
    			        + " String name = field.getAttribute(\"name\");"
    			        + " if (name != null && !name.isEmpty()) return name;"
    			        + " return field.getTagName().equals(\"textarea\") ? \"text area\" : field.getAttribute(\"type\");"
    			        + "}";
    			
    			
    			String predefinedInputsMethod = "public static List<String> getPredefinedInputs(String fieldType) {\n" +
    					"    List<String> inputs = new ArrayList<>();\n" +
    					"    switch (fieldType) {\n" +
    					"        case \"text\":\n" +
    					"        case \"textarea\":\n" +
    					"            inputs.add(\"\");\n" +
    					"            inputs.add(\"123456\");\n" +
    					"            inputs.add(\"Valid input text\");\n" +
    					"            break;\n" +
    					"        case \"email\":\n" +
    					"            inputs.add(\"test@example.com\");\n" +
    					"            inputs.add(\"invalid-email\");\n" +
    					"            break;\n" +
    					"        case \"number\":\n" +
    					"            inputs.add(\"123\");\n" +
    					"            inputs.add(\"-1\");\n" +
    					"            inputs.add(\"abcd\");\n" +
    					"            break;\n" +
    					"        case \"password\":\n" +
    					"            inputs.add(\"P@ssw0rd\");\n" +
    					"            inputs.add(\"short\");\n" +
    					"            break;\n" +
    					"        case \"url\":\n" +
    					"            inputs.add(\"https://example.com\");\n" +
    					"            inputs.add(\"invalid_url\");\n" +
    					"            break;\n" +
    					"        case \"tel\":\n" +
    					"            inputs.add(\"1234567890\");\n" +
    					"            inputs.add(\"abc123\");\n" +
    					"            break;\n" +
    					"        case \"checkbox\":\n" +
    					"        case \"radio\":\n" +
    					"            inputs.add(\"checked\");\n" +
    					"            inputs.add(\"unchecked\");\n" +
    					"            break;\n" +
    					"        case \"range\":\n" +
    					"            inputs.add(\"0\");\n" +
    					"            inputs.add(\"50\");\n" +
    					"            inputs.add(\"100\");\n" +
    					"            break;\n" +
    					"        default:\n" +
    					"            inputs.add(\"DefaultTestInput\");\n" +
    					"    }\n" +
    					"    return inputs;\n" +
    					"}";

//    			String allMethods = testTextField + "\n" + testEmailField + "\n" + testSelectField + "\n" + testCheckbox + "\n" + testRadioButton;

    			executedMethods.add(testTextField);
    			executedMethods.add(testEmailField);
    			executedMethods.add(testSelectField);
    			executedMethods.add(testCheckbox);
    			executedMethods.add(testRadioButton);
    			executedMethods.add(fetchVisibleFormFields);
    			executedMethods.add(checkIframesForForms);
    			executedMethods.add(extractAllVisibleFields);
    			executedMethods.add(extractVisibleFields);
    			executedMethods.add(extractVisibleSelectFields);
    			executedMethods.add(getFieldIdentifier);
    			executedMethods.add(predefinedInputsMethod);
    			
    			
    			
    			
    			
    			String mainCode = 
    				    "JavascriptExecutor js = (JavascriptExecutor) driver;\n" +
    				    "String script =\n" +
    				    "    \"var detectedErrors = [];\" +\n" +
    				    "    \"(function() {\" +\n" +
    				    "    \"    var originalAlert = window.alert;\" +\n" +
    				    "    \"    var originalConfirm = window.confirm;\" +\n" +
    				    "    \"    var originalPrompt = window.prompt;\" +\n" +
    				    "    \"    window.alert = function(message) {\" +\n" +
    				    "    \"        detectedErrors.push(message);\" +\n" +
    				    "    \"        originalAlert(message);\" +\n" +
    				    "    \"    };\" +\n" +
    				    "    \"    window.confirm = function(message) {\" +\n" +
    				    "    \"        detectedErrors.push(message);\" +\n" +
    				    "    \"        return originalConfirm(message);\" +\n" +
    				    "    \"    };\" +\n" +
    				    "    \"    window.prompt = function(message, defaultText) {\" +\n" +
    				    "    \"        detectedErrors.push(message);\" +\n" +
    				    "    \"        return originalPrompt(message, defaultText);\" +\n" +
    				    "    \"    };\" +\n" +
    				    "    \"})();\" +\n" +
    				    "    \"function isVisible(el) {\" +\n" +
    				    "    \"    var style = window.getComputedStyle(el);\" +\n" +
    				    "    \"    return (style.display !== 'none' && style.visibility !== 'hidden' && el.innerText.trim().length > 2);\" +\n" +
    				    "    \"}\" +\n" +
    				    "    \"function isErrorText(el) {\" +\n" +
    				    "    \"    var style = window.getComputedStyle(el);\" +\n" +
    				    "    \"    var textColor = style.color;\" +\n" +
    				    "    \"    var match = textColor.match(/rgb\\\\((\\\\d+),\\\\s*(\\\\d+),\\\\s*(\\\\d+)\\\\)/);\" +\n" +
    				    "    \"    if (!match) return false;\" +\n" +
    				    "    \"    var r = parseInt(match[1]), g = parseInt(match[2]), b = parseInt(match[3]);\" +\n" +
    				    "    \"    return (r > 120 && r > g + 40 && r > b + 40);\" +\n" +
    				    "    \"}\" +\n" +
    				    "    \"function hasErrorAttributes(el) {\" +\n" +
    				    "    \"    return el.hasAttribute('aria-invalid') || el.getAttribute('role') === 'alert' || el.className.includes('error');\" +\n" +
    				    "    \"}\" +\n" +
    				    "    \"function checkElement(el) {\" +\n" +
    				    "    \"    if (isVisible(el) && (isErrorText(el) || hasErrorAttributes(el))) {\" +\n" +
    				    "    \"        detectedErrors.push(el.innerText.trim());\" +\n" +
    				    "    \"    }\" +\n" +
    				    "    \"}\" +\n" +
    				    "    \"function checkForErrors(element) {\" +\n" +
    				    "    \"    if (!element) return;\" +\n" +
    				    "    \"    let allChildren = element.querySelectorAll('span, label, div, p');\" +\n" +
    				    "    \"    allChildren.forEach(checkElement);\" +\n" +
    				    "    \"}\" +\n" +
    				    "    \"function findErrorMessage(inputField) {\" +\n" +
    				    "    \"    let currentElement = inputField.parentElement;\" +\n" +
    				    "    \"    while (currentElement && currentElement.tagName.toLowerCase() !== 'body') {\" +\n" +
    				    "    \"        checkForErrors(currentElement);\" +\n" +
    				    "    \"        if (detectedErrors.length > 0) {\" +\n" +
    				    "    \"            return detectedErrors;\" +\n" +
    				    "    \"        }\" +\n" +
    				    "    \"        if (currentElement.querySelector('input, textarea, select')) {\" +\n" +
    				    "    \"            break;\" +\n" +
    				    "    \"        }\" +\n" +
    				    "    \"        currentElement = currentElement.parentElement;\" +\n" +
    				    "    \"    }\" +\n" +
    				    "    \"    return detectedErrors;\" +\n" +
    				    "    \"}\" +\n" +
    				    "    \"return detectedErrors.concat(findErrorMessage(arguments[0]));\";\n" +

    				    "for (Map.Entry<String, WebElement> entry : testCaseMap.entrySet()) {\n" +
    				    "    System.out.println(\"\\nNext Element Testing\");\n" +
    				    "    String testCaseName = entry.getKey();\n" +
    				    "    System.out.println(\"TestCaseName : \" + testCaseName);\n" +
    				    "    WebElement field = entry.getValue();\n" +
    				    "    String fieldType = field.getTagName();\n" +
    				    "    List<String> inputValues = inputs.getOrDefault(testCaseName, new ArrayList<>());\n" +
    				    "    System.out.println(\"Field : \" + field);\n" +
    				    "    for (String input : inputValues) {\n" +
    				    "        try {\n" +
    				    "            switch (fieldType) {\n" +
    				    "                case \"input\":\n" +
    				    "                    String type = field.getAttribute(\"type\");\n" +
    				    "                    if (\"text\".equals(type)) {\n" +
    				    "                        System.out.println(\"\\nTesting text field\");\n" +
    				    "                        testTextField(testCaseName, field, input, js, script);\n" +
    				    "                    } else if (\"email\".equals(type)) {\n" +
    				    "                        System.out.println(\"\\nTesting email field\");\n" +
    				    "                        validationResult = testEmailField(testCaseName, field, input, js, script);\n" +
    				    "                        testResults.add(validationResult);\n" +
    				    "                    } else if (\"number\".equals(type)) {\n" +
    				    "                        System.out.println(\"\\nTesting number field\");\n" +
    				    "                        testNumberField(testCaseName, field, input, js, script);\n" +
    				    "                    }\n" +
    				    "                    break;\n" +
    				    "                case \"textarea\":\n" +
    				    "                    System.out.println(\"\\nTesting text-area field\");\n" +
    				    "                    testTextField(testCaseName, field, input, js, script);\n" +
    				    "                    break;\n" +
    				    "                case \"select\":\n" +
    				    "                    System.out.println(\"\\nTesting select field\");\n" +
    				    "                    testSelectField(testCaseName, field, input);\n" +
    				    "                    break;\n" +
    				    "                case \"checkbox\":\n" +
    				    "                    System.out.println(\"\\nTesting checkbox field\");\n" +
    				    "                    testCheckbox(testCaseName, field, \"Checked\".equals(input));\n" +
    				    "                    break;\n" +
    				    "                case \"radio\":\n" +
    				    "                    System.out.println(\"\\nTesting radio field\");\n" +
    				    "                    testRadioButton(testCaseName, field);\n" +
    				    "                    break;\n" +
    				    "            }\n" +
    				    "        } catch (Exception e) {\n" +
    				    "        }\n" +
    				    "    }\n" +
    				    "}"; 
    			
    			mainMethod.add(mainCode);
    			
    			
    			
    			
    			String testResultConstructor = 
    				    "public class TestResult {\n" +
    				    "    private String testCaseName;\n" +
    				    "    private String fieldType;\n" +
    				    "    private String givenInput;\n" +
    				    "    private String expectedOutput;\n" +
    				    "    private String actualOutput;\n" +
    				    "    private String result;\n\n" +
    				    "    public TestResult(String testCaseName, String fieldType, \n" +
    				    "                      String givenInput, String expectedOutput, \n" +
    				    "                      String actualOutput, String result) {\n" +
    				    "        this.testCaseName = testCaseName;\n" +
    				    "        this.fieldType = fieldType;\n" +
    				    "        this.givenInput = givenInput;\n" +
    				    "        this.expectedOutput = expectedOutput;\n" +
    				    "        this.actualOutput = actualOutput;\n" +
    				    "        this.result = result;\n" +
    				    "    }\n\n" +
    				    "    @Override\n" +
    				    "    public String toString() {\n" +
    				    "        return \"TestResult [testCaseName=\" + testCaseName + \", fieldType=\" + fieldType + \", givenInput=\" + givenInput +\n" +
    				    "               \", expectedOutput=\" + expectedOutput + \", actualOutput=\" + actualOutput + \", result=\" + result + \"]\";\n" +
    				    "    }\n" +
    				    "}";
    			
    			testResultClassMethod.add(testResultConstructor);
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
