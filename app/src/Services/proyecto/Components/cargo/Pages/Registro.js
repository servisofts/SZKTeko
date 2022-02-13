import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SHr, SIcon, SNavigation, SPage, SText, SView, SLoad } from 'servisofts-component';
import Parent from '../index';
import etapa from "../../etapa"
import proyecto from "../../proyecto"
import SSocket from 'servisofts-socket';

class Registro extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
        this.key_etapa = SNavigation.getParam("key_etapa");
        this.key_tipo_cargo = SNavigation.getParam("key_tipo_cargo");
    }

    getContent() {
        this.data = {};
        if (this.key) {
            this.data = Parent.Actions.getByKey(this.key, this.props);
            if (!this.data) return <SLoad />
            this.key_tipo_cargo = this.data.key_tipo_cargo;
        } else {
            this.data = {};
        }
        if (this.key_etapa) {
            var data_etapa = etapa.Actions.getByKey(this.key_etapa, this.props);
            if (!data_etapa) return <SLoad />
            var data_proyecto = proyecto.Actions.getByKey(data_etapa.key_proyecto, this.props);
            if (!data_proyecto) return <SLoad />
            this.data_etapa = data_etapa;
        }
        if (!this.key_tipo_cargo) {
            if (!this.state.tipo_cargo) {
                SNavigation.navigate("tipo_cargo/select", {
                    key_servicio: data_proyecto.key_servicio,
                    onSelect: (obj) => {
                        this.setState({
                            tipo_cargo: obj
                        })
                    }
                })
            } else {
                this.key_tipo_cargo = this.state.tipo_cargo.key;
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
                descripcion: { label: "Descripcion", isRequired: true, defaultValue: this.data["descripcion"] },
                observacion: { label: "Observacion", isRequired: true, defaultValue: this.data["observacion"] },
                monto: { label: "Monto", isRequired: true, defaultValue: parseFloat(this.data["monto"] ?? 0).toFixed(2), type: "money", icon: <SText>{this.data_etapa.key_moneda}</SText> },
                // url: { label: "url", isRequired: true, defaultValue: this.data["url"] },
            }}
            onSubmitName={"Guardar"}
            onSubmit={(values) => {
                values.key_tipo_cargo = this.key_tipo_cargo
                if (this.key) {
                    Parent.Actions.editar({ ...this.data, ...values }, this.props);
                } else {
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