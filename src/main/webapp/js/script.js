
let landingAnime = document.getElementsByClassName("landing-anime");
let loadingAnime = document.getElementsByClassName("loading-anime");
let invalidInputAnime = document.getElementsByClassName("invalid-error-anime");
let processErrorAnime = document.getElementsByClassName("process-error-anime");
let canvasDiv = document.getElementsByClassName("canvas-div");


document.getElementById("menu-icon").addEventListener("click", function () {
	document.getElementById("side-bar").classList.toggle("show");
});

/*document.getElementById("formValidator-id").addEventListener("click", function(){
	document.getElementById("formValidator-id").classList.add("selectedBG");
	document.getElementById("brokenLink-id").classList.remove("selectedBG");
	document.getElementById("API-id").classList.remove("selectedBG");
});

document.getElementById("brokenLink-id").addEventListener("click", function(){
	document.getElementById("formValidator-id").classList.remove("selectedBG");
	document.getElementById("brokenLink-id").classList.add("selectedBG");
	document.getElementById("API-id").classList.remove("selectedBG");
});

document.getElementById("API-id").addEventListener("click", function(){
	document.getElementById("formValidator-id").classList.remove("selectedBG");
	document.getElementById("brokenLink-id").classList.remove("selectedBG");
	document.getElementById("API-id").classList.add("selectedBG");
});*/
  
  
  
  
function showModal() {
	document.getElementById('overlay').style.display = 'block';
}
  
function hideModal() {
	document.getElementById('overlay').style.display = 'none';
}
  
let fetchFieldsBtn = document.getElementById("fetch-fields");
let saveTestsButton = document.getElementById("save-testInputs-btn");
let startTestingButton = document.getElementById("startTesting-button");

fetchFieldsBtn.addEventListener("click", function() {
	
	if(document.getElementById("inputURL").value!=""){
		console.log("entered");
	
		document.getElementById("totalTestCount").innerText = "00";
		document.getElementById("passedTestCount").innerText = "00";
		document.getElementById("failedTestCount").innerText = "0";
		
		document.getElementById("test-result").innerText="";
		document.getElementById("report-passedTestCases").innerText="";
		document.getElementById("report-failedTestCases").innerText="";
		
		for (let i = 0; i < landingAnime.length; i++) {
	        landingAnime[i].classList.remove("active");
			landingAnime[i].classList.add("hide");
	    }
		
		for (let i = 0; i < loadingAnime.length; i++) {
			loadingAnime[i].classList.remove("hide");
	        loadingAnime[i].classList.add("active");
	    }
		
		clearCanvas('testResultsChart');
		clearCanvas('categoryChart');
		
		for (let i = 0; i < canvasDiv.length; i++) {
			canvasDiv[i].classList.add("hideCanvas");
		}
			
	    fetchTestCases();
	}
});

saveTestsButton.addEventListener("click", function(){
    overlay.style.display = 'none';
    startTestingButton.style.display = 'inline-block';
    startTestingButton.classList.add('shine-animation');
});

document.getElementById("startTesting-button").addEventListener('click', function() {
	// Remove the animation class to stop the shining effect
	startTestingButton.classList.remove('shine-animation');
});



/*let testCases = [];

function fetchTestCases() {
	
    let url = document.getElementById("inputURL").value;

    fetch("extractFields", {
        method: "POST",
		headers: { "Content-Type": "application/x-www-form-urlencoded" },
		body: "url=" + encodeURIComponent(url)
        body: new URLSearchParams({ url })
    })
    .then(response => response.json())
    .then(data => {
        testCases = data;
        renderTestCases();
    });
}*/

/*function renderTestCases() {
    let tableBody = document.querySelector("#testTable tbody");
    tableBody.innerHTML = ""; // Clear previous content

    testCases.forEach((test, index) => {
		
		if (!test || !Array.isArray(test.predefinedInputs)) {
	        console.error(`Error: predefinedInputs missing for test case at index ${index}`, test);
	        test.predefinedInputs = []; // Initialize empty array to prevent crashes
	    }
			
			
        let row = document.createElement("tr");

        let testCaseCell = document.createElement("td");
        testCaseCell.textContent = test.testCaseName;
        row.appendChild(testCaseCell);

        let inputCell = document.createElement("td");
        let predefinedInputs = test.predefinedInputs.map(input => `<span>${input}</span>`).join(", ");
        inputCell.innerHTML = `${predefinedInputs} <br> <input type="text" id="customInput${index}" placeholder="Add Custom Input">`;
        row.appendChild(inputCell);

        let actionCell = document.createElement("td");
        let addButton = document.createElement("button");
        addButton.textContent = "Add";
        addButton.onclick = function() {
            let customInput = document.getElementById(`customInput${index}`).value;
            if (customInput) {
                test.predefinedInputs.push(customInput); // Add new input to test case
                renderTestCases(); // Re-render the table to update inputs
            }
        };
        actionCell.appendChild(addButton);
        row.appendChild(actionCell);

        tableBody.appendChild(row);
    });

    showModal();
}*/



/*let userInputs = {};

function saveAndClose() {
    userInputs = {}; // Reset stored inputs

    testCases.forEach((test, index) => {
        let customInput = document.getElementById(`customInput${index}`).value;
        
        // Use test.testCaseName instead of test.fieldName
        let testCaseKey = test.testCaseName || `TestCase${index}`;

        // Ensure the key exists in the object before pushing inputs
        if (!userInputs[testCaseKey]) {
            userInputs[testCaseKey] = [...test.predefinedInputs];
        }

        if (customInput) {
            userInputs[testCaseKey].unshift(customInput); // Add custom input to the start
        }
    });

    console.log("Saved User Inputs:", userInputs); // Debugging		//JSON.stringify(userInputs, null, 2)
    hideModal();
}*/

/*function saveAndClose() {
    userInputs = {}; // Reset stored inputs

    testCases.forEach((test, index) => {
        let inputField = document.getElementById(`customInput${index}`);
        if (inputField) {  // Check if input field exists before reading value
            let customInput = inputField.value;
            userInputs[test.fieldName] = customInput ? [customInput, ...test.predefinedInputs] : test.predefinedInputs;
        } else {
            console.warn(`Input field not found: customInput${index}`);
        }
    });

    console.log("Saved User Inputs:", userInputs);
    
	hideModal();
}*/

/*function saveAndClose() {
    userInputs = {}; // Reset stored inputs

    testCases.forEach((test, index) => {
        let customInput = document.getElementById(`customInput${index}`).value;
        userInputs[test.fieldName] = customInput ? [customInput, ...test.predefinedInputs] : test.predefinedInputs;
    });

    console.log("Saved User Inputs:", userInputs);
    
	hideModal();
}*/












// Map to store test cases and their input values
let testCaseMap = new Map();
let testCases = []; // Holds test cases fetched from the servlet
let userInputs = {}; // Stores final inputs

function showModal() {
    document.getElementById('overlay').style.display = 'block';
}

function hideModal() {
    document.getElementById('overlay').style.display = 'none';
}

// Fetch test cases from the servlet
function fetchTestCases() {
    let url = document.getElementById("inputURL").value;

    fetch("extractFields", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "url=" + encodeURIComponent(url)
    })
    .then(response => response.json())
    .then(data => {
        testCases = data;
        renderTestCases();
    })
    .catch(error => console.error("Error fetching test cases:", error));
}

// Function to create an input row dynamically
function createInputRow(value = '', isInitial = false) {
    const row = document.createElement('div');
    row.className = `input-row ${isInitial ? 'initial' : ''}`;

    // Handle placeholders
    let placeholderText = isInitial
        ? (value.trim() === '' ? "empty value" : '')  // Predefined empty → "empty value"
        : "give your value to test";                 // User input → "give your value to test"

    row.innerHTML = `
        <input type="text" value="${value}" placeholder="${placeholderText}" ${isInitial ? 'readonly' : ''}>
        ${isInitial ? '' : '<button class="remove-input" onclick="removeInputValue(this)">×</button>'}
    `;
    return row;
}

// Add input value dynamically
function addInputValue(testCaseName) {
    const safeId = testCaseName.replace(/[^a-zA-Z0-9]/g, '_');
    const inputContainer = document.getElementById(`inputs_${safeId}`);

    if (inputContainer) {
        let existingValues = new Set(
            Array.from(inputContainer.querySelectorAll("input"))
                .map(input => input.value.trim())
        );

        // Ensure at least one user input can be added, even if there is an "empty value"
        if (!existingValues.has("give your value to test")) {
            inputContainer.appendChild(createInputRow());
        }
    }
}

// Remove input value
function removeInputValue(button) {
    const row = button.parentElement;
    if (!row.classList.contains('initial')) {
        row.remove();
    }
}

// Save user inputs and send them to the servlet
function saveAndClose() {
    userInputs = {}; // Reset stored inputs

    testCases.forEach((test) => {
        let safeId = test.testCaseName.replace(/[^a-zA-Z0-9]/g, '_');
        let inputContainer = document.getElementById(`inputs_${safeId}`);
        let inputValues = Array.from(inputContainer.querySelectorAll("input"))
            .map(input => input.value.trim())
            .filter((value, index, self) => self.indexOf(value) === index); // Prevent duplicates

        // Ensure at least "empty value" is stored if no inputs are given
        userInputs[test.testCaseName] = inputValues.length > 0 ? inputValues : ["empty value"];
    });

    console.log("Saved User Inputs:", userInputs);
    hideModal();
}

// Render test cases dynamically from servlet response
function renderTestCases() {
    let tableBody = document.querySelector("#testTable tbody");
    tableBody.innerHTML = ""; // Clear previous content

    testCases.forEach(({ testCaseName, predefinedInputs }) => {
        const safeId = testCaseName.replace(/[^a-zA-Z0-9]/g, '_');
        testCaseMap.set(testCaseName, predefinedInputs);

        const row = document.createElement("tr");

        let testCaseCell = document.createElement("td");
        testCaseCell.textContent = testCaseName;
        row.appendChild(testCaseCell);

        let inputCell = document.createElement("td");
        let inputContainer = document.createElement("div");
        inputContainer.className = "input-values";
        inputContainer.id = `inputs_${safeId}`;

        predefinedInputs.forEach(input => {
            inputContainer.appendChild(createInputRow(input, true));
        });

        inputCell.appendChild(inputContainer);
        row.appendChild(inputCell);

        let actionCell = document.createElement("td");
        let addButton = document.createElement("button");
        addButton.className = "add-input";
        addButton.textContent = "+ Add Input";
        addButton.onclick = function () {
            addInputValue(testCaseName);
        };
        actionCell.appendChild(addButton);
        row.appendChild(actionCell);

        tableBody.appendChild(row);
    });

    showModal();
}
























function showChartRepresentation() {
	fetch("getChartRepresentation")
	    .then(response => response.json())
	    .then(data => {
	        // Extract Passed count
	        const passedCount = data["Passed"];

	        // Extract Failed reasons
	        const failedReasons = data["Failed Reasons"];
	        const failedLabels = Object.keys(failedReasons);
	        const failedValues = failedLabels.map(key => failedReasons[key]);
			
			
			const failedCount = Object.values(failedReasons).reduce((sum, count) => sum + count, 0);
			const totalTestCases = passedCount + failedCount;
			
			animateCounter("totalTestCount", totalTestCases, "blue");
			animateCounter("passedTestCount", passedCount, "green");
	        animateCounter("failedTestCount", failedCount, "red");
			

	        // Pie Chart Data (Passed + Failed Reasons)
	        const pieLabels = ["Passed", ...failedLabels];
	        const pieValues = [passedCount, ...failedValues];
	
			// Clear previous Pie Chart
			clearCanvas('testResultsChart');
					
	        // Display Pie Chart (Passed vs Failure Reasons)
	        const canvas1 = document.getElementById('testResultsChart').getContext('2d');
	        const pieChart = new Chart(canvas1, {
	            type: 'pie',
	            data: {
	                labels: pieLabels,
	                datasets: [{
	                    data: pieValues,
	                    backgroundColor: ["#00ab41", "#FF474C", "orange", "purple", "blue"]
	                }]
	            }
	        });
			
			// Store Chart instance for later clearing
			document.getElementById('testResultsChart').chartInstance = pieChart;
					
					

	        // Extract Field Types Count
	        const fieldTypeLabels = Object.keys(data["Field Types"]);
	        const fieldTypeValues = fieldTypeLabels.map(key => data["Field Types"][key]);
	
			// Clear previous Bar Chart
			clearCanvas('categoryChart');
					
	        // Display Bar Chart (Field Types)
	        const canvas2 = document.getElementById('categoryChart').getContext('2d');
	        const barChart = new Chart(canvas2, {
	            type: 'bar',
	            data: {
	                labels: fieldTypeLabels,
	                datasets: [{
	                    label: 'Field Type Count',
	                    data: fieldTypeValues,
	                    backgroundColor: "#45b6fe"
	                }]
	            },
				options: {
			        responsive: true,
			        maintainAspectRatio: false,  // Prevent Chart.js from forcing aspect ratio
			    }
	        });
			
			// Store Chart instance for later clearing
			document.getElementById('categoryChart').chartInstance = barChart;
			
			for (let i = 0; i < canvasDiv.length; i++) {
		        canvasDiv[i].classList.remove("hideCanvas");
				canvasDiv[i].classList.add("showCanvas");
		    }
	    })
	    .catch(error => console.error('Error fetching data:', error));
}


function clearCanvas(canvasId) {
    const canvas = document.getElementById(canvasId);
    const ctx = canvas.getContext('2d');
    
    // Reset canvas by destroying the existing Chart instance if present
    if (canvas.chartInstance) {
        canvas.chartInstance.destroy();
    }
    
    // Clear the canvas manually
    ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function animateCounter(id, targetValue, color) {
    let count = 0;
    const element = document.getElementById(id);
    element.style.color = color; // Change text color dynamically

    const interval = setInterval(() => {
        if (count < targetValue) {
            count++;
            element.innerText = count;
        } else {
            clearInterval(interval);
        }
    }, 10); 
}














document.getElementById("startTesting-button").addEventListener("click", function(event){
	
	event.preventDefault();
	
	let url = document.getElementById("inputURL").value;
	let resultsDiv = document.getElementById("test-result");
	
	fetch("runTestCases", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(userInputs) // Send inputs as JSON
				
		/*headers: { "Content-Type": "application/x-www-form-urlencoded" },
		body: "userInputs=" + encodeURIComponent(JSON.stringify(userInputs))*/
	    
		/*headers: { "Content-Type": "application/x-www-form-urlencoded" },
	   	body: "url=" + encodeURIComponent(url)*/
	})
	.then(response => response.json())	
	
	.then(data => {
		
		console.log("Server Response:", data); // Debugging: Check what the server returns
		for (let i = 0; i < loadingAnime.length; i++) {
			loadingAnime[i].classList.remove("active");
	        loadingAnime[i].classList.add("hide");
	    }
		
		resultsDiv.innerHTML="";
		
		if (!Array.isArray(data)) { // Check if response is not an array
            resultsDiv.innerHTML = `<p style="color:red;">Error: ${data.error || "Unexpected response format"}</p>`;
            return;
        }
		
		const passedTestsDiv = document.getElementById('report-passedTestCases');
		const failedTestsDiv = document.getElementById('report-failedTestCases');
		
	
		data.forEach(rslt => {
			
			const testCaseDiv = document.createElement('div');
			testCaseDiv.className = 'test-case ' + (rslt.result === 'Pass' ? 'pass' : 'fail');
			testCaseDiv.textContent = rslt.testCaseName;
			testCaseDiv.dataset.target = rslt.testCaseNumber;
			
			if (rslt.result === 'Pass') {
                passedTestsDiv.appendChild(testCaseDiv);
            } else {
                failedTestsDiv.appendChild(testCaseDiv);
            }
			
			
			
			let color = rslt.result === "Pass" ? "green" : "red";
			let cssClass = rslt.result === "Pass" ? "pass" : "fail";
			
			const resultDiv = document.createElement('div');
			resultDiv.className = `test-result ${cssClass}`;
			resultDiv.style = `border-left: 5px solid ${color}; padding: 10px`;
			resultDiv.id = rslt.testCaseNumber;
			
			console.log("===========>",rslt.result === "Fail");
			console.log("----------->",rslt.screenshotKey);
			// "View Snap" Link only for failed test cases
            let snapLink = (rslt.result === "Fail" && rslt.screenshotKey)
                ? `<a href="#" class="view-snap" onclick="openScreenshotPopup('${rslt.screenshotKey}', '${rslt.testCaseName}')">View Snap</a>`
                : "";
			
			
			resultDiv.innerHTML += `
		            <strong>Test Case:</strong> ${rslt.testCaseName}<br>
		            <strong>Field Type:</strong> ${rslt.fieldType}<br>
		            <strong>Given Input:</strong> ${rslt.givenInput}<br>
		            <strong>Expected Output:</strong> ${rslt.expectedOutput}<br>
		            <strong>Actual Output:</strong> ${rslt.actualOutput}<br>
		            <strong>Result:</strong> <span style="color: ${color}; font-weight: bold;">${rslt.result}</span>
					${snapLink}
		        </div>
			`;
			
			resultsDiv.appendChild(resultDiv);
	    });
		showChartRepresentation();	
							
	})
	.catch(error => {
        console.error("Error:", error);
        resultsDiv.innerHTML = "<p>Something went wrong!</p>";
    });
		
	/*.then(data => {
        let resultDiv = document.getElementById("test-result");
        let color = data.result === "Passed" ? "green" : "red";

        resultDiv.innerHTML = `
            <div class="test-case" style="border-left: 5px solid ${color}; padding: 10px; margin-bottom: 10px;">
                <strong>Test Case:</strong> ${data.testCaseName}<br>
                <strong>Field Type:</strong> ${data.fieldType} (${data.fieldName})<br>
                <strong>Given Input:</strong> ${data.givenInput}<br>
                <strong>Expected Output:</strong> ${data.expectedOutput}<br>
                <strong>Actual Output:</strong> ${data.actualOutput}<br>
                <strong>Result:</strong> <span style="color: ${color}; font-weight: bold;">${data.result}</span>
            </div>
        `;
    })
    .catch(error => {
        console.error("Error:", error);
        document.getElementById("test-result").innerHTML = "<p>Something went wrong!</p>";
    });*/
	
	
	
		
    /*resultDiv.innerHTML = `
        <div class="test-case ${cssClass}" style="border-left: 5px solid ${color}; padding: 10px; margin-bottom: 10px;">
            <strong>Test Case:</strong> ${data.testCaseName}<br>
            <strong>Field Type:</strong> ${data.fieldType} (${data.fieldName})<br>
            <strong>Given Input:</strong> ${data.givenInput}<br>
            <strong>Expected Output:</strong> ${data.expectedOutput}<br>
            <strong>Actual Output:</strong> ${data.actualOutput}<br>
            <strong>Result:</strong> <span style="color: ${color}; font-weight: bold;">${data.result}</span>
        </div>
        `;
    })*/
});

document.addEventListener('click', function (event) {
    if (event.target.classList.contains('test-case')) {
		console.log("Trigerred");
        const targetId = event.target.dataset.target;
        const targetElement = document.getElementById(targetId);
        if (targetElement) {
            targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
			targetElement.classList.add('blinking');
			setTimeout(() => {
                targetElement.classList.remove('blinking');
            }, 3000);
        }
    }
});


/*document.querySelectorAll('.test-case').forEach(item => {
    item.addEventListener('click', function () {
        const targetId = this.dataset.target;
        const targetElement = document.getElementById(targetId);
        if (targetElement) {
            targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    });
});*/




// Function to open the front-layer div with the screenshot
function openScreenshotPopup(screenshotKey, testCaseName) {
    let popupDiv = document.getElementById("screenshotPopup");
    let popupContent = document.getElementById("screenshotContent");

    // Update popup content
    popupContent.innerHTML = `
        <h3>${testCaseName}</h3>
        <img src="viewScreenshot?screenshotId=${screenshotKey}" alt="Screenshot" style="max-width: 100%; height: auto; border: 1px solid #ccc;">
        <br>
        <button onclick="downloadScreenshot('${screenshotKey}')">Download</button>
        <button onclick="closeScreenshotPopup()">Close</button>
    `;

    popupDiv.style.display = "block"; // Show the popup
}

// Function to close the popup
function closeScreenshotPopup() {
    document.getElementById("screenshotPopup").style.display = "none";
}

// Function to trigger the screenshot download
function downloadScreenshot(screenshotKey) {
    window.location.href = `downloadScreenshot?screenshotId=${screenshotKey}`;
}




/*// Open Screenshot Popup
function openScreenshotPopup(screenshotKey, testCaseName) {
    let popupDiv = document.createElement("div");
    popupDiv.id = "screenshotPopup";
    popupDiv.className = "popup";

    popupDiv.innerHTML = `
        <div class="popup-content">
            <span class="close-btn" onclick="closeScreenshotPopup()">&times;</span>
            <h2>${testCaseName} - Screenshot</h2>
            <img src="viewScreenshot?screenshotKey=${screenshotKey}" alt="Test Screenshot">
            <button onclick="downloadScreenshot('${screenshotKey}')">Download</button>
        </div>
    `;

    document.body.appendChild(popupDiv);
}

// Close Screenshot Popup
function closeScreenshotPopup() {
    let popupDiv = document.getElementById("screenshotPopup");
    if (popupDiv) {
        document.body.removeChild(popupDiv);
    }
}

// Download Screenshot
function downloadScreenshot(screenshotKey) {
    if (!screenshotKey) {
        alert("Screenshot key is missing!");
        return;
    }

    let downloadLink = document.createElement("a");
    downloadLink.href = `downloadScreenshot?screenshotKey=${encodeURIComponent(screenshotKey)}`;
    downloadLink.download = `Test_Screenshot_${screenshotKey}.png`;
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
}*/