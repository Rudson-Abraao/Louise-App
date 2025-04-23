package com.Louise.teste.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.Instant;
import java.util.Map;

@Service
public class TokenStorageService {

    private String accessToken;
    private String refreshToken;
    private Instant expiracaoToken;
    private String basicAuth;

    private final WebClient webClient;


    public TokenStorageService(WebClient.Builder webClientBuilder, HttpServletResponse response) {
        this.webClient = webClientBuilder.baseUrl("https://www.bling.com.br").build();

    }

    public void saveToken(String token,String refreshToken, Long expiraEm, String basicAuth) {

        System.out.println(token);
        System.out.println(refreshToken);

        this.accessToken = token;
        this.refreshToken= refreshToken;
        this.expiracaoToken = Instant.now().plusSeconds(expiraEm);
        this.basicAuth = basicAuth;
    }

    public String pegaToken() {


        if (accessToken == null || Instant.now().isAfter(expiracaoToken)) {
            webClient.post()
                            .uri("Api/v3/oauth/token")
                                    .headers(headers -> {
                                        headers.set("Authorization", "Basic " + basicAuth);
                                        headers.set("Content-Type", "application/x-www-form-urlencoded");
                                    })
                                            .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                                                    .with("refresh_token", refreshToken))
                                                    .retrieve()
                                                            .bodyToMono(Map.class)
                                                                    .doOnNext(response -> {
                                                                        this.accessToken = (String) response.get("access_token");
                                                                        this.refreshToken = (String) response.get("refresh_token");
                                                                        Long expiraEm = ((Number) response.get("expires_in")).longValue();
                                                                        this.expiracaoToken = Instant.now().plusSeconds(expiraEm);

                                                                    })
                                                                            .block();
            return accessToken;
        }

        return accessToken;
    }

    
}
