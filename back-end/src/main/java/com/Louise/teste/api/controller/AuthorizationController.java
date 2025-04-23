package com.Louise.teste.api.controller;

import com.Louise.teste.api.service.OAuth2TokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RestController
public class AuthorizationController {

    @Autowired
    OAuth2TokenService tokenService;

    @Autowired
    HttpServletResponse response;

    @Value("${bling.client_id}")
    private String clienteId = "e7c65e332679b2ea4d8b4dd96d395234f677181d";


    @GetMapping("/home")
    public RedirectView login() {
        return new RedirectView("https://www.bling.com.br/Api/v3/oauth/authorize?response_type=code&client_id=" + clienteId +"&state=9270ef59840db7d76ed76b4d258799f1");
    }

    @GetMapping("/oauth/callback")
    public RedirectView acessarToken (@RequestParam("code") String codigo) {
        tokenService.getAccessToken(codigo);
        return new RedirectView("http://localhost:8080/aplicacoes");
    }

    @GetMapping("/aplicacoes")
    public RedirectView paginaInicial() {
          return new RedirectView("http://localhost:5173/");
    }




}
