

import { ThemeProvider } from 'styled-components'
import EstilosGlobais from './componentes/EstilosGlobais'
import Formulario from './componentes/Formulario'

const App = () => {

  const tema = {
    font: {
      fontePrincipal: 'Fira Sans'
    }
  }

  return (
  <ThemeProvider theme={tema}>
    <EstilosGlobais />
    <Formulario />
  </ThemeProvider>
  )

}

export default App
