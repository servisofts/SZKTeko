import SSocket from 'servisofts-socket';
import Parent from './index';
import Service from '../../index';

export default class Actions {
    static _getReducer = (props) => {
        return props.state[Parent.component + "Reducer"];
    }
    static getAll = (props) => {
        var reducer = Actions._getReducer(props);
        var data = reducer.data;
        if (!data) {
            if (reducer.estado == "cargando") return null;
            SSocket.send({

                component: Parent.component,
                version: Parent.version,
                type: "getAll",
                estado: "cargando",
                key_usuario: "",
            })
            return null;
        }
        return data;
    }

    static getByKey = (key, props) => {
        var data = Actions.getAll(props);
        if (!data) return null;
        return data[key];
    }

    static registro = (data, props) => {
        SSocket.send({

            component: Parent.component,
            version: Parent.version,
            type: "registro",
            estado: "cargando",
            key_usuario: "",
            data: data
        })
    }
    static conectar = (data, props) => {
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "conectar",
            estado: "cargando",
            key_usuario: "",
            data: data
        })
    }
    static getDataTable = (dispositivo, table = { name, header }) => {
        delete dispositivo["actividad"];
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "getDataTable",
            estado: "cargando",
            key_usuario: "",
            table:table,
            dispositivo: dispositivo
        })
    }
    static deleteDataTable = (dispositivo, table = { name, header }) => {
        delete dispositivo["actividad"];
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "deleteDataTable",
            estado: "cargando",
            key_usuario: "",
            table:table,
            dispositivo: dispositivo
        })
    }
    static registroDataTable = (dispositivo, table = { name, header }) => {
        delete dispositivo["actividad"];
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "registroDataTable",
            estado: "cargando",
            key_usuario: "",
            table:table,
            dispositivo: dispositivo
        })
    }
    static getUsuarios = (dispositivo, props) => {
        delete dispositivo["actividad"];
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "getUsuarios",
            estado: "cargando",
            key_usuario: "",
            dispositivo: dispositivo
        })
    }
    static changeIp = (dispositivo, props) => {
        delete dispositivo["actividad"];
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "changeIp",
            estado: "cargando",
            key_usuario: "",
            dispositivo: dispositivo,
        })
    }
    static open = (dispositivo, parameters = { operID, doorOrAuxoutID, outputAddrType, doorAction }, props) => {
        delete dispositivo["actividad"];
        SSocket.send({
            component: Parent.component,
            version: Parent.version,
            type: "open",
            estado: "cargando",
            key_usuario: "",
            dispositivo: dispositivo,
            parameters: parameters
        })
    }
    static copiar = (data, props) => {
        SSocket.send({

            component: Parent.component,
            version: Parent.version,
            type: "copiar",
            estado: "cargando",
            key_usuario: "",
            data: data
        })
    }
    static editar = (data, props) => {
        SSocket.send({

            component: Parent.component,
            version: Parent.version,
            type: "editar",
            estado: "cargando",
            key_usuario: "",
            data: data
        })
    }
    static eliminar = (data, props) => {
        SSocket.send({

            component: Parent.component,
            version: Parent.version,
            type: "editar",
            estado: "cargando",
            key_usuario: "",
            data: {
                ...data,
                estado: 0,
            }
        })
    }

    static getError = (type, props) => {
        if (props.state.usuarioReducer.estado == "error" && props.state.usuarioReducer.type == type) {
            props.state.usuarioReducer.estado = "";
            return props.state.usuarioReducer.error;
        }
        return null
    }

}