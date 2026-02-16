package fr.framelab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.framelab.dto.LoginRequest;
import fr.framelab.dto.TokenResponse;
import fr.framelab.model.User;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {
    private final HttpClient client;
    private final ObjectMapper mapper;

    public AuthService() {
        this.mapper = new ObjectMapper();

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

    }

    public TokenResponse login (String mail,String password) throws Exception {
        try {

            String jsonBody = mapper.writeValueAsString(
                    new LoginRequest(mail, password)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5500/api/auth/login"))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), TokenResponse.class);
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
