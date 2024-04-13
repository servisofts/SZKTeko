import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SNavigation, SPage, SText, SView } from 'servisofts-component';
import CabeceraDato from '..';

class RegistroCabeceraDato extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key=SNavigation.getParam("key");
    }

    render() {
        return (
            <SPage title={'RegistroCabeceraDato'}>
                <SView col={"xs-12"} center>
                    <SForm
                        col={"xs-12 sm-10 md-8 lg-6 xl-4"}
                        inputProps={{
                            customStyle: "calistenia"
                        }}
                        inputs={{
                            nombre: { label: "nombre", type: "text", defaultValue: "", isRequired: true },
                        }}
                        onSubmitName={"Registrar"}
                        onSubmit={(values) => {
                           CabeceraDato.Actions.registro(values,this.key, this.props)
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
export default connect(initStates)(RegistroCabeceraDato);