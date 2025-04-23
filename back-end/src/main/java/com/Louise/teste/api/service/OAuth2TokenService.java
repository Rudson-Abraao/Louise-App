package com.Louise.teste.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OAuth2TokenService {

    private final AtomicReference<String> accessTokenCache = new AtomicReference<>();

    private final WebClient webClient;

    private final TokenStorageService tokenStorageService;

    @Value("${bling.client_id}")
    private String clientId;

    @Value("${bling.client_secret}")
    private String clientSecret;

    @Value("${bling.redirect_uri}")
    private String redirectUri;



    public OAuth2TokenService(WebClient.Builder webClientBuilder, TokenStorageService tokenStorageService) {
        this.webClient = webClientBuilder.baseUrl("https://www.bling.com.br").build();
        this.tokenStorageService = tokenStorageService;
    }



    public void getAccessToken(String codigo) {

        System.out.println(codigo);
        String auth = clientId + ":" + clientSecret;
        System.out.println("client secret: " + clientSecret);
        System.out.println("client id: " + clientId);
        String basicAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        webClient.post()
                .uri("/Api/v3/oauth/token")
                .headers(headers -> {
                    headers.set("Authorization","Basic "+ basicAuth);
                    headers.set("Content-Type","application/x-www-form-urlencoded");
                })
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", codigo))

                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> {
                    String token = (String) response.get("access_token");
                    String refreshToken = (String) response.get("refresh_token");
                    Long expiraEm = ((Number) response.get("expires_in")).longValue();
                    tokenStorageService.saveToken(token, refreshToken, expiraEm, basicAuth);
                })
                .block();

    }
}
