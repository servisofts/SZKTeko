import React, { Component } from 'react';
import { connect } from 'react-redux';

import { SHr, SIcon, SImage, SLoad, SNavigation, SPage, SPopup, SStorage, SText, SView } from 'servisofts-component'
import SSocket from 'servisofts-socket';
import BarraSuperior from '../../Components/BarraSuperior';
import BotonesPaginas from '../../Components/BotonesPaginas';
import { SSRolesPermisosGetPages, SSRolesPermisosValidate } from '../../SSRolesPermisos';
import Usuario from '../Usuario';
// import Usuario from '../Usuario';
// import UsuarioSession from '../Usuario';
class InicioPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }
    getPaginas() {
        var pages = SSRolesPermisosGetPages();
        if (!pages) {
            return <SLoad />
        }
        return Object.keys(pages).map((key) => {
            var obj = pages[key];
            // console.log(obj)
            if (!obj.is_page) {
                return null;
            }
            if (!SSRolesPermisosValidate({ page: obj.url, permiso: "ver" })) {
                return null;
            }
            var urlImage = SSocket.api.rp + "page/" + obj.key;
            return <SView col={"xs-3 sm-2.5 md-2 lg-1.5 xl-1.3"} colSquare style={{
                padding: 4,
            }}>
                <SView flex onPress={() => {
                    SNavigation.navigate(obj.url)
                }}>
                    <SView center>
                        <SView col={"xs-7"} colSquare>
                            <SImage src={urlImage} style={{
                                width: "100%",
                                height: "100%",
                            }} />
                        </SView>
                    </SView>
                    <SHr />
                    <SView center>
                        <SText center fontSize={12}>{obj.url}</SText>
                    </SView>
                </SView>
            </SView>

        });
    }
    render() {
        if (!Usuario.Actions.getUsuarioLogueado(this.props)) {
            SNavigation.replace("carga");
            return null;
        }
        return (
            <SPage
                // title="Inicio"
                hidden
                preventBack
            >
                <BarraSuperior title={"Catálogo de Ropa"} />
                <SView
                    col={"xs-12"} center
                >
                    <SView col={"xs-11"} row>
                        {this.getPaginas()}
                    </SView>

                    {/* <BotonesPaginas
                        history={this.props.history}
                        data={[
                            // {
                            //     label: "Salir", icon: <SIcon name={"Salir"} />, onPress: () => {
                            //         
                            //     }
                            // },
                            // { url: "tarifa", label: "Tarifas", icon: <SIcon name={"Tarifas"} /> },
                            // { url: "parametros", label: "Parametros", icon: <SIcon name={"Parametros"} /> },
                            // { url: "palabra_restringida", label: "Palabra restringida", icon: <SIcon name={"Parametros"} /> },
                            // { url: "motivo_cancelacion", label: "motivo cancelacion", icon: <SIcon name={"Parametros"} /> },
                        ]}
                    /> */}
                </SView>

            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(InicioPage);