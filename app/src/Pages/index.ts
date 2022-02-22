import { SPageListProps } from 'servisofts-component'

import InicioPage from "./InicioPage";
import CargaPage from './CargaPage/index';
import Usuario from './Usuario';
import AjustesPage from './AjustesPage';
import Servicios from './Servicios';
import CabeceraDato from './CabeceraDato';

import Services from '../Services';
const Pages: SPageListProps = {
    "inicio": InicioPage,
    "carga": CargaPage,
    AjustesPage,
    ...Usuario.Pages,
    ...Servicios.Pages,
    ...CabeceraDato.Pages,
    ...Services.Pages
}


export const Reducers = {
    ...Usuario.Reducers,
    ...Servicios.Reducers,
    ...CabeceraDato.Reducers
}
export default Pages;