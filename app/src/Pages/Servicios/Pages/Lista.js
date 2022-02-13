import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Linking } from 'react-native'
import { SDate, SHr, SIcon, SLoad, SNavigation, SPage, STable2, SText, SView } from 'servisofts-component';
import Servicios from '..';
import FloatButtom from '../../../Components/FloatButtom';

class Lista extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    getLista() {
        var data = Servicios.Actions.getAll(this.props);
        if (!data) return <SLoad />
        return <STable2
            header={[
                { key: "index", label: "#", width: 50, },

                { key: "nombre", label: "nombre", width: 150, },
                { key: "descripcion", label: "descripcion", width: 200, },
                {
                    key: "key-detalle", label: "Ip", width: 150, center: true, render: (item) => {
                        var obj = data[item];
                        return `${obj.ip_public || ""} \n${obj.ip || ""}`
                    }
                },
                {
                    key: "key-detallePuerto", label: "Puerto", width: 150, center: true, render: (item) => {
                        var obj = data[item];
                        return `${obj.puerto} | ${obj.puerto_ws} | ${obj.puerto_http} `
                    }
                },
                // { key: "puerto_ws", label: "Puerto WS", width: 100, center: true },
                // { key: "puerto_http", label: "Puerto HTTP", width: 100, center: true },
                // { key: "ip", label: "IP Local", width: 100 },
                // { key: "ip_public", label: "IP Public", width: 100 },
                { key: "fecha_last", label: "Ultima Conexion", width: 160, center: true, render: (val) => new SDate(val).toString("yyyy/MM/dd hh:mm") },
                {
                    key: "session", label: "Conectado", width: 80, center: true, component: (item) => {
                        return <SView width={25} height={25} backgroundColor={!item ? "#600" : "#060"} style={{
                            borderRadius: 8,
                        }}>
                        </SView>
                    }
                },
                {
                    key: "key-ver", label: "ver", width: 50, center: true, component: (item) => {
                        return <SView width={35} height={35} onPress={() => {
                            SNavigation.navigate("servicios/perfil", { key: item })
                        }}>
                            <SIcon name={'Ajustes'} />
                        </SView>
                    }
                },
            ]}
            data={data}

            limit={50}
        />
    }
    openUrl = async (url) => {
        // Checking if the link is supported for links with custom URL scheme.
        const supported = await Linking.canOpenURL(url);

        if (supported) {
            // Opening the link with some app, if the URL scheme is "http" the web link should be opened
            // by some browser in the mobile
            await Linking.openURL(url);
        } else {
            Alert.alert(`Don't know how to open this URL: ${url}`);
        }
    }

    render() {
        return (
            <SPage title={'Servicios habilitados para ZKTeco'} disableScroll>
                <SView col={"xs-12"} center height>
                    {this.getLista()}
                </SView>
                {/* <FloatButtom label={"+"} style={{ bottom: 40 }} onPress={() => {
                    SNavigation.navigate("servicios/registro")
                }} /> */}
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Lista);