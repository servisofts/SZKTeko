//  COMPONENT CONFIG
const component = "dispositivo"; // COMPONENT NAME 
const version = "1.0";
// ---------------------------------------
import Actions from "./Actions";
import Reducer from "./Reducer";

import Lista from "./Pages/Lista";
import Registro from "./Pages/Registro";
import Perfil from "./Pages/Perfil";
import Select from "./Pages/Select";
import ListaUsuarios from "./Pages/ListaUsuarios";
import DataTable from "./Pages/DataTable";
export default {
    component,
    version,
    Actions,
    Reducers: {
        [component + 'Reducer']: Reducer
    },
    Pages: {
        [component]: Lista,
        [component + "/registro"]: Registro,
        [component + "/perfil"]: Perfil,
        [component + "/select"]: Select,
        [component + "/listaUsuario"]: ListaUsuarios,
        [component+"/dataTable"]:DataTable
    }
}