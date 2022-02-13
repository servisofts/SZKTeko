import { combineReducers } from 'redux';

import { Reducers} from '../Pages'
import Services from '../Services'
const reducers = combineReducers({
    ...Services.Reducers,
    ...Reducers
   
});

export default (state, action) => {
    switch (action.type) {
        case 'USUARIO_LOGOUT':
            state = undefined;
            break;
        default:
            break;
    }
    return reducers(state, action);
}