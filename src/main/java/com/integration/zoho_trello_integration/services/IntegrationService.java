package com.integration.zoho_trello_integration.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class IntegrationService {

    @Autowired
    private RestTemplate restTemplate;

    private TokenService tokenService;

    @Autowired
    private TrelloServices trelloServices;

    public IntegrationService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Scheduled(fixedRate = 60000)
    public void integrateZoho() {
        try {
            String responseData = fetchZohoData();
            if (responseData != null) {
                filteredResponse(responseData);
            } else {
                System.err.println("Failed to fetch data from Zoho CRM");
            }
        } catch (Exception e) {
            System.err.println("Error in scheduled integration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String fetchZohoData() {
        String zohoApiUrl = "https://www.zohoapis.com/crm/v2/Deals";
        
        try {
            String accessToken = tokenService.getAccessToken();
            
            if (accessToken == null || accessToken.isEmpty()) {
                System.err.println("Failed to obtain access token");
                return null;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                zohoApiUrl, 
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("Error fetching data from Zoho CRM: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void filteredResponse(String responseData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseData);
            JsonNode dataNode = rootNode.path("data");
            for (JsonNode deal: dataNode) {
                String dealStage = deal.path("Stage").asText();
                String dealType = deal.path("Type").asText();
                String projectBoardId = deal.path("Project_Board_ID__c").asText();
                
                if (dealStage.equals("Project Kickoff")
                    && dealType.equals("New Implementation Project")
                    && (projectBoardId == null || projectBoardId.isEmpty() || projectBoardId.equals("null"))) {
                        String boardName = deal.path("Deal_Name").asText() + " Board";
                        if (trelloServices.createBoard(boardName)) {
                            String boardId = trelloServices.getBoardId(boardName);
                            ((ObjectNode) deal).put("Project_Board_ID__c", boardId);
                        } else {
                            System.err.println("Failed to create Trello board for deal: " + deal.path("Deal_Name").asText());
                        }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing response data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
