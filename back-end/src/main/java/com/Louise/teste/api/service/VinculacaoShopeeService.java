package com.Louise.teste.api.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VinculacaoShopeeService {

    private final WebClient webClient;
    private final TokenStorageService tokenStorageService;

    public VinculacaoShopeeService(WebClient.Builder webClientBuilder, TokenStorageService tokenStorageService) {
        this.webClient = webClientBuilder.baseUrl("https://www.bling.com.br/Api/v3").build();
        this.tokenStorageService = tokenStorageService;
    }

    private Long idProdutoPai;
    private List<Long> idVariacoes;
    private Map<String, Long> mapaSkuId = new HashMap<>();
    private Long idVariacao;
    private Integer idVinculoLoja;


    public void vinculaShopee(List<Long> idsShopee, List<String> skus, Double preco) {
        var token = tokenStorageService.pegaToken();

        mapeamento(skus,idsShopee);
        pegaIdProdutoPai(token,skus.getFirst());
        pegaVariacoes(token);
        consultaVariacoesLoja(token,preco);
        atualizaVinculacao(token, skus, preco);
        System.out.println("""
                
                ************************************
                Todos os vinculos foram atualizados
                ************************************
                """);

    }



    public void mapeamento(List<String> skus, List<Long> idsShopee) {
        for (int i = 0; i < skus.size() && i < idsShopee.size(); i++) {
            mapaSkuId.put(skus.get(i), idsShopee.get(i));
        }
    }




    public void pegaIdProdutoPai(String token, String sku) {
        webClient.get()
                .uri("/produtos?sku=" + sku)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(res -> {
                    List data = (List) res.get("data");
                    Map<String,Object> produto = (Map<String, Object>) data.get(0);
                    this.idProdutoPai = (Long) produto.get("idProdutoPai");
                })
                .block();
    }

    public void pegaVariacoes(String token) {
        webClient.get()
                .uri("/produtos/variacoes/" + idProdutoPai)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(res -> {
                    Map<String,Object> data = (Map<String, Object>) res.get("data");
                    List<Map<String,Object>> variacoes = (List<Map<String, Object>>) data.get("variacoes");
                    this.idVariacoes = variacoes.stream()
                            .map(v -> (Long) v.get("id"))
                            .collect(Collectors.toList());
                })
                .block();

        System.out.println("ids das variacoes :::: " + idVariacoes);

    }

    public void consultaVariacoesLoja(String token, Double preco) {
        idVariacoes.stream().forEach( v -> {

            webClient.get()
                    .uri("/produtos/lojas?idLoja=205392202&idProduto=" + v)
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnNext(res -> {
                        List data = (List) res.get("data");
                        if (data.size() != 0) {
                            System.out.println("produto esta na loja");
                        } else {
                            System.out.println("produto não esta na loja");
                            inserirProdutoLoja(token,preco,v);
                        }
                    })
                    .delayElement(Duration.ofMillis(400))
                    .block();
        });

    }

    public void inserirProdutoLoja (String token, Double preco, Long variacao ) {

        var bodyRequisicao = String.format(Locale.US,"""
                {
                  "codigo": "000",
                  "produto": {
                    "id": %d
                  },
                  "loja": {
                    "id": 205392202
                  },
                  "preco": %f
                }
                """,variacao,preco);


        webClient.post()
                .uri("/produtos/lojas")
                .headers(headers -> headers.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyRequisicao)
                .retrieve()
                .bodyToMono(String.class)
                .delayElement(Duration.ofMillis(400))
                .subscribe();

        System.out.println("produto inserido na loja");
    }



    private void atualizaVinculacao(String token, List<String> skus, Double preco) {
        skus.forEach(s -> {
            pegaIdVariacao(token,s);
            pegaVinculoLoja(token);

            var bodyRequisicao = String.format(Locale.US,"""
                {
                  "codigo": "%s",
                  "produto": {
                    "id": %d
                  },
                  "loja": {
                    "id": 205392202
                  },
                  "preco": %f
                }
                """, mapaSkuId.get(s), idVariacao, preco );


            webClient.put()
                    .uri("/produtos/lojas/"+idVinculoLoja)
                    .headers(headers -> headers.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(bodyRequisicao)
                    .retrieve()
                    .bodyToMono(String.class)
                    .delayElement(Duration.ofMillis(400))
                    .subscribe();

            System.out.println("vinculo atualizado");
        });

    }

    public void pegaIdVariacao(String token, String sku) {
        webClient.get()
                .uri("/produtos?sku=" + sku)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(res -> {
                    List data = (List) res.get("data");
                    Map<String,Object> produto = (Map<String, Object>) data.get(0);
                    this.idVariacao = (Long) produto.get("id");
                })
                .delayElement(Duration.ofMillis(400))
                .block();
    }

    public void pegaVinculoLoja(String token) {
        webClient.get()
                .uri("/produtos/lojas?idLoja=205392202&idProduto=" + idVariacao)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(res -> {
                    List data = (List) res.get("data");
                    Map<String, Object> dadosProduto = (Map<String, Object>) data.getFirst();
                    this.idVinculoLoja = (Integer) dadosProduto.get("id");
                })
                .delayElement(Duration.ofMillis(400))
                .block();

    }

}
