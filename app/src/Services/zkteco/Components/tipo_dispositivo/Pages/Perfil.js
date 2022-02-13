import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SHr, SLoad, SNavigation, SPage, SText, SView } from 'servisofts-component';
import Parent from ".."
class Perfil extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key = SNavigation.getParam("key");
    }

    getSceneData() {
        var data = Parent.Actions.getByKey(this.key, this.props);
        if (!data) return <SLoad />
        return <SText>{JSON.stringify(data)}</SText>
    }

   
    render() {
        return (
            <SPage title={'Perfil'} disableScroll center>
                {this.getSceneData()}
                <SHr />
                <SHr />
                <SHr />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Perfil);