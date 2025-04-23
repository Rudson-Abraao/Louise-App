package com.Louise.teste.api.controller;

import com.Louise.teste.api.service.VerificacaoService;
import com.Louise.teste.api.service.VinculacaoShopeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping("/bling")
public class BlingController {

    private final VerificacaoService verificacaoService;
    private final VinculacaoShopeeService vinculacaoShopeeService;

    public BlingController(VerificacaoService verificacaoService, VinculacaoShopeeService vinculacaoShopeeService) {
        this.verificacaoService = verificacaoService;
        this.vinculacaoShopeeService = vinculacaoShopeeService;
    }

    @PostMapping("/verificacao")
    public ResponseEntity<List<String>> verificaPrecoLoja(@RequestBody DadosVerificacao dados ) {
        var retorno =  verificacaoService.verificaPrecoLoja(dados.sku(),dados.preco(),dados.loja());
        return ResponseEntity.ok(retorno);
    }



    @PostMapping("/shopee-integracao")
    public void vinculacaoShopee(@RequestBody DadosVinculaShopee dados) {
        vinculacaoShopeeService.vinculaShopee(dados.idsShopee(),dados.listaSkus(), dados.preco());
        System.out.println("requisicao acabou");
//        return new RedirectView("http://localhost:8080/home");

    }
}

