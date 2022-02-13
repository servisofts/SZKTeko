import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SHr, SIcon, SNavigation, SPage, SText, SView, SLoad } from 'servisofts-component';
import Parent from '../index';
import SSocket from 'servisofts-socket';

class Registro extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
        this.key_proyecto = SNavigation.getParam("key_proyecto");
    }

    getContent() {
        this.data = {};
        if (this.key) {
            this.data = Parent.Actions.getByKey(this.key, this.props);
            if (!this.data) return <SLoad />
        } else {
            this.data = {
                key_moneda: "Bs."
            };
        }

        return <SForm
            center
            row
            ref={(form) => { this.form = form; }}
            col={"xs-11 sm-9 md-7 lg-5 xl-4"}

            inputProps={{
                customStyle: "calistenia"
            }}
            inputs={{
                descripcion: { label: "Descripcion", isRequired: true, defaultValue: this.data["descripcion"] },
                observacion: { label: "Observacion", isRequired: true, defaultValue: this.data["observacion"] },
                key_moneda: { label: "Moneda", isRequired: true, defaultValue: this.data["key_moneda"], type: "select", options: ["Bs.", "$us", "€"], col: "xs-4" },
                monto: { label: "Monto", isRequired: true, defaultValue: parseFloat(this.data["monto"] ?? 0).toFixed(2), type: "money", col: "xs-8", icon: " " },

                // url: { label: "url", isRequired: true, defaultValue: this.data["url"] },
            }}
            onSubmitName={"Guardar"}
            onSubmit={(values) => {
                if (this.key) {
                    Parent.Actions.editar({ ...this.data, ...values }, this.props);
                } else {
                    values.key_proyecto = this.key_proyecto;
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