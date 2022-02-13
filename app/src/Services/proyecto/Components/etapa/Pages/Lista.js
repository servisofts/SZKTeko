import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SDate, SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, STheme, SView } from 'servisofts-component';
import Parent from ".."
import FloatButtom from '../../../../../Components/FloatButtom';
import tipo_cargo from "../../tipo_cargo"
import tipo_seguimiento from '../../tipo_seguimiento';
import seguimiento from '../../seguimiento';
class Lista extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key_proyecto = SNavigation.getParam("key_proyecto");
    }

    getLista() {
        var data = Parent.Actions.getAll(this.props);
        var data_seguimiento = seguimiento.Actions.getAll(this.props);
        var data_tipo_seguimiento = tipo_seguimiento.Actions.getAll(this.props);
        if (!data) return <SLoad />
        if (!data_seguimiento) return <SLoad />
        if (!data_tipo_seguimiento) return <SLoad />

        return <STable2
            header={[
                { key: "index", label: "#", width: 50 },
                { key: "descripcion", label: "Descripcion", width: 150 },
                { key: "observacion", label: "observacion", width: 250 },
                { key: "key_moneda", label: "Moneda", width: 70, center: true },
                {
                    key: "monto", label: "Costo", width: 100, sumar: true, center: true, render: (item) => { return parseFloat(item ?? 0).toFixed(2) }
                },
                {
                    key: "key-seguimiento_Monto", label: "Efectivo Actual", width: 150, center: true,
                    render: (item) => {
                        let seguimientos = Object.values(data_seguimiento).filter(itm => itm.key_etapa == item);
                        if (seguimientos.length == 0) return parseFloat(0).toFixed(2);
                        var total = 0
                        seguimientos.map((obj) => {
                            total += parseFloat(obj.monto)
                        })
                        return parseFloat(total).toFixed(2);
                    },
                },
                {
                    key: "key-seguimiento_estado", label: "Estado", width: 250, center: true,
                    render: (item) => {
                        let seguimientos = Object.values(data_seguimiento).filter(itm => itm.key_etapa == item);
                        if (seguimientos.length == 0) return {};
                        seguimientos.sort((a, b) => {
                            return new SDate(b.fecha_on).isAfter(new SDate(a.fecha_on)) ? 1 : -1;
                        })
                        return seguimientos[0];
                    },
                    component: (item) => {
                        if (!item) return null;
                        if (!item.key_tipo_seguimiento) return null;
                        var dts = data_tipo_seguimiento[item.key_tipo_seguimiento];
                        return <SView row col={"xs-12"} height center>
                            <SView style={{
                                width: 35,
                                height: 35,
                                borderRadius: 4,
                                backgroundColor: dts.color,
                            }}>
                            </SView>
                            <SView width={8} />
                            <SView flex height center>
                                <SText col={"xs-12"}>{dts.descripcion}</SText>
                                <SText col={"xs-12"} color={STheme.color.gray}>{item.descripcion}</SText>
                            </SView>


                        </SView>
                    }
                },

                {
                    key: "key-seguimiento_cantidad", label: "# Seg.", width: 150, center: true,
                    render: (item) => {
                        let seguimientos = Object.values(data_seguimiento).filter(itm => itm.key_etapa == item);
                        return seguimientos.length
                    }
                },
                { key: "fecha_on", order: "asc", label: "Creacion", width: 150, center: true, render: (item) => { return new SDate(item).toString("yyyy-MM-dd hh:mm:ss") } },
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
                    key: "key-ver", label: "Ver", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => { SNavigation.navigate(Parent.component + "/perfil", { key: item }) }}>
                            <SIcon name={"Salir"} width={35} />
                        </SView>
                    }
                },
                {
                    key: "key-cargo", label: "Cargos", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => { SNavigation.navigate("cargo", { key_etapa: item }) }}>
                            <SIcon name={"Ajustes"} width={35} />
                        </SView>
                    }
                },
                {
                    key: "key-seguimiento", label: "Seguimientos", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => { SNavigation.navigate("seguimiento", { key_etapa: item }) }}>
                            <SIcon name={"Alert"} width={35} />
                        </SView>
                    }
                },

            ]}
            filter={(data) => {
                if (data.estado != 1) return false;
                if (this.key_proyecto) {
                    if (this.key_proyecto != data.key_proyecto) return false;
                }
                return true;
            }}
            data={data}
        />
    }

    render() {
        if (!this.key_proyecto) return <SText>No hay key proyecto</SText>
        return (
            <SPage title={'Lista de ' + Parent.component} disableScroll>
                {this.getLista()}
                <FloatButtom onPress={() => {
                    SNavigation.navigate(Parent.component + "/registro", { key_proyecto: this.key_proyecto });
                }} />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Lista);