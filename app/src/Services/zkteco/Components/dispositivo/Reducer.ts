import Parent from './index'

type DataProps = {
    component: any,
    type: string,
    version?: any,
    estado?: "exito" | "cargando" | "error",
    error: any,
    [key: string]: any;
}

const initialState = () => {
    var initialState: any = {
        component: Parent.component,
        version: Parent.version,
        data_usuarios:{},
    }
    return initialState;
}
export default (state: any, action: DataProps) => {
    if (!state) return initialState();
    if (action.component != Parent.component) return state;
    // if (action.version != Parent.version) return state;
    TypesSwitch(state, action)
    state.type = action.type;
    state.estado = action.estado;
    state.error = action.error;
    state.lastSend = new Date();
    state = { ...state };
    return state;
}

const TypesSwitch = (state: any, action: DataProps) => {
    switch (action.type) {
        case "getAll": return getAll(state, action);
        case "registro": return registro(state, action);
        case "editar": return editar(state, action);
        case "getByKey": return getByKey(state, action);
        case "copiar": return copiar(state, action);
        case "getUsuarios": return getUsuarios(state, action);
        case "getDataTable": return getDataTable(state, action);

    }
}

const getAll = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    state.data = action.data;
}

const registro = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    state.lastRegister = action.data;
    if (!state.data) return;
    state.data[action.data.key] = action.data;
}
const copiar = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    if (!state.data) return;
    action.data.map((obj: any) => {
        state.data[obj.key] = obj;
    })

}
const editar = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    if (!state.data) return;
    state.data[action.data.key] = action.data;
}
const getByKey = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    state.data[action.data.key] = action.data;
}
const getUsuarios = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    state.data_usuarios[action.dispositivo.key] = action.data;
}
const getDataTable = (state: any, action: DataProps) => {
    if (action.estado != "exito") return;
    state.table = action.table;
    state.dataTable = action.data;
}