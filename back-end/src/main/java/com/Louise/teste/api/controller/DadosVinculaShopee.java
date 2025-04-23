package com.Louise.teste.api.controller;

import java.util.List;

public record DadosVinculaShopee(
        List<String> listaSkus,
        List<Long> idsShopee,
        Double preco
) {
}
