package com.example.demo;  // adjust to your package

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WebhookClient {

    @PostConstruct
    public void runTask() {
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println("Starting webhook generation...");

            // Step 1: Generate webhook and get token
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> requestBody = Map.of(
                    "name", "Likithsai",
                    "regNo", "22BCE8282",
                    "email", "likithsaiyedoti@gmail.com"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);
            System.out.println("Access Token length: " + (accessToken != null ? accessToken.length() : "null"));

            // Step 2: Final SQL Query (Question 2)
            String finalQuery = "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, (" +
                    "SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e.DEPARTMENT " +
                    "AND DATEDIFF(day, e2.DOB, e.DOB) > 0) AS YOUNGER_EMPLOYEES_COUNT " +
                    "FROM EMPLOYEE e JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "ORDER BY e.EMP_ID DESC;";

            System.out.println("Sending SQL solution...");

            // Step 3: Submit the solution to the webhook using JWT in Authorization header
            HttpHeaders answerHeaders = new HttpHeaders();
            answerHeaders.setContentType(MediaType.APPLICATION_JSON);

            if (accessToken == null || accessToken.isEmpty()) {
                System.err.println("Error: AccessToken is null or empty!");
                return;
            }

            answerHeaders.setBearerAuth(accessToken.trim());

            Map<String, String> answerBody = Map.of("finalQuery", finalQuery);
            HttpEntity<Map<String, String>> answerRequest = new HttpEntity<>(answerBody, answerHeaders);

            System.out.println("Submitting to webhook URL: " + webhookUrl);

            ResponseEntity<String> submissionResponse = restTemplate.postForEntity(webhookUrl, answerRequest, String.class);

            System.out.println("Submission Response Status Code: " + submissionResponse.getStatusCode());
            System.out.println("Process completed successfully.");

        } catch (Exception e) {
            System.err.println("Error during task execution:");
            e.printStackTrace();
        }

    }
}
