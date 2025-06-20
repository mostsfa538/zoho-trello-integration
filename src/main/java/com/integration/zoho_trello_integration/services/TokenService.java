package com.integration.zoho_trello_integration.services;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class TokenService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${zoho.refresh-token}")
    private String refreshToken;

    @Value("${zohoClientId}")
    private String clientId;

    @Value("${zohoClientSecret}")
    private String clientSecret;

    @Value("${redirectUri}")
    private String redirectUri;

    private String accessToken;

    public String getAccessToken() {
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = refreshAcessToken();
        }
        return accessToken;
    }
    
    public String refreshAcessToken() {
        try {
            String url = "https://accounts.zoho.com/oauth/v2/token";
            HttpHeaders  headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("refresh_token", refreshToken);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("grant_type", "refresh_token");
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                request, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String newToken = (String) responseBody.get("access_token");
                
                if (newToken != null) {
                    return newToken;
                } else {
                    System.err.println("Access token not found in response: " + responseBody);
                    throw new RuntimeException("Access token not found in response");
                }
            } else {
                System.err.println("Failed to refresh access token: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                throw new RuntimeException("Failed to refresh access token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error refreshing access token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
