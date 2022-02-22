import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SDate, SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, SView } from 'servisofts-component';
import Parent from ".."
import FloatButtom from '../../../../../Components/FloatButtom';
class Lista extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.key_servicio = SNavigation.getParam("key_servicio");
    }

    getLista() {
        var data = Parent.Actions.getAll(this.props);
        if (!data) return <SLoad />
        return <STable2
            header={[
                { key: "index", label: "#", width: 50 },
                { key: "descripcion", label: "Descripcion", width: 150 },
                { key: "observacion", label: "observacion", width: 150 },
                {
                    key: "actividad-estado", label: "Actividad", center: true, width: 70, component: (item) => {
                        return <SView style={{
                            width: 30,
                            height: 30,
                            borderRadius: 4,
                            backgroundColor: item.estado == 1 ? "#0f0" : "#f00"
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
                // {
                //     key: "key-ver", label: "Ver", width: 50, center: true,
                //     component: (item) => {
                //         return <SView onPress={() => { SNavigation.navigate(Parent.component + "/perfil", { key: item }) }}>
                //             <SIcon name={"Salir"} width={35} />
                //         </SView>
                //     }
                // },
                {
                    key: "key-reboot", label: "Reboot", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => { 
                            Parent.Actions.reboot(data[item],this.props);
                         }}>
                            <SIcon name={"Off"} width={35} />
                        </SView>
                    }
                },

                {
                    key: "key-dispositivos", label: "Devices", width: 50, center: true,
                    component: (item) => {
                        return <SView onPress={() => { SNavigation.navigate("dispositivo", { key_punto_venta: item }) }}>
                            <SIcon name={"Ajustes"} width={35} />
                        </SView>
                    }
                },



            ]}
            filter={(data) => {
                if (data.estado != 1) return false;
                if (this.key_servicio) {
                    if (this.key_servicio != data.key_servicio) return false;
                }
                return true;
            }}
            data={data}
        />
    }

    render() {
        return (
            <SPage title={'Lista de ' + Parent.component} disableScroll>
                {this.getLista()}
                <FloatButtom onPress={() => {
                    SNavigation.navigate(Parent.component + "/registro", { key_servicio: this.key_servicio });
                }} />
            </SPage>
        );
    }
}
const initStates = (state) => {
    return { state }
};
export default connect(initStates)(Lista);