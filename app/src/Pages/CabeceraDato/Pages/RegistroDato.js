import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SLoad, SNavigation, SPage, SText, SView } from 'servisofts-component';
import CabeceraDato from '..';

class RegistroDato extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
    }
    getTypes() {
        var tipoDato = CabeceraDato.Actions.getAllTipoDato(this.props);
        var tipoDatoCabecera = CabeceraDato.Actions.getAllTipoDatoCabecera(this.props);
        if (!tipoDato) return null
        if (!tipoDatoCabecera) return null
        var list = [];
        list.push({ key: false, content: <SText>{`No seleccionado`}</SText> })
        Object.keys(tipoDatoCabecera).map((key) => {
            var tipo = tipoDato[tipoDatoCabecera[key].key_tipo_dato];
            list.push({ key: key, content: <SText>{`${tipoDatoCabecera[key].descripcion} / ${tipo.descripcion}`}</SText> })
        })
        return list;
    }
    render() {
        var types = this.getTypes();
        if (!types) return <SLoad />
        return (
            <SPage title={'RegistroDato'}>
                <SView col={"xs-12"} center>
                    <SForm
                        col={"xs-12 sm-10 md-8 lg-6 xl-4"}
                        inputProps={{
                            customStyle: "calistenia"
                        }}
                        inputs={{
                            descripcion: { label: "nombre", type: "text", defaultValue: "", isRequired: true },
                            key_tipo_dato_cabecera: { label: "Type", type: "select", isRequired: true, options: types },
                            requerido: { label: "Requerido?", type: "select", defaultValue: false, options: [{ key: false, content: "NO" }, { key: true, content: "SI" }] },
                            login: { label: "Para login?", type: "select", defaultValue: false, options: [{ key: false, content: "NO" }, { key: true, content: "SI" }] },
                        }}
                        onSubmitName={"Registrar"}
                        onSubmit={(values) => {
                            CabeceraDato.Actions.registroDato(values, this.key, this.props)
                            SNavigation.goBack();
                        }}
                    />

                </SView>

            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(RegistroDato);