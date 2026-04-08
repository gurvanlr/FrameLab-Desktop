package fr.framelab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.framelab.dto.ChallengeResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChallengeService {
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ChallengeService () {
        this.mapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }

    public ChallengeResponse[] getChallenge() throws Exception {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5500/api/challenges/active"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200) {
                System.out.println("succes\n"+response.body().toString());
                return mapper.readValue(response.body(), ChallengeResponse[].class);
            }

            if (response.statusCode() == 401) {
                return null;
            }

            throw new Exception("Erreur serveur : " + response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new Exception("Impossible de contacter le serveur", e);
        }

    }

}
