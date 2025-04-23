import { use, useState } from "react";
import useEnviaRequisicaoVinculo from "../../hooks/useEnviaRequisicaoVinculo";
import styled from "styled-components";
import logo from "../../assets/logos/logoLouise.jpg"
import { ClipLoader } from "react-spinners";


const ContainerPrincipalEstilizado = styled.div`
width: 100vw;
height: 100vh;
display: flex;
justify-content: center;
align-items: center;
background-color: black;
font-family: ${props => props.theme.font.fontePrincipal};
`
const ContainerFormEstilizado = styled.div`
padding: 10px;
width: 700px;
height: 500px;
display: flex;
justify-content: space-between;
gap: 10px;
background-color: white;
color: black;
font-size: 16px;
font-weight: bold;
border-radius: 10px;
border: solid 2px rebeccapurple;
box-shadow: 0px 0px 50px 0px rebeccapurple;
`
const ContainerTextAreaEstilizado = styled.div`
height: 100%;
display: flex;
flex-direction: column;
`
const TextAreaEstilizado = styled.textarea`
border-radius: 5px;
border: solid 2px rebeccapurple;
padding: 5px;
height: 100%;
resize: none;
`
const DivPrecoEstilizado = styled.div`
justify-self: start;
display: flex;
flex-direction: column;
align-items: center;
/* justify-content: center; */
gap: 2px;
`

const ContainerPrecoBotaoEstilizado = styled.div`
display: flex;
flex-direction: column;
width: 200px;
/* height: 100%; */
align-items: center;
justify-content: center;
gap: 15px;
position: relative;
/* bottom: 60px; */
`

const InputPrecoEstilizado = styled.input`
border: solid 2px rebeccapurple;
border-radius: 5px;
padding: 5px;
`

const BotaoEstilizado = styled.button`
padding: 5px;
width: 120px;
font-weight: bold;
color: black;
background-color: white;
border: none;
border-radius: 5px;
border: solid 2px rebeccapurple;
/* box-shadow: 0px 0px 5px 0px rebeccapurple; */
cursor: pointer;
`

const TituloEstilizado = styled.h1`
position: absolute;
top: 20%;
margin: 0;
`
const LogoEstilizado = styled.img`
position: absolute;
top: 0px;
width: 200px;

`

const NovoVinculo = styled.div`
/* position: absolute;
bottom: 30px; */
display: flex;
flex-direction: column;
justify-content: center;
align-items: center;
`

const Formulario = () => {


    const [skus, setSkus] = useState("");
    const [idShopee, setIdShopee] = useState("");
    const [preco, setPreco] = useState(0);
    // const [loading,setLoading] = useState(false);

    const { loading, vinculoFinalizado, vincular } = useEnviaRequisicaoVinculo();






    return (
        <ContainerPrincipalEstilizado>
            <ContainerFormEstilizado>

                <ContainerTextAreaEstilizado>
                    <label htmlFor="Sku">Sku's das variações</label>
                    <TextAreaEstilizado name="Sku" id="skus" onInput={(e) => setSkus(e.target.value)}></TextAreaEstilizado>
                </ContainerTextAreaEstilizado>
                <ContainerTextAreaEstilizado>
                    <label htmlFor="idShopee">Id's da shopee</label>
                    <TextAreaEstilizado name="idShopee" id="" onInput={(e) => setIdShopee(e.target.value)}></TextAreaEstilizado>
                </ContainerTextAreaEstilizado>

                <ContainerPrecoBotaoEstilizado>
                    {/* <TituloEstilizado>Louise App</TituloEstilizado> */}
                    {/* <img src="logoLouise.jpg" alt="" /> */}
                    {/* <img src={logo} alt="" srcset="" style={width: 40px} /> */}
                    <LogoEstilizado src={logo}></LogoEstilizado>
                    {!loading && !vinculoFinalizado &&
                        <DivPrecoEstilizado>
                            <label htmlFor="preco">Preço:</label>
                            <InputPrecoEstilizado type="number" id="preco" onInput={(e) => setPreco(e.target.value)} />
                        </DivPrecoEstilizado>
                    }
                    {loading ? <ClipLoader color="rebeccapurple" size={30} />
                        : vinculoFinalizado
                            ? <NovoVinculo>
                                <BotaoEstilizado onClick={() => window.location.href = 'http://localhost:8080/home'}> Novo vinculo</BotaoEstilizado>
                                <p>Vinculos Atualizados!</p>
                            </NovoVinculo>
                            : <BotaoEstilizado onClick={() => vincular(skus, idShopee, preco)}>Vincular</BotaoEstilizado>}

                </ContainerPrecoBotaoEstilizado>

            </ContainerFormEstilizado>

        </ContainerPrincipalEstilizado>
    )
};

export default Formulario;