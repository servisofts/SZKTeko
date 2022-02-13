import React, { Component } from 'react';
import { connect } from 'react-redux';
import { SIcon, SLoad, SNavigation, SPage, SPopup, STable2, SText, SView } from 'servisofts-component';
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
                { key: "observacion", label: "observacion", width: 250 },
                { key: "ip", label: "ip", width: 150 },
                { key: "puerto", label: "puerto", width: 150 },
                { key: "mac", label: "mac", width: 150 },
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