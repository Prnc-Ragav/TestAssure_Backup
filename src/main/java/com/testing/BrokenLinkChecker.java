package com.testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BrokenLinkChecker extends Thread{
	
	
//	public static void main(String[] args) {
//		testURL("http://127.0.0.1:5500/form3/index.html");
//	}
//   
	 
    public static List<BrokenLinkResults> testURL(String pageurl) {
    	
    	WebDriverManager.chromedriver().setup();
    	System.out.println("--------------------------------------");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(pageurl);

        List<String> links = new ArrayList<String>() ;
        for(WebElement ele : driver.findElements(By.tagName("a")))
        {
        	
        	try {
        		links.add(ele.getAttribute("href"));
			} catch (Exception e) {
				System.out.println("Can't able to get href");
			}
        	
        }
        driver.quit();
        
        
        System.out.println(links.size());
        
        
        
        
        
      
//        for (String link : links) {
//            testResults.add(checkLink(link));
//        }
        
        
        
       return processListInThreads(links);
         
        
//        return LinkCheckerThread.results;
	}
    
    
    public static BrokenLinkResults checkLink1(String linkUrl) {
        BrokenLinkResults testResult = null;
        
        try {
            // Construct the cURL command
            ProcessBuilder pb = new ProcessBuilder("curl", "-s", "-o", "/dev/null", "-w", "%{http_code}", linkUrl);
            Process process = pb.start();

            // Capture the output (HTTP status code)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String responseCodeStr = reader.readLine();
            process.waitFor(); // Wait for the process to complete

            int responseCode = Integer.parseInt(responseCodeStr.trim()); // Convert to integer
            
            if (responseCode >= 400) {
                testResult = new BrokenLinkResults(linkUrl, "Page not loaded", responseCode, "Fail");
                System.out.println("❌ Broken: " + linkUrl + " (Status: " + responseCode + ")");
            } else {
                testResult = new BrokenLinkResults(linkUrl, "Page loaded successfully", responseCode, "Passed");
                System.out.println("✅ Working: " + linkUrl);
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + linkUrl + " - " + e.getMessage());
        }

        return testResult;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static List<BrokenLinkResults> processListInThreads(List<String> items) {
    	List<BrokenLinkResults> results = new ArrayList<BrokenLinkResults>();
        int size = items.size();
        int chunkSize = (int) Math.ceil((double) size / 4); // Divide the list into 4 parts
 
        List<LinkCheckerThread> threads = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, size);

            if (start < end) {  // Avoid empty sublists
                List<String> sublist = items.subList(start, end);
                LinkCheckerThread thread = new LinkCheckerThread(sublist);
                threads.add(thread);
                thread.start();  // Start each thread
            }
        }

        // Wait for all threads to finish
        for (LinkCheckerThread thread : threads) {
            try {
                thread.join();  // Ensures main thread waits for all child threads
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        for(LinkCheckerThread thread : threads)
        {
        		results.addAll(thread.getResults());
        }

        System.out.println("✅ All threads completed!");
        return results;
    }
    
    
 
//    public static BrokenLinkResults checkLink(String linkUrl) {
//    	BrokenLinkResults testResult = null; 
//        try {
//            URL url = new URL(linkUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//            connection.setRequestProperty("User-Agent", 
//                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
//            connection.setInstanceFollowRedirects(true);
//            connection.connect();
//            
//            
//            int responseCode = connection.getResponseCode();
//            
//            if (responseCode >= 400) {
//            	testResult = new BrokenLinkResults(linkUrl,"Page not loaded", responseCode,"Fail");
//                System.out.println("❌ Broken: " + linkUrl + " (Status: " + responseCode + ")");
//            } else {
//            	testResult = new BrokenLinkResults(linkUrl,"Page loaded successfully", responseCode,"Passed");
//                System.out.println("✅ Working: " + linkUrl);
//            }
//        } catch (Exception e) {
//            System.out.println("❌ Error: " + linkUrl);
//        }
//        
//        return testResult;
//        
//    }
    
    
    
    
    
    public static BrokenLinkResults checkLink(String linkUrl) {
        BrokenLinkResults testResult = null; 
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(linkUrl);
            
            // Setting headers to mimic a real browser
            request.setHeader("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int responseCode = response.getCode();

                if (responseCode >= 400) {
                	
                    testResult = checkLink1(linkUrl);
                    System.out.println("❌ Broken: " + linkUrl + " (Status: " + responseCode + ")");
                } else {
                    testResult = new BrokenLinkResults(linkUrl, "Page loaded successfully", responseCode, "Passed");
                    System.out.println("✅ Working: " + linkUrl);
                }

                // (Optional) Read and discard response content
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    EntityUtils.consume(entity);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + linkUrl + " - " + e.getMessage());
        }

        return testResult;
    }
    
    
    
    
    
    
    
    
    
    
    
    
}









class LinkCheckerThread extends Thread {
	
	private List<String> linkURLs;
	public List<BrokenLinkResults> results = new ArrayList<BrokenLinkResults>();

    public LinkCheckerThread(List<String> subList) {
        this.linkURLs = subList;
    }
	
	
	@Override
	public void run(){
		for(String linkUrl :  linkURLs)
			results.add(BrokenLinkChecker.checkLink(linkUrl));
	}


	public List<BrokenLinkResults> getResults() {
		return results;
	}
	
}
