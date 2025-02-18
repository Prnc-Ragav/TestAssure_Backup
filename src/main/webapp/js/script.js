document.getElementById("menu-icon").addEventListener("click", function () {
  document.getElementById("side-bar").classList.toggle("show");
});

document.getElementById("startTesting-button").addEventListener("click", function(event){
	event.preventDefault();
	
	let url = document.getElementById("inputURL").value;
	let resultsDiv = document.getElementById("test-result");
	
	fetch("test", {
		method: "POST",
	    headers: { "Content-Type": "application/x-www-form-urlencoded" },
	   	body: "url=" + encodeURIComponent(url)
	})
	.then(response => response.json())	
	
	.then(data => {
		
		console.log("Server Response:", data); // Debugging: Check what the server returns
		
		
		resultsDiv.innerHTML="";
		
		if (!Array.isArray(data)) { // Check if response is not an array
            resultsDiv.innerHTML = `<p style="color:red;">Error: ${data.error || "Unexpected response format"}</p>`;
            return;
        }
		
		const passedTestsDiv = document.getElementById('report-passedTestCases');
		const failedTestsDiv = document.getElementById('report-failedTestCases');
		
	
		data.forEach(rslt => {
			
			const testCaseDiv = document.createElement('div');
			testCaseDiv.className = 'test-case ' + (rslt.result === 'Passed' ? 'pass' : 'fail');
			testCaseDiv.textContent = rslt.testCaseName;
			testCaseDiv.dataset.target = rslt.testCaseNumber;
			
			if (rslt.result === 'Passed') {
                passedTestsDiv.appendChild(testCaseDiv);
            } else {
                failedTestsDiv.appendChild(testCaseDiv);
            }
			
			
			
			let color = rslt.result === "Passed" ? "green" : "red";
			let cssClass = rslt.result === "Passed" ? "pass" : "fail";
			
			const resultDiv = document.createElement('div');
			resultDiv.className = `test-result ${cssClass}`;
			resultDiv.style = `border-left: 5px solid ${color}; padding: 10px`;
			resultDiv.id = rslt.testCaseNumber;
			
			resultDiv.innerHTML += `
		            <strong>Test Case:</strong> ${rslt.testCaseName}<br>
		            <strong>Field Type:</strong> ${rslt.fieldType} (${rslt.fieldName})<br>
		            <strong>Given Input:</strong> ${rslt.givenInput}<br>
		            <strong>Expected Output:</strong> ${rslt.expectedOutput}<br>
		            <strong>Actual Output:</strong> ${rslt.actualOutput}<br>
		            <strong>Result:</strong> <span style="color: ${color}; font-weight: bold;">${rslt.result}</span>
		        </div>
			`;
			
			resultsDiv.appendChild(resultDiv);
	    })						
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