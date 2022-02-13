import React, { Component } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { connect } from 'react-redux';
import { SStorage } from 'servisofts-component';
class CerrarSession extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    render() {
        return (
            <TouchableOpacity style={{
                margin: 4,
                width: 100,
                height: 35,
                borderColor: "#ddd",
                borderWidth: 1,
                borderRadius: 8,
                backgroundColor: "#99000099",
                justifyContent: "center",
                alignItems: "center"
            }} onPress={() => {
                SStorage.removeItem("usr_log");
                this.props.state.usuarioReducer.usuarioLog = false;
                this.props.state.usuarioReducer.usuarioDatos = false;
                // this.props.state.fileReducer.data = false;
                this.props.navigation.replace("carga")
            }}>
                <Text style={{
                    fontSize: 10,
                    color: "#fff",
                    fontWeight: "bold",
                    textAlign: "center"
                }}> Cerrar sesión  </Text>
            </TouchableOpacity>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(CerrarSession);