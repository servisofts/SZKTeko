const initialState = {
    estado: "Not Found",
    data: false,
    servicioHabilitado: {},
    certificado: {},
}

export default (state, action) => {
    if (!state) return initialState
    if (action.component == "servicio") {
        switch (action.type) {
            case "getAll":
                getAll(state, action);
                break;
            case "getAllHabilitados":
                getAllHabilitados(state, action);
                break;
            case "registrar":
                registrar(state, action);
                break;
            case "getServicioHabilitado":
                getServicioHabilitado(state, action);
                break;
            case "getCertificado":
                getCertificado(state, action);
                break;
            case "registrarCertificado":
                registrarCertificado(state, action);
                break;
            case "switchHabilitado":
                switchHabilitado(state, action);
                break;
        }
        state.type = action.type;
        state.estado = action.estado;
        state.error = action.error;
        state.lastSend = new Date();
        state = { ...state };
    }
    return state;
}


const getAll = (state, action) => {
    if (action.estado === "exito") {
        state.data = {};
        action.data.map((item) => {
            state.data[item.key] = item;
        })
    }
}
const getAllHabilitados = (state, action) => {
    if (action.estado === "exito") {
        state.data = {};
        action.data.map((item) => {
            state.data[item.key] = item;
        })
    }
}
const registrar = (state, action) => {
    if (action.estado === "exito") {
        if (state.data) {
            state.data[action.data.key] = action.data;
        }
    }
}
const getServicioHabilitado = (state, action) => {
    if (action.estado === "exito") {
        state.servicioHabilitado[action.key] = {};
        action.data.map((item) => {
            state.servicioHabilitado[action.key][item.key_habilitado] = item;
        })

    }
}
const getCertificado = (state, action) => {
    if (action.estado === "exito") {
        state.certificado[action.key_servicio] = action.data;

    }
}
const switchHabilitado = (state, action) => {
    if (action.estado === "exito") {
        console.log(action);
        if (action.data == "on") {
            if (!state.servicioHabilitado[action.key_servicio]) {
                state.servicioHabilitado[action.key] = {};
            }
            state.servicioHabilitado[action.key_servicio][action.key_habilitado] = action.data_obj;
        } else {
            if (state.servicioHabilitado[action.key_servicio]) {
                delete state.servicioHabilitado[action.key_servicio][action.key_habilitado];
            }
        }

    }
}
const registrarCertificado = (state, action) => {
    if (action.estado === "exito") {
        state.certificado[action.key_servicio] = action.data;
    }
}
