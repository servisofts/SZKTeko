import React, { Component } from 'react';
import { TouchableOpacity, View, Text } from "react-native";
import { SIcon } from 'servisofts-component';
const FloatButtom = (props) => {
    if (props.esconder) {
        return <View />
    }
    return (
        <TouchableOpacity onPress={() => {
            props.onPress();
        }}
            style={{
                position: "absolute",
                right: 10,
                bottom: 40,
                width: 50,
                height: 50,
                // borderWidth: STheme.color.secondary + "22",
                // borderColor: STheme.color.secondary + "22",
                justifyContent: "center",
                alignItems: "center",
                // margin: 4,
                ...props.style
            }}>
            <SIcon name={props.icon ?? "Add"} />
            {/* <Svg name={"Add"} style={{
                width: "100%",
                height: "100%",
                // fill:"#C31"
            }} /> */}
        </TouchableOpacity >
    )
}
export default FloatButtom;