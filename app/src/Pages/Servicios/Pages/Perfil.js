import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SPage, SText, SNavigation, SLoad, SView, SHr, STheme, SIcon, SButtom } from 'servisofts-component';
import Parent from '..';
import BotonesPaginas from '../../../Components/BotonesPaginas';

class Perfil extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
        this.nombre = SNavigation.getParam("nombre");
    }
    getPerfil() {
        let data;
        if (this.nombre) {
            data = Parent.Actions.getByName(this.nombre, this.props);

        } else {
            data = Parent.Actions.getByKey(this.key, this.props);
        }
        if (!data) return <SLoad />
        this.key = data.key;
        // <SText>{data.nombre}</SText>
        return <SView col={"xs-12"} center card>
            <SHr height={32} />
            <SText fontSize={22} font={"Roboto-Light"} >{`servisofts.`}<SText fontSize={32} bold style={{
                textTransform: "uppercase"
            }} font={"Roboto-Bold"}>{`${data.nombre}`}</SText></SText>
            <SHr />

            <SText fontSize={10} color={STheme.color.lightGray} >{`${data.puerto}\t|\t${data.puerto_ws}\t|\t${data.puerto_http}`}</SText>
            <SHr />

            <SText fontSize={10} color={STheme.color.lightGray} >{`${data.ip || ""}`}</SText>
            <SText fontSize={10} color={STheme.color.lightGray} >{`${data.ip_public || ""}`}</SText>

            <SHr />
            <SText fontSize={10} color={STheme.color.lightGray} >{`${data.key}`}</SText>
            <SHr height={32} />
        </SView>
    }
    btnDelete(callback) {
        return <SView width={30} height={30} style={{
            position: "absolute",
            top: -2,
            right: 8,
        }} >
            <SButtom props={{
                type: "danger",
                variant: "confirm"
            }} style={{
                width: 30,
                height: 30,
                padding: 0,
            }} onPress={callback}>
                <SView col={"xs-12"} center flex height>
                    <SIcon name={"Delete"} />
                </SView>
            </SButtom>
        </SView>
    }


    render() {
        return (
            <SPage title={'Perfil'}>
                <SView col={"xs-12"} center>
                    <SView col={"xs-12 sm-10 md-8 lg-6 xl-4"} center>
                        <SHr height={32} />
                        {this.getPerfil()}
                        <SHr height={32} />

                    </SView>
                    <BotonesPaginas data={[
                        { label: "Puntos de venta", icon: "Servisofts", url: "punto_venta", params: { key_servicio: this.key } },
                        // { label: "Tipos de cargos", icon: "Ajustes", url: "tipo_cargo", params: { key_servicio: this.key } },
                        // { label: "Tipos de datos", url: "tipoDato", icon: "Lock" },
                    ]} />
                </SView>
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Perfil);