import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SLoad, SPage, STable2, SText } from 'servisofts-component';
import CabeceraDato from '..';

class TipoDato extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }
    getDatosCabecera() {
        var datos = CabeceraDato.Actions.getAllTipoDatoCabecera(this.props);
        var tipoDato = CabeceraDato.Actions.getAllTipoDato(this.props);
        if (!datos) return <SLoad />
        if (!tipoDato) return <SLoad />
        return <STable2
            header={[
                { key: "index", label: "#", width: 50, },
                { key: "descripcion", label: "descripcion", width: 150, },
                { key: "key_tipo_dato", label: "key_tipo_dato", width: 150, render: (item) => { return tipoDato[item].descripcion } },
            ]}
            data={datos}

            limit={50}
        />
    }
    render() {
        return (
            <SPage title={'TipoDato'} disableScroll>
                {this.getDatosCabecera()}
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(TipoDato);