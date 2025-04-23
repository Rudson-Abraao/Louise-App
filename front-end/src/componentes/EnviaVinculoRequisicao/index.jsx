// const EnviaVinculoRequisicao = (skus,idShopee,preco) => {

//     console.log('teste bem sucedido');
    

//     const skusConvertido = skus
//         .split('\n')
//         .map(v => v.trim())
//         .filter(v => v !== '');
        
//         const idShopeeConvertido = idShopee
//         .split('\n')
//         .map(v => v.trim())
//         .filter(v => v !== '');
    
//         console.log(skusConvertido);
//         console.log(idShopeeConvertido);
        
//         const options = {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json'
//             },
//             body: JSON.stringify({
//                 idsShopee: idShopeeConvertido,
//                 listaSkus: skusConvertido,
//                 preco: preco
//             })
//         };

//         fetch('http://localhost:8080/bling/shopee-integracao',options)
//         .then(response => {
//             if(!response.ok) {
//                 console.log('erro na requisicao');
//             } else {
//                 console.log('sucesso na requisição');
//             }
//         })
    

// }

// export default EnviaVinculoRequisicao;