import SSocket from "servisofts-socket";

export default class Actions {
    static getAll(key, props) {
        var reducer = props.state.cabeceraDatoReducer;
        var data = reducer.data[key];
        if (!data) {
            if (reducer.estado == "cargando") return;
            var object = {
                component: "cabeceraDato",
                type: "getAll",
                estado: "cargando",
                servicio: {
                    key: key
                },
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
    static registro(data, key_servicio, props) {
        var reducer = props.state.cabeceraDatoReducer;
        var object = {
            component: "cabeceraDato",
            type: "registro",
            estado: "cargando",
            data: data,
            servicio: {
                key: key_servicio
            },
        }
        SSocket.send(object);
    }

    static eliminar(data, key_servicio, props) {
        var reducer = props.state.cabeceraDatoReducer;
        var object = {
            component: "cabeceraDato",
            type: "eliminar",
            estado: "cargando",
            data: data,
            servicio: {
                key: key_servicio
            },
        }
        SSocket.send(object);
    }

    static registroDato(data, key_servicio, props) {
        var reducer = props.state.cabeceraDatoReducer;
        var object = {
            component: "cabeceraDato",
            type: "registroDato",
            estado: "cargando",
            data: data,
            servicio: {
                key: key_servicio
            },
        }
        SSocket.send(object);
    }
    static getAllDato(key, props) {
        var reducer = props.state.cabeceraDatoReducer;
        var data = reducer.dato[key];
        if (!data) {
            if (reducer.estado == "cargando") return;
            var object = {
                component: "cabeceraDato",
                type: "getAllDato",
                estado: "cargando",
                servicio: {
                    key: key
                },
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
    static getAllTipoDato(props) {
        var reducer = props.state.cabeceraDatoReducer;
        var data = reducer.tipoDato;
        if (!data) {
            if (reducer.estado == "cargando") return;
            var object = {
                component: "cabeceraDato",
                type: "getAllTipoDato",
                estado: "cargando",
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
    static getAllTipoDatoCabecera(props) {
        var reducer = props.state.cabeceraDatoReducer;
        var data = reducer.tipoDatoCabecera;
        if (!data) {
            if (reducer.estado == "cargando") return;
            var object = {
                component: "cabeceraDato",
                type: "getAllTipoDatoCabecera",
                estado: "cargando"
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
    static getAllTipoDatoCabecera(props) {
        var reducer = props.state.cabeceraDatoReducer;
        var data = reducer.tipoDatoCabecera;
        if (!data) {
            if (reducer.estado == "cargando") return;
            var object = {
                component: "cabeceraDato",
                type: "getAllTipoDatoCabecera",
                estado: "cargando"
            }
            SSocket.send(object);
            return;
        }
        return data;
    }

    static getDatoCabecera(cabecera, key, props) {
        var reducer = props.state.cabeceraDatoReducer;
        var data = reducer.dato_cabecera[key];
        if (data) {
            data = reducer.dato_cabecera[key][cabecera];
        }
        if (!data) {
            if (reducer.estado == "cargando") return;
            var object = {
                component: "cabeceraDato",
                type: "getDatoCabecera",
                estado: "cargando",
                cabecera: cabecera,
                servicio: {
                    key: key
                },
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
}