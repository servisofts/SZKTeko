import React, { Component } from 'react';
import { View, Text, Animated } from 'react-native';
import { SButtom, SView } from 'servisofts-component';

export default class NavBar extends Component {
    static INSTACE = null;
    static open() {
        NavBar.INSTACE.fadeIn();
    }
    static close() {
        NavBar.INSTACE.fadeOut();
    }

    constructor(props) {
        super(props);
        this.state = {
            timeAnim: 1000,
            isOpen: false,
        };
        NavBar.INSTACE = this;
        this.animSize = new Animated.Value(0);
    }

    fadeIn() {
        this.setState({ isOpen: true });
        Animated.timing(this.animSize, {
            toValue: 1,
            duration: this.state.timeAnim,
            useNativeDriver: true
        }).start();
    }

    fadeOut() {

        Animated.timing(this.animSize, {
            toValue: 0,
            duration: this.state.timeAnim,
            useNativeDriver: true
        }).start(() => {
            this.setState({ isOpen: false });
        });
    }

    getNav() {
        return <SView col={"xs-8 md-6 xl-4"} height backgroundColor={"#000"}
            style={{
                position: "absolute",
                left: this.animSize.interpolate({
                    inputRange: [0, 1],
                    outputRange: ["-70%", "0%"]
                }),
            }}

        >

        </SView>
    }
    render() {
        if (!this.state.isOpen) return <SView />
        return (
            <SView style={{
                position: "absolute",
                width: "100%",
                height: "100%",
                backgroundColor: "#66000066",
            }}
                activeOpacity={1}
                onPress={() => {
                    if (this.state.isOpen) {
                        this.fadeOut();
                    } else {
                        this.fadeIn();
                    }
                }
                }>
                {this.getNav()}
            </SView>
        );
    }
}
