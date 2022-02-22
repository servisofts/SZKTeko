import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SButtom, SDate, SForm, SHr, SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, SView } from 'servisofts-component';
import Parent from ".."
import FloatButtom from '../../../../../Components/FloatButtom';
import Struct from '../Struct';
import RegistroDataTable from './RegistroDataTable';
class DataTable extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key_dispositivo = SNavigation.getParam("key_dispositivo");
    }

    getForm() {
        this.dispositivo = Parent.Actions.getByKey(this.key_dispositivo, this.props);
        if (!this.dispositivo) return <SLoad />

        return Object.keys(Struct).map((key) => {
            return <>
                <SView width={8} />
                <SButtom type='outline' onPress={() => {

                    var option = { ...Struct[key] };
                    option.header = option.header.toString(); // Remplazo las , por \t por que asi nesecita PUL ZKTECO
                    console.log(option);
                    option.header = option.header.replace(/,/g, "\t");
                    console.log(option);
                    Parent.Actions.getDataTable(this.dispositivo, option);
                    this.setState({
                        select: key
                    })
                }}>{key}</SButtom>
                <SView width={8} />
                {/* <SButtom type='danger' variant='confirm' onPress={() => {
                    var option = { ...Struct[key] };
                    //option.header = option.header.toString(); // Remplazo las , por \t por que asi nesecita PUL ZKTECO
                    option.header = "";
                    console.log(option);
                    Parent.Actions.deleteDataTable(this.dispositivo, option);
                }}>Delete {key}</SButtom> */}
                {/* <SButtom type='danger' onPress={() => {
                     var option = { ...Struct[key] };
                     option.header = option.header.toString(); // Remplazo las , por \t por que asi nesecita PUL ZKTECO
                     console.log(option);
                     option.header = "CardNo=6033885\tPin=1\tName=juan\tPassword=\tGroup=0\tStartTime=0\tEndTime=0"
                     console.log(option);
                    Parent.Actions.registroDataTable(this.dispositivo,option)
                }}>registrar {key}</SButtom> */}

            </>
        });

    }
    getTable() {
        var dataTable = this.props.state.dispositivoReducer.dataTable;
        var table = this.props.state.dispositivoReducer.table;
        if (!dataTable) return null;
        if (!table) return null;
        var headers = table.header.split(/\t/g,).map((key) => {
            var options = {};
            if (key == "Time_second") {
                options.render = (itm) => {
                    return new SDate(new Date(itm * 1000)).toString();
                }
                options.order = "desc";
            }
            return { key: key, label: key, width: 150, ...options }
        });
        return <STable2
            header={[
                { key: "index", label: "#", width: 50 },
                ...headers
            ]}
            data={this.props.state.dispositivoReducer.dataTable}

        />
    }

    render() {
        if (!this.key_dispositivo) {
            SNavigation.goBack();
            return <SLoad />
        }
        return (
            <SPage title={'DataTable de ' + Parent.component} disableScroll>
                <SView row col={"xs-12"}>
                    {this.getForm()}

                </SView>
                <SView col={"xs-12"} flex>
                    {this.getTable()}
                </SView>
                {!this.state.select ? null :
                    <FloatButtom onPress={() => {
                        SPopup.open({
                            key: "registroDataTable",
                            content: <RegistroDataTable key_table={this.state.select} dispositivo={this.dispositivo}/>
                        })
                    }} />}
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(DataTable);