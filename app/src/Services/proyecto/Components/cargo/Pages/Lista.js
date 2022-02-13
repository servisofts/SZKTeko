import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, SView } from 'servisofts-component';
import Parent from ".."
import FloatButtom from '../../../../../Components/FloatButtom';
import tipo_cargo from "../../tipo_cargo"
import etapa from '../../etapa';
class Lista extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key_etapa = SNavigation.getParam("key_etapa");
    }

    getLista() {
        if (this.key_etapa) {
            var data_etapa = etapa.Actions.getByKey(this.key_etapa, this.props);
            if (!data_etapa) return <SLoad />
            this.data_etapa = data_etapa;
        }
        var data = Parent.Actions.getAll(this.props);
        var data_tipo_cargo = tipo_cargo.Actions.getAll(this.props);
        if (!data) return <SLoad />
        if (!data_tipo_cargo) return <SLoad />
        return <STable2
            header={[
                { key: "index", label: "#", width: 50 },
                {
                    key: "key_tipo_cargo", label: "Tipo de cargo", width: 150, render: (item) => {
                        return data_tipo_cargo[item]?.descripcion;
                    }
                },
                { key: "descripcion", label: "Descripcion", width: 150 },
                { key: "observacion", label: "observacion", width: 350 },
                { key: "key-moneda", label: "Moneda", width: 70, center: true, render: (itm) => { return this.data_etapa?.key_moneda } },
                {
                    key: "monto", label: "Monto", width: 100, sumar: true, center: true, render: (item) => { return parseFloat(item ?? 0).toFixed(2) }
                },

                // { key: "key_cargo", label: "key_cargo", width: 150 },

                // { key: "key_etapa", label: "key_etapa", width: 150 },
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


            ]}
            filter={(data) => {
                if (data.estado != 1) return false;
                if (this.key_etapa) {
                    if (this.key_etapa != data.key_etapa) return false;
                }
                return true;
            }}
            data={data}
        />
    }

    render() {
        if (!this.key_etapa) {
            SNavigation.goBack();
            return <SLoad />
        }
        return (
            <SPage title={'Lista de ' + Parent.component} disableScroll>
                {this.getLista()}
                <FloatButtom onPress={() => {
                    SNavigation.navigate(Parent.component + "/registro", { key_etapa: this.key_etapa });
                }} />
                <FloatButtom style={{
                    bottom: 100,
                }} icon="Alert" onPress={() => {
                    SNavigation.navigate("etapa/select", {
                        key_proyecto: this.data_etapa?.key_proyecto,
                        onSelect: (itm) => {
                            SPopup.confirm({
                                title: "Copiar cargos de etapa",
                                message: "¿Esta seguro de copiar los cargos de la etapa seleccionada?",
                                onPress: () => {
                                    Parent.Actions.copiar({
                                        key_from: itm.key,
                                        key_to: this.key_etapa,
                                    }, this.props);
                                }
                            })

                        }
                    });
                }} />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Lista);