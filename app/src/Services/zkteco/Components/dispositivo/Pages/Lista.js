import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SDate, SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, SView } from 'servisofts-component';
import Parent from ".."
import FloatButtom from '../../../../../Components/FloatButtom';
import punto_venta from '../../punto_venta';
class Lista extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key_punto_venta = SNavigation.getParam("key_punto_venta");
    }

    getLista() {
        var data = Parent.Actions.getAll(this.props);
        if (!data) return <SLoad />
        return <STable2
            header={[
                { key: "index", label: "#", width: 50 },
                { key: "descripcion", label: "Descripcion", width: 150 },
                { key: "observacion", label: "observacion", width: 100 },
                { key: "ip", label: "ip", width: 120 },
                { key: "puerto", label: "puerto", width: 70 },
                { key: "mac", label: "mac", width: 150 },
                {
                    key: "actividad-estado", label: "Actividad", center: true, width: 70, component: (item) => {
                        return <SView style={{
                            width: 30,
                            height: 30,
                            borderRadius: 4,
                            backgroundColor: item.isConected ? "#0f0" : "#f00"
                        }}>

                        </SView>
                    }
                },
                {
                    key: "actividad-hora", label: "Last Activity", width: 150, center: true, render: (item) => {
                        return new SDate(item.fecha_on).toString("yyyy-MM-dd hh:mm:ss")
                    }
                },
                {
                    key: "key-editar", label: "Editar", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => { SNavigation.navigate(Parent.component + "/registro", { key: item }) }}>
                            <SIcon name={"Edit"} width={35} />
                        </SView>
                    }
                },


                {
                    key: "key-eliminar", label: "Eliminar", width: 70, center: true,
                    component: (key) => {
                        return <SView width={35} height={35} onPress={() => { SPopup.confirm({ title: "Eliminar", message: "¿Esta seguro de eliminar?", onPress: () => { Parent.Actions.eliminar(data[key], this.props) } }) }}>
                            <SIcon name={'Delete'} />
                        </SView>
                    }
                },
                 {
                    key: "key-dataTable", label: "dataTable", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => {
                            Parent.Actions.getUsuarios(data[item], this.props);
                            SNavigation.navigate(Parent.component+"/dataTable",{
                                key_dispositivo:item,
                            });
                        }}>
                            <SIcon name={"Ajustes"} width={35} />
                        </SView>
                    }
                },
                {
                    key: "key-connect", label: "Connect", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => {
                            Parent.Actions.conectar(data[item], this.props);
                        }}>
                            <SIcon name={"Off"} width={35} />
                        </SView>
                    }
                },
                {
                    key: "key-open", label: "entrada", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => {
                            Parent.Actions.open(data[item], {
                                operID: 1,
                                doorOrAuxoutID: 1,
                                outputAddrType: 1,
                                doorAction: 3,
                            }, this.props);
                        }}>
                            <SIcon name={"Salir"} width={35} />
                        </SView>
                    }
                },
                {
                    key: "key-open", label: "salida", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => {
                            Parent.Actions.open(data[item], {
                                operID: 1,
                                doorOrAuxoutID: 2,
                                outputAddrType: 1,
                                doorAction: 3,
                            }, this.props);
                        }}>
                            <SView style={{
                                transform: [
                                    { rotate: "180deg" }
                                ]
                            }}>
                                <SIcon name={"Salir"} width={35} />
                            </SView>

                        </SView>
                    }
                },
                {
                    key: "key-ip", label: "Sync IP", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => {
                            Parent.Actions.changeIp({...data[item], gateway:"255.255.255.255"}, this.props);
                        }}>
                            <SView style={{
                            }}>
                                <SIcon name={"Reload"} width={35} fill={"#f0f"}/>
                            </SView>
                        </SView>
                    }
                },
            ]}
            filter={(data) => {
                if (data.estado != 1) return false;
                if (this.key_punto_venta) {
                    if (this.key_punto_venta != data.key_punto_venta) return false;
                }
                return true;
            }}
            data={data}
        />
    }

    render() {
        if (!this.key_punto_venta) {
            SNavigation.goBack();
            return <SLoad />
        }
        return (
            <SPage title={'Lista de ' + Parent.component} disableScroll>
                {this.getLista()}
                <FloatButtom onPress={() => {
                    SNavigation.navigate(Parent.component + "/registro", { key_punto_venta: this.key_punto_venta });
                }} />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Lista);