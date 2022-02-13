
import React, { Component } from 'react';
import { View, Text, TouchableOpacity, ViewStyle } from 'react-native';
import { connect } from 'react-redux';
import { SImage } from 'servisofts-component';
import SSocket from 'servisofts-socket';
import * as SImageImput from '../SImageImput';
type tprop = {
    data: Object,
    component: String,
    style: ViewStyle
}
class FotoPerfilComponent extends Component<tprop> {
    constructor(props) {
        super(props);
        this.state = {
            repaint: new Date().getTime()
        };
    }
    render() {
        var data = this.props.data;
        if (!data) {
            return <View />
        }
        if (!data.key) {
            return <View />
        }
        return (<TouchableOpacity style={{
            width: "100%",
            height: "100%",
            backgroundColor: "#ff999933",
            borderRadius: 8,
            overflow: "hidden",
            ...this.props.style
        }} onPress={() => {
            SImageImput.choseFile({
                servicio: "root",
                component: this.props.component,
                type: "subirFoto",
                estado: "cargando",
                key: data.key,
                key_usuario: this.props.state.usuarioReducer.usuarioLog.key,
            }, (resp) => {
                // this.props.dispatch({
                //     component: "image",
                //     type: "cambio",
                //     url: SSocket.api.root + this.props.component + "_" + data.key,
                // })
                this.state.repaint = new Date().getTime()
                this.setState({ ...this.state });
            });
        }}>
            {/* {"foto"} */}
            <SImage src={SSocket.api.root + this.props.component + "_" + data.key + "?date=" + this.state.repaint} />

        </TouchableOpacity>
        )
    }
}

const initStates = (state) => {
    return { state }
};
export default connect(initStates)(FotoPerfilComponent);