import React from "react"
import Svg, { Rect, LinearGradient, Stop } from "react-native-svg"
const Gradient = (props ) => {
    return <Svg viewBox="0 0 342 588">
        <LinearGradient id="Naranja_amarillo" x1="0.5" y1="294" x2="341.5" y2="294" gradientUnits="userSpaceOnUse">
            <Stop offset="0" stop-color="#ffef26" />
            <Stop offset="1" stop-color="#e3312d" />
        </LinearGradient>
        <Rect class="cls-1" x="0.5" y="0.5" width="341" height="587" stroke="#1d1d1b" fill="url(#Naranja_amarillo)" />
    </Svg>
}
export default Gradient