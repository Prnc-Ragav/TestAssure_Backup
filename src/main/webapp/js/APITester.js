document.getElementById("startTesting").addEventListener("click", function () {
    let fileInput = document.getElementById("inputURL").files[0];

    if (!fileInput) {
        alert("Please select a file!");
        return;
    }

    let formData = new FormData();
    formData.append("file", fileInput);
    fetch("APITester", {
        method: "POST",
        body: formData, 
    }) 
    .then(response => response.json())
    .then(data => {
        console.log(data);
        let passedResultDiv = document.getElementById("passed");
        let failedResultDiv = document.getElementById("failed");

        passedResultDiv.innerHTML = "";
        failedResultDiv.innerHTML = "";

        if (data.error) {
            passedResultDiv.innerHTML = `<p style="color: red;">Error: ${data.error}</p>`;
        } else if (data.length === 0) {
            passedResultDiv.innerHTML = `<p style="color: red;">No links found in the given file</p>`;
        } else {
            data.forEach(link => {
                if (link != null) {
					let baseUrl = link.baseUrl;
					let endpoint = link.endpoint;
					let method = link.method;
					let requestBody = link.requestBody.split(",");
					let responseCode = link.responseCode;
					let responseBody = link.responseBody;
					let isSuccess = link.isSuccess;
					let body = "";
					let description = requestBody[1].split(":")[1];
					for(let i=2;i<requestBody.length;i++)
					{
						body += requestBody[i];	
					}
					body = body.split(":")[1];

					                    
					
					if(isSuccess)
					{
						console.log(body);
						passedResultDiv.innerHTML += `
											    <div class="apiTestResult pass" style="border-left: 5px solid ${isSuccess ? 'green' : 'red'};">
											        <strong>Base URL:</strong> ${baseUrl} <br>
											        <strong>Endpoint:</strong> ${endpoint} <br>
											        <strong>Method:</strong> ${method} <br>
											        <strong>Request Body:</strong> ${body} <br>
													<strong>Request Description:</strong> ${description} <br>
											        <strong>Response Code:</strong> ${responseCode} <br>
											        <strong>Response Body:</strong> ${responseBody} <br>
											        <strong>Status:</strong> Pass <br>
											        
											    </div>`;	
					}
					else{
						failedResultDiv.innerHTML += `
											    <div class="apiTestResult fail" style="border-left: 5px solid ${isSuccess ? 'green' : 'red'};">
											        <strong>Base URL:</strong> ${baseUrl} <br>
											        <strong>Endpoint:</strong> ${endpoint} <br>
											        <strong>Method:</strong> ${method} <br>
											        <strong>Request Body:</strong> ${body} <br>
													<strong>Request Description:</strong> ${description} <br>
											        <strong>Response Code:</strong> ${responseCode} <br>
											        <strong>Response Body:</strong> ${responseBody} <br>
											        <strong>Status:</strong> Fail <br>
											        
											    </div>`;
					}

                }
            });
        }
    })
    .catch(error => console.error("Error:", error.toString()));
});
