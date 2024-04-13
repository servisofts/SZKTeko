package Components;

import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SConsole;
import SocketCliente.SocketCliente;

public class punto_venta {
    public static final String Component = "punto_venta";

    public static void onMessage(JSONObject obj, JSONObject session) {
        switch (obj.getString("type")) {
            case "identificarse":
                identificarse(obj, session);
                break;
        }
    }

    public static void identificarse(JSONObject obj, JSONObject session) {
        if (obj.getString("estado").equals("exito")) {
            SConsole.log("Identificado con exito!!!");
            // State.punto_venta.put("obj", obj.getSJSonObject("obj"));
            JSONObject toSend = new JSONObject();
            toSend.put("component", "dispositivo");
            toSend.put("type", "getAll");
            toSend.put("estado", "cargando");
            toSend.put("key_tipo_dispositivo", "096acabc-3aca-41f3-86e3-d47b0e1add17");
            toSend.put("key_punto_venta", SConfig.getJSON().getString("key_punto_venta"));
            SocketCliente.send("zkteco", toSend.toString());
        } else {
            SConsole.log("ERROR al identificarse");

        }
    }
}
