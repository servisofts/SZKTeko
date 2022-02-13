import { SPageListProps } from 'servisofts-component'
const ServiceName = "proyecto";

import proyecto from './Components/proyecto';
import cargo from './Components/cargo';
import tipo_cargo from './Components/tipo_cargo';
import etapa from './Components/etapa';
import tipo_seguimiento from './Components/tipo_seguimiento';
import seguimiento from './Components/seguimiento';
const Pages: SPageListProps = {
    ...proyecto.Pages,
    ...cargo.Pages,
    ...tipo_cargo.Pages,
    ...etapa.Pages,
    ...tipo_seguimiento.Pages,
    ...seguimiento.Pages
}

const Reducers = {
    ...proyecto.Reducers,
    ...cargo.Reducers,
    ...tipo_cargo.Reducers,
    ...etapa.Reducers,
    ...tipo_seguimiento.Reducers,
    ...seguimiento.Reducers
}

export default {
    ServiceName,
    Pages,
    Reducers

};

