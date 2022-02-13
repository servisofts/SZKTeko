import React, { Component } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { SInput, SText, SThread, SView, SIcon, STheme } from 'servisofts-component';
// import STextImput from '../STextImput';

type Tprops = {
    repaint: Function,
    placeholder: String,
    contador: boolean,
    minLength: number,
}

export default class Buscador extends Component<Tprops> {
    static defaultProps = {
        minLength: -1,
    }
    constructor(props) {
        super(props);
        this.state = {
            value: "",
            cantidad: 0,
        };
    }

    buscar(data) {
        if (typeof data != "object") {
            return Object.keys(data);
        }
        var lista_keys = Object.keys(data);
        var val = this.state.value.trim() || "";
        if (val.length <= this.props.minLength) {
            return {}
        }
        // var arrPalabras = val.replaceAll(" ", "|");
        var arrPalabras = val.split(" ");
        var arr2 = [];
        var objFinal = {};
        lista_keys.map((key) => {
            var obj = data[key];
            var str = JSON.stringify(obj);
            var isValid = false;
            var peso = 0;
            for (let i = 0; i < arrPalabras.length; i++) {
                const txtTest = arrPalabras[i];
                var expreg = new RegExp(":.*?" + txtTest + ".*?(,|})", "i");
                var expreg2 = new RegExp("dato.:.*?" + txtTest + ".*?(,|})", "i");
                if (expreg.test(str) || expreg2.test(str)) {
                    isValid = true;
                    peso++;
                }
            }
            if (!this.state.verEliminados) {
                if (obj.estado == 0) {
                    isValid = false;
                }
            }
            if (isValid) {
                arr2.push(key);
                if (!objFinal[key]) {
                    objFinal[key] = data[key];
                }
                objFinal[key]["Peso"] = peso;
            }
        })
        this.setState({ cantidad: Object.keys(objFinal).length })
        return objFinal;
    }
    getVerEliminados = () => {
        if (!this.props.eliminados) {
            return <View />
        }
        return <TouchableOpacity
            onPress={() => {
                this.state.verEliminados = !this.state.verEliminados;
                this.props.repaint();
            }}
            style={{
                marginStart: 4,
                marginEnd: 4,
                width: 25,
                height: 25,
            }}>
            <SIcon name={(!this.state.verEliminados ? "NoDelete" : "Delete")} style={{
                width: 25,
                height: 25,
            }} />
        </TouchableOpacity>
    }
    render() {
        return (
            <SView col={"xs-12"} height={50} center>
                <View style={{
                    width: "90%",
                    // backgroundColor: "#fff",
                    justifyContent: "center",
                    alignItems: "center",
                    flexDirection: "row",

                }}>
                    <View style={{
                        flex: 1,
                        maxWidth: 600,
                        height: 30,
                        backgroundColor: STheme.color.card,
                        justifyContent: "center",
                        borderRadius: 8,
                        alignItems: "center",
                    }}>
                        <SInput placeholder={this.props.placeholder ? this.props.placeholder : "Buscar..."} style={{
                            width: "100%",
                            color: "#fff",
                            padding: 4,
                            paddingLeft: 8,
                            paddingRigth: 8,
                        }}
                            autoFocus={true}

                            onChangeText={(txt) => {
                                this.state.value = txt;
                                new SThread(500, "onChangeBuscador", true).start(() => {
                                    this.props.repaint();
                                })
                            }}
                        />

                    </View>
                    {this.getVerEliminados()}

                </View>
                {/* <SView col={"xs-12"} style={{
                    alignItems: "flex-end",
                    maxWidth: 600,
                }}>
                    <SText style={{
                        color: "#999", fontSize: 10,

                    }}>Resultados ({this.state.cantidad})</SText>
                </SView> */}
            </SView>
        );
    }
}
