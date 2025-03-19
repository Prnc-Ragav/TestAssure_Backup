document.getElementById("startTesting").addEventListener("click", function () {
    let urlInput = document.getElementById("inputURL").value;
    
    if (!urlInput) {
        alert("Please enter a URL!");
        return;
    }

    fetch("BrokenLinkServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "url=" + encodeURIComponent(urlInput),
    }) 
    .then(response => {
		return response.json();
	})
    .then(data => {
		console.log(data);
        let passedResultDiv = document.getElementById("passed");
		let failedResultDiv = document.getElementById("failed");
		console.log(passedResultDiv);
		console.log(failedResultDiv);
        passedResultDiv.innerHTML = "";
		failedResultDiv.innerHTML = "";

        if (data.error) {
            passedResultDiv.innerHTML = `<p style="color: red;">Error: ${data.error}</p>`;
        }
		else if(data.length == 0)
		{
			passedResultDiv.innerHTML = `<p style="color: red;">No links found in the given page</p>`;
		} 
		else {
            data.forEach(link => {
				console.log(link);
				if(link != null){
					let color = link.result === "Passed" ? "green" : "red";
						if(color == "green")
						{
							passedResultDiv.innerHTML +=  `<div class="brokenLinkResult pass" style="border-left: 5px solid #4CAF50;">Test case Name : ${link.testCaseName} <br>  URL : ${link.url} <br>Expected Output :  ${link.expectedOutput} <br> Actual Output : ${link.actualOutput}</div>`;
						}
						else if(color == "red")
						{
							failedResultDiv.innerHTML += `<div class="brokenLinkResult fail" style="border-left: 5px solid #dc3545cf;">Test case Name : ${link.testCaseName} <br>  URL : ${link.url}  <br>Expected Output : ${link.expectedOutput} <br> Actual Output : ${link.actualOutput}</div>`;
						}
						else{
							failedResultDiv.innerHTML += `<div class="brokenLinkResult" style="color:red" > failed  </div>`;
						}
						console.log(color);
				}
               
            });
        }
    })
    .catch(error => console.error("Error:", error.toString()
	));
});
