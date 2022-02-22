
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SForm, SHr, SIcon, SNavigation, SPage, SText, SView, SLoad, STheme } from 'servisofts-component';
import SSocket from 'servisofts-socket';
import Struct from '../Struct';
import Parent from ".."

class RegistroDataTable extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    getInput() {
        var inpt = {}
        Struct[this.props.key_table].header.map((obj) => {
            inpt[obj] = { type: "text", label: obj }
        })
        return inpt
    }
    getContent() {
        return <SForm
            center
            ref={(form) => { this.form = form; }}
            col={"xs-11 sm-9 md-7 lg-5 xl-4"}
            inputProps={{
                customStyle: "calistenia"
            }}
            inputs={{
                ...this.getInput()
            }}
            onSubmitName={"Guardar"}
            onSubmit={(values) => {
                var to_insert = "";
                var options = {...Struct[this.props.key_table]};
                Struct[this.props.key_table].header.map((obj) => {
                    if (to_insert.length > 0) {
                        to_insert+="\t";
                    }
                    to_insert += `${obj}=${values[obj] ?? ""}`
                })
                options.header = to_insert;

                Parent.Actions.registroDataTable(this.props.dispositivo,options)
            }}
        />
    }

    render() {
        return (
            <SView col={"xs-11"} backgroundColor={STheme.color.background} withoutFeedback height={1000} style={{
                maxHeight: "90%"
            }}>
                <SView height={30}>{ }</SView>
                <SView col={"xs-12"} center>
                    <SText>{JSON.stringify()}</SText>
                    {this.getContent()}
                </SView>
                <SHr />
            </SView>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(RegistroDataTable);