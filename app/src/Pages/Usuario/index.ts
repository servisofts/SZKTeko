import { SPageListProps } from 'servisofts-component'
import Actions from './Actions'

import Index from './Page/Home'


const Pages: SPageListProps = {
    index: Index,
}


import usuarioReducer from './Reducer/usuarioReducer'
const Reducers = {
    usuarioReducer,
}


export default {
    Pages,
    Actions,
    Reducers
};