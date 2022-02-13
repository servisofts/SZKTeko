import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SHr, SIcon, SNavigation, SPage, SText, SView, SLoad } from 'servisofts-component';
import Parent from '../index';
import etapa from "../../etapa"
import SSocket from 'servisofts-socket';

class Registro extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
        this.key_etapa = SNavigation.getParam("key_etapa");
        this.key_tipo_seguimiento = SNavigation.getParam("key_tipo_seguimiento");
    }

    getContent() {
        this.data = {};
        if (this.key) {
            this.data = Parent.Actions.getByKey(this.key, this.props);
            if (!this.data) return <SLoad />
            this.key_tipo_seguimiento = this.data.key_tipo_seguimiento;
        } else {
            this.data = {};
        }
        if (this.key_etapa) {
            var data_etapa = etapa.Actions.getByKey(this.key_etapa, this.props);
            if (!data_etapa) return <SLoad />
            this.data_etapa = data_etapa;
        }
        if (!this.key_tipo_seguimiento) {
            if (!this.state.tipo_seguimiento) {
                SNavigation.navigate("tipo_seguimiento/select", {
                    onSelect: (obj) => {
                        this.setState({
                            tipo_seguimiento: obj
                        })
                    }
                })
            } else {
                this.key_tipo_seguimiento = this.state.tipo_seguimiento.key;
            }

        }
        return <SForm
            center
            ref={(form) => { this.form = form; }}
            col={"xs-11 sm-9 md-7 lg-5 xl-4"}
            inputProps={{
                customStyle: "calistenia"
            }}
            inputs={{
                foto_p: { type: "file", isRequired: false, col: "xs-4 sm-3.5 md-3 lg-2.5 xl-2.5" },
                descripcion: { label: "Descripcion", isRequired: true, defaultValue: this.data["descripcion"] },
                observacion: { label: "Observacion", isRequired: true, defaultValue: this.data["observacion"] },
                monto: { label: "Monto", isRequired: false, defaultValue: parseFloat(this.data["monto"] ?? 0).toFixed(2), type: "money", icon: <SText>{this.data_etapa.key_moneda}</SText> },
                fecha_expiracion: { label: "Fecha de Expiracion", isRequired: false, defaultValue: this.data["fecha_expiracion"], type: "date" },
                // url: { label: "url", isRequired: true, defaultValue: this.data["url"] },
            }}
            onSubmitName={"Guardar"}
            onSubmit={(values) => {
                values.key_tipo_seguimiento = this.key_tipo_seguimiento
                if (this.key) {
                    Parent.Actions.editar({ ...this.data, ...values }, this.props);
                } else {
                    if (values.key_tipo_seguimiento == "e3e835e2-a1f1-47ce-9af0-927b83ba205f") {
                        values.monto = values.monto * -1
                    }
                    values.key_etapa = this.key_etapa;
                    Parent.Actions.registro(values, this.props);
                }
            }}
        />
    }

    render() {
        var reducer = this.props.state[Parent.component + "Reducer"];
        if (reducer.type == "registro" || reducer.type == "editar") {
            if (reducer.estado == "exito") {
                if (reducer.type == "registro") this.key = reducer.lastRegister?.key;
                if (this.form) {
                    this.form.uploadFiles(SSocket.api.root + "upload/" + Parent.component + "/" + this.key);
                }

                reducer.estado = "";
                SNavigation.goBack();
            }
        }

        return (
            <SPage title={'Registro de ' + Parent.component} center>
                <SView height={30}></SView>
                {this.getContent()}
                <SHr />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Registro);