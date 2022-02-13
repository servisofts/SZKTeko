import { SPageListProps } from 'servisofts-component'
const ServiceName = "zkteco";

import punto_venta from './Components/punto_venta';
import dispositivo from './Components/dispositivo';
import tipo_dispositivo from './Components/tipo_dispositivo';
const Pages: SPageListProps = {
    ...punto_venta.Pages,
    ...dispositivo.Pages,
    ...tipo_dispositivo.Pages
}

const Reducers = {
    ...punto_venta.Reducers,
    ...dispositivo.Reducers,
    ...tipo_dispositivo.Reducers
}

export default {
    ServiceName,
    Pages,
    Reducers

};

