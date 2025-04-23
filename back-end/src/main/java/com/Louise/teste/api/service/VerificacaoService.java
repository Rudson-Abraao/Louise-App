package com.Louise.teste.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VerificacaoService {

    private final WebClient webClient;
    private final TokenStorageService tokenStorageService;


    private Long idProdutoPai;
    private List<Long> idVariacoes;
    private List vinculosCriados = new ArrayList<>();
//    private List<String> produtosVinculados = new ArrayList<String>();

    public VerificacaoService(WebClient.Builder webClientBuilder, TokenStorageService tokenStorageService) {
        this.webClient = webClientBuilder.baseUrl("https://www.bling.com.br/Api/v3").build();
        this.tokenStorageService = tokenStorageService;
    }



    public List<String> verificaPrecoLoja(String sku, Double preco, Long loja) {
        System.out.println("sku::: " + sku);
        System.out.println("preco::: " + preco);
        System.out.println("loja::: " + loja);
        var token = tokenStorageService.pegaToken();

        vinculosCriados.removeAll(vinculosCriados);
        pegaIdProdutoPai(token,sku);
        pegaVariacoes(token);
        consultaVariacoesLoja(token,loja, preco);
        verificaPrecoNaLoja(token,loja,preco);
        if (!vinculosCriados.isEmpty()) {
            System.out.println("vinculados criados não está vazio");
            return pegaProduto(token,vinculosCriados);
        }else {
            return new ArrayList<>();
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
                    this.idProdutoPai = (Long) produto.get("id");
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

        System.out.println(idVariacoes);

    }

    public void consultaVariacoesLoja(String token ,Long loja, Double preco) {
        idVariacoes.stream().forEach( v -> {
            System.out.println("loja na requisição de verificar loja::: " + loja);
            System.out.println("variacao na requisição de verificar loja::: " + v);
            webClient.get()
                    .uri("/produtos/lojas?idLoja=" + loja + "&idProduto=" + v)
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnNext(res -> {
                        List data = (List) res.get("data");
                        if (data.size() != 0) {
                            System.out.println("produto esta na loja");
                        } else {
                            System.out.println("produto não esta na loja");
                            inserirProdutoLoja(token,preco,loja,v);
                        }
                    })
                    .delayElement(Duration.ofMillis(400))
                    .block();
        });

    }

    public void inserirProdutoLoja (String token, Double preco, Long loja, Long variacao ) {

        var bodyRequisicao = String.format(Locale.US,"""
                {
                  "codigo": "000",
                  "produto": {
                    "id": %d
                  },
                  "loja": {
                    "id": %d
                  },
                  "preco": %f
                }
                """,variacao,loja,preco);


        webClient.post()
                .uri("/produtos/lojas")
                .headers(headers -> headers.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyRequisicao)
                .retrieve()
                .bodyToMono(String.class)
                .delayElement(Duration.ofMillis(400))
                .subscribe();

        vinculosCriados.add(variacao);

        System.out.println("produto inserido na loja");
    }

    public void verificaPrecoNaLoja(String token ,Long loja, Double preco) {
        idVariacoes.forEach(idVariacao -> {
            webClient.get()
                    .uri("/produtos/lojas?idLoja=" + loja + "&idProduto=" + idVariacao)
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnNext(res -> {
                        List data = (List) res.get("data");
                        Map<String, Object> dadosProduto = (Map<String, Object>) data.get(0);
                        Integer idVinculoLoja = (Integer) dadosProduto.get("id");
                        Number precoNumber = (Number) dadosProduto.get("preco");
                        Double precoNaLoja = precoNumber.doubleValue();
                        String idProdutoLoja = (String) dadosProduto.get("codigo");;

                        int compare = Double.compare(precoNaLoja,preco);
                        if (compare == 0) {
                            System.out.println("preco ok");
                        } else {
                            System.out.println("preco errado");
                            alteraPrecoProduto(token,idVinculoLoja, idProdutoLoja, idVariacao, loja, preco);
                        }
                    })
                    .delayElement(Duration.ofMillis(400))
                    .block();
        });

    }

    public void alteraPrecoProduto (String token,Integer idVinculoLoja, String idProdutoLoja, Long idVariacao, Long loja, Double preco) {

        System.out.println("id na loja::: " + idProdutoLoja);

        var bodyRequisicao = String.format(Locale.US,"""
                {
                  "codigo": "%s",
                  "produto": {
                    "id": %d
                  },
                  "loja": {
                    "id": %d
                  },
                  "preco": %f
                }
                """, idProdutoLoja, idVariacao, loja, preco );


        webClient.put()
                .uri("/produtos/lojas/"+idVinculoLoja)
                .headers(headers -> headers.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyRequisicao)
                .retrieve()
                .bodyToMono(String.class)
                .delayElement(Duration.ofMillis(550))
                .subscribe();

        System.out.println("preço corrigido" );

    }

    public List<String> pegaProduto(String token, List variacoes) {
        List<String> produtosVinculados = new ArrayList<>();
        variacoes.forEach(variacao -> {
            webClient.get()
                    .uri("/produtos/" + variacao)
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnNext(res -> {
                        Map<String, Object> data = (Map<String, Object>) res.get("data");
                        String sku = (String) data.get("codigo");
                        System.out.println("sku do produto vinculado::: " + sku);
                        produtosVinculados.add(sku);
                    })
                    .delayElement(Duration.ofMillis(400))
                    .block();
        });
        System.out.println("produtos vinculados ::: " + produtosVinculados);
        return produtosVinculados;


    }

}

