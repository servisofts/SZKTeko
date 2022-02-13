import { SNavigation, SStorage } from "servisofts-component";
import SSocket from "servisofts-socket";

export default class Actions {
    static getError(type, props) {
        if (props.state.usuarioReducer.estado == "error" && props.state.usuarioReducer.type == type) {
            props.state.usuarioReducer.estado = "";
            return props.state.usuarioReducer.error;
        }
        return null
    }
    static getUsuarioLogueado(props) {
        return props.state.usuarioReducer.usuarioLog;
    }
    static login(data) {
        var object = {
            component: "usuario",
            version: "2.0",
            type: "login",
            estado: "cargando",
            data: data,
        }
        // alert(JSON.stringify(object));
        SSocket.send(object);
    }

    static logout(props) {
        SStorage.removeItem("usr_log");
        props.state.usuarioReducer.usuarioLog = false;
        props.state.usuarioReducer.usuarioDatos = false;
        SNavigation.replace("carga");
    }

    static registro_cliente(data, props) {
        data["Fecha nacimiento"] = "1900-01-01"
        Actions.registro(data, "d16d800e-5b8d-48ae-8fcb-99392abdf61f", props);
    }
    static registro(data, key_rol, props) {
        var object = {
            component: "usuario",
            type: "registro",
            version: "2.0",
            estado: "cargando",
            cabecera: "registro_administrador",
            key_usuario: !props.state.usuarioReducer.usuarioLog ? "" : props.state.usuarioReducer.usuarioLog.key,
            key_rol: key_rol,
            data: data,
        }
        SSocket.send(object);
    }
    static editar(data, props) {
        var object = {
            component: "usuario",
            type: "editar",
            version: "2.0",
            estado: "cargando",
            cabecera: "registro_administrador",
            key_usuario: props.state.usuarioReducer.usuarioLog.key,
            data: data,
        }
        SSocket.send(object);
    }
    static getAll(props) {
        var cabecera = "registro_administrador";
        var data = props.state.usuarioReducer.data[cabecera];
        if (!data) {
            if (props.state.usuarioReducer.estado == "cargando") {
                return;
            }
            var object = {
                component: "usuario",
                version: "2.0",
                type: "getAll",
                estado: "cargando",
                cabecera: "registro_administrador",
                key_usuario: props.state.usuarioReducer.usuarioLog.key,
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
    static getByKey(key, props) {
        var cabecera = "registro_administrador";
        var data = props.state.usuarioReducer.data[cabecera];
        if (!data) {
            if (props.state.usuarioReducer.estado == "cargando") {
                return;
            }
            var object = {
                component: "usuario",
                version: "2.0",
                type: "getAll",
                estado: "cargando",
                cabecera: "registro_administrador",
                key_usuario: props.state.usuarioReducer.usuarioLog.key,
            }
            SSocket.send(object);
            return;
        }
        var obj = data[key]
        return obj;
    }
    static getAllClientesActivos(props) {
        var cabecera = "registro_administrador";
        var data = props.state.clientesActivosReducer.data;
        if (!data) {
            if (props.state.clientesActivosReducer.estado == "cargando") {
                return;
            }
            var object = {
                component: "clientesActivos",
                type: "getAll",
                estado: "cargando",
                key_usuario: props.state.usuarioReducer.usuarioLog.key,
            }
            SSocket.send(object);
            return;
        }
        return data;
    }
}