const initialState = {
    estado: "",
    data: {},
    dato: {},
    dato_cabecera: {},
}

export default (state, action) => {
    if (!state) return initialState;
    if (action.component == "cabeceraDato") {
        switch (action.type) {
            case "registro":
                registro(state, action);
                break;
            case "eliminar":
                eliminar(state, action);
                break;
            case "getAll":
                getAll(state, action);
                break;
            case "registroDato":
                registroDato(state, action);
                break;
            case "getAllDato":
                getAllDato(state, action);
                break;
            case "getAllTipoDato":
                getAllTipoDato(state, action);
                break;
            case "getAllTipoDatoCabecera":// todos los datos que manejamos
                getAllTipoDatoCabecera(state, action);
                break;
            case "getDatoCabecera":// todos los datos que manejamos
                getDatoCabecera(state, action);
                break;
            case "registroDatoCabecera":// todos los datos que manejamos
                registroDatoCabecera(state, action);
                break;
        }
        state.type = action.type;
        state.estado = action.estado;
        state.error = action.error;
        state = { ...state };
    }
    return state;
}
const registro = (state, action) => {
    if (action.estado === "exito") {
        if (state.data[action.servicio.key]) {
            state.data[action.servicio.key][action.data.key] = action.data;
        }

    }
}
const eliminar = (state, action) => {
    if (action.estado === "exito") {
        if (state.data[action.servicio.key]) {
            delete state.data[action.servicio.key][action.data.key];
        }

    }
}
const getAll = (state, action) => {
    if (action.estado === "exito") {
        state.data[action.servicio.key] = {};
        Object.keys(action.data).map(key => {
            state.data[action.servicio.key][key] = action.data[key];
        })
    }
}

const registroDato = (state, action) => {
    if (action.estado === "exito") {
        if (state.data[action.servicio.key]) {
            state.dato[action.servicio.key][action.data.key] = action.data;
        }

    }
}
const getAllDato = (state, action) => {
    if (action.estado === "exito") {
        state.dato[action.servicio.key] = {};
        action.data.map((obj, i) => {
            state.dato[action.servicio.key][obj.key] = obj;
        })
    }
}
const getAllTipoDato = (state, action) => {
    if (action.estado === "exito") {
        state.tipoDato = {};
        action.data.map(item => {
            state.tipoDato[item.key] = item;
        })
    }
}
const getAllTipoDatoCabecera = (state, action) => {
    if (action.estado === "exito") {
        state.tipoDatoCabecera = {};
        action.data.map(item => {
            state.tipoDatoCabecera[item.key] = item;
        })
    }
}
const getDatoCabecera = (state, action) => {
    if (action.estado === "exito") {
        if (!state.dato_cabecera[action.servicio.key]) {
            state.dato_cabecera[action.servicio.key] = {}
        }
        state.dato_cabecera[action.servicio.key][action.cabecera] = action.data;
    }
}
const registroDatoCabecera = (state, action) => {
    if (action.estado === "exito") {
        if (state.dato_cabecera[action.servicio.key]) {
            delete state.dato_cabecera[action.servicio.key] 
        }
    }
}