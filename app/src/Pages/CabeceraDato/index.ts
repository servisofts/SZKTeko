import { SPageListProps } from 'servisofts-component'
import Actions from './Actions'



import TipoDato from './Pages/TipoDato'
import RegistroCabeceraDato from './Pages/RegistroCabeceraDato'
import RegistroDato from './Pages/RegistroDato'
import CabeceraPerfil from './Pages/CabeceraPerfil'
const Pages: SPageListProps = {
    "tipoDato": TipoDato,
    "registroCabeceraDato": RegistroCabeceraDato,
    "registroDato": RegistroDato,
    "cabecera": CabeceraPerfil
}


import cabeceraDatoReducer from './Reducer/cabeceraDatoReducer'
const Reducers = {
    cabeceraDatoReducer,
}


export default {
    Pages,
    Actions,
    Reducers
};