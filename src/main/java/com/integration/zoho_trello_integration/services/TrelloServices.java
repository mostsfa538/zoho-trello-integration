package com.integration.zoho_trello_integration.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TrelloServices {

    @Value("${trello.api.key}")
    private String apiKey;

    @Value("${trello.access.token}")
    private String accessToken;

    public String getBoardId(String boardName) {
        String url = "https://api.trello.com/1/members/me/boards?key=" + apiKey + "&token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonResponse = new ObjectMapper().readTree(response.getBody());
                
                for (JsonNode board : jsonResponse) {
                    if (boardName.equals(board.get("name").asText())) {
                        return board.get("id").asText();
                    }
                }
                return null;
            } else {
                System.err.println("Failed to get boards: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error getting board ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean createBoard(String boardName) {
        String boardId = getBoardId(boardName);
        if (boardId != null) {
            System.out.println("Board already exists with ID: " + boardId);
            return true;
        }
        
        String url = "https://api.trello.com/1/boards/?name=" + boardName + "&key=" + apiKey + "&token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonResponse = new ObjectMapper().readTree(response.getBody());
                boardId = jsonResponse.get("id").asText();

                createLists("To_Do", boardId);
                createLists("In_Progress", boardId);
                createLists("Done", boardId);
                return true;
            } else {
                System.err.println("Failed to create board: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error creating board: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void createLists(String name, String boardId) {
        String url = "https://api.trello.com/1/lists?name=" + name +
                     "&idBoard=" + boardId +
                     "&key=" + apiKey +
                     "&token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonResponse = new ObjectMapper().readTree(response.getBody());
                String listId = jsonResponse.get("id").asText();

                if ("To_Do".equals(name)) {
                    createCard("Kickoff Meeting Scheduled", listId);
                    createCard("Requirements Gathering", listId);
                    createCard("System Setup", listId);
                }
            } else {
                System.err.println("Failed to create list: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error creating list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createCard(String cardName, String listId) {
        String url = "https://api.trello.com/1/cards?name=" + cardName +
                     "&desc=" + cardName +
                     "&idList=" + listId +
                     "&key=" + apiKey +
                     "&token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                entity,
                String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Card created successfully: ");
            } else {
                System.err.println("Failed to create card: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error creating card: " + e.getMessage());
            e.printStackTrace();
        }
    }
}