import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SDate, SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, SView } from 'servisofts-component';
import Parent from ".."
class ListaUsuarios extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key_dispositivo = SNavigation.getParam("key_dispositivo");
    }

    getListaUsuarios() {
        var data = Parent.Actions._getReducer(this.props)
        data = data.data_usuarios[this.key_dispositivo];
        if (!data) return <SLoad />
        return <STable2
            header={[
                { key: "index", label: "#", width: 50 },
                { key: "Pin", label: "Pin", width: 50 },
                { key: "CardNo", label: "CardNo", width: 50 },
                { key: "tPassword", label: "tPassword", width: 50 },
                { key: "tGroup", label: "tGroup", width: 50 },
                { key: "tStartTime", label: "tStartTime", width: 50 },
                { key: "tEndTime", label: "tEndTime", width: 50 },

            ]}
            data={data}
        />
    }

    render() {
        if (!this.key_dispositivo) {
            SNavigation.goBack();
            return <SLoad />
        }
        return (
            <SPage title={'ListaUsuarios de ' + Parent.component} disableScroll>
                {this.getListaUsuarios()}
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(ListaUsuarios);