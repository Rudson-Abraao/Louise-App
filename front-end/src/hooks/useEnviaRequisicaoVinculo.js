import { useCallback, useState } from "react";

export default function useEnviaRequisicaoVinculo() {
    
    const [loading, setLoading] = useState(false);
    const [vinculoFinalizado, setVinculoFinalizado] = useState(false);

    const vincular = useCallback(async (skus, idShopee, preco) => {
        setLoading(true)

        const skusConvertido = skus
            .split('\n')
            .map(v => v.trim())
            .filter(v => v !== '');

        const idShopeeConvertido = idShopee
            .split('\n')
            .map(v => v.trim())
            .filter(v => v !== '');

        console.log(skusConvertido);
        console.log(idShopeeConvertido);

        const options = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                idsShopee: idShopeeConvertido,
                listaSkus: skusConvertido,
                preco: preco
            })
        };

        try {
            await fetch('http://localhost:8080/bling/shopee-integracao', options)
                .then(response => {
                    if (!response.ok) {
                        console.log('erro na requisicao');
                        window.location.href = 'http://localhost:8080/home'
                    } 
                })
        }
        catch (err) {
            console.log(err.message);
        }
        finally {
            setLoading(false)
            setVinculoFinalizado(true)
        }

    }, [])
    

    return {loading,vinculoFinalizado,vincular}
}