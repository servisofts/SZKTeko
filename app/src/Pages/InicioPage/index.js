import React, { Component } from 'react';
import View from 'react-native';
import { connect } from 'react-redux';
import { SButtom, SHr, SImage, SLoad, SNavigation, SPage, SText, SView } from 'servisofts-component';
import SSocket from 'servisofts-socket';
import BarraSuperior from '../../Components/BarraSuperior';
import BotonesPaginas from '../../Components/BotonesPaginas';
import NavBar from '../../Components/NavBar';
import Usuario from '../Usuario';

// import Usuario from '../Usuario';
// import UsuarioSession from '../Usuario';
class InicioPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }
       render() {
        // if (!Usuario.Actions.getUsuarioLogueado(this.props)) {
        //     SNavigation.replace("carga");
        //     return null;
        // }
        var source = require("./Images/catalogo.png");
        var camisa = require("./Images/camisa.jpg");
        return (
            <SPage
                title="Servisofts - ZKTeco"
            >
                <SView col={"xs-12"} row center >
                        {/* {this.getPaginas()} */}
                </SView>

                <BotonesPaginas data={[
                    { label: "Servicios", url: "servicios", icon: "Servisofts" },
                    { label: "Tipos de dispositivos", url: "tipo_dispositivo", icon: "Lock" },
                ]} />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(InicioPage);