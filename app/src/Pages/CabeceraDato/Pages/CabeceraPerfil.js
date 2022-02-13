import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SLoad, SNavigation, SPage, SText, SView, SHr, STheme, SPopup, SScrollView2 } from 'servisofts-component';
import CabeceraDato from '..';
import SSocket from 'servisofts-socket';
class CabeceraPerfil extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
        this.cabecera = SNavigation.getParam("cabecera");
        this.key_cabecera = SNavigation.getParam("key_cabecera");
    }

    getDatos() {
        var data = CabeceraDato.Actions.getDatoCabecera(this.cabecera, this.key, this.props);
        var datos = CabeceraDato.Actions.getAllDato(this.key, this.props);
        if (!data) return <SLoad />
        if (!datos) return <SLoad />
        return data.map((obj, i) => {
            var item = obj.dato;
            // return <SText>{JSON.stringify(obj,"\n","\t")}</SText>
            return <><SView col={"xs-12"} card style={{
                padding: 8,
            }}>
                <SView col={"xs-12"}>
                    <SView col={"xs-12"} row>
                        <SView flex>
                            <SText fontSize={16} bold>{`${item.descripcion}`}</SText>
                        </SView>
                        <SView width={50} center height>
                            <SText fontSize={20} bold color={"#7FD9DB"}>{`${item.login ? "*" : ""}`}</SText>
                        </SView>
                        <SView width={50} center height>
                            <SText fontSize={20} bold color={"#FF9AA3"}>{`${item.requerido ? "*" : ""}`}</SText>
                            {/* <SText fontSize={10} center>{`${item.requerido ? "Require" : ""}`}</SText> */}
                        </SView>

                    </SView>
                    <SHr />
                    <SText>{`${obj.tipo_dato_cabecera.descripcion} :: ${obj.tipo_dato.descripcion}`}</SText>
                    {/* {this.getTipoDato(item.key_tipo_dato_cabecera)} */}
                </SView>
                <SHr />
                <SText fontSize={10} color={STheme.color.lightGray}>{`${item.key}`}</SText>
            </SView>
                <SHr />
            </>
        })

    }

    getListaDatosFaltantes() {
        var data = CabeceraDato.Actions.getDatoCabecera(this.cabecera, this.key, this.props);
        var datos = CabeceraDato.Actions.getAllDato(this.key, this.props);
        if (!datos) return <SLoad />
        if (!data) return <SLoad />
        return <SView col={"xs-12"} flex backgroundColor={STheme.color.background}>
            <SScrollView2 disableHorizontal>
                <SView col={"xs-12"} center style={{
                    padding: 8,
                }}>
                    <SHr />
                    <SView col={"xs-11"} row style={{
                        padding: 8,
                    }}>
                        <SView flex>
                        </SView>
                        <SView width={50} center height>
                            <SText fontSize={10} color={"#7FD9DB"}>{`Login?`}</SText>
                        </SView>
                        <SView width={50} center height>
                            <SText fontSize={10} color={"#FF9AA3"}>{`Requerido?`}</SText>
                        </SView>
                    </SView>
                    <SHr />
                    {Object.keys(datos).map((key) => {
                        var obj = datos[key];
                        var dataCab = data.find(datoCabecera => datoCabecera.dato.key === key);
                        if (dataCab) return null;
                        return <><SView col={"xs-11"} card style={{
                            padding: 8,
                        }} onPress={() => {
                            SSocket.send({
                                component: "cabeceraDato",
                                type: "registroDatoCabecera",
                                estado: "cargando",
                                cabecera: this.cabecera,
                                data: {
                                    key_dato_cabecera: this.key_cabecera,
                                    key_dato: key
                                },
                                servicio: {
                                    key: this.key
                                },
                            })
                            SPopup.close("NuevoDato");
                        }}>
                            <SView col={"xs-12"}>
                                <SView col={"xs-12"} row>
                                    <SView flex>
                                        <SText fontSize={16} bold>{`${obj.descripcion}`}</SText>
                                    </SView>
                                    <SView width={50} center height>
                                        <SText fontSize={20} bold color={"#7FD9DB"}>{`${obj.login ? "*" : ""}`}</SText>
                                    </SView>
                                    <SView width={50} center height>
                                        <SText fontSize={20} bold color={"#FF9AA3"}>{`${obj.requerido ? "*" : ""}`}</SText>
                                        {/* <SText fontSize={10} center>{`${item.requerido ? "Require" : ""}`}</SText> */}
                                    </SView>

                                </SView>
                                <SHr />
                                {/* <SText>{`${obj.tipo_dato_cabecera.descripcion} :: ${obj.tipo_dato.descripcion}`}</SText> */}
                                {/* {this.getTipoDato(item.key_tipo_dato_cabecera)} */}
                            </SView>
                            <SHr />
                            <SText fontSize={10} color={STheme.color.lightGray}>{`${obj.key}`}</SText>
                        </SView>
                            <SHr />
                        </>
                    })}
                </SView>
            </SScrollView2>
        </SView>
    }
    render() {
        return (
            <SPage title={'CabeceraPerfil'}>
                <SView col={"xs-12"} center>
                    <SView col={"xs-12 sm-10 md-8 lg-6 xl-4"} center>
                        <SText fontSize={18} col={"xs-12"}>{`Datos`}</SText>
                        <SHr />
                        <SView col={"xs-12"} card style={{
                            padding: 8,
                        }} center height={40} onPress={() => {
                            // SNavigation.navigate("registroDato", { key: this.key })
                            SPopup.open({
                                "key": "NuevoDato",
                                content: <SView col={"xs-12 sm-10 md-8 lg-6 xl-4"} card height={800} style={{
                                    maxHeight: "100%",
                                    overflow: 'hidden',
                                }}>
                                    {this.getListaDatosFaltantes()}
                                </SView>
                            })
                        }}>
                            <SText fontSize={12} >{`Nuevo Dato`}</SText>
                        </SView>
                        <SHr />
                        <SView col={"xs-12"} row style={{
                            padding: 8,
                        }}>
                            <SView flex>
                            </SView>
                            <SView width={50} center height>
                                <SText fontSize={10} color={"#7FD9DB"}>{`Login?`}</SText>
                            </SView>
                            <SView width={50} center height>
                                <SText fontSize={10} color={"#FF9AA3"}>{`Requerido?`}</SText>
                            </SView>
                        </SView>
                        <SHr />
                        {this.getDatos()}
                        {/* <SText>{JSON.stringify(this.props.state,"\n","\t")}</SText> */}
                    </SView>
                </SView>

            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(CabeceraPerfil);