package Components;

import org.json.JSONObject;

import Servisofts.SConfig;
import SocketCliente.SocketCliente;

public class Servicio {
    public static final String Component = "servicio";

    public static void onMessage(JSONObject obj, JSONObject session) {
        switch (obj.getString("type")) {
            case "init":
                init(obj, session);
                break;
        }
    }

    public static void init(JSONObject obj, JSONObject session) {
        JSONObject response = new JSONObject();
        response.put("type", "identificarse");
        response.put("component", "punto_venta");
        response.put("key_punto_venta", SConfig.getJSON().getString("key_punto_venta"));
        response.put("key_tipo_dispositivo", "096acabc-3aca-41f3-86e3-d47b0e1add17");
        SocketCliente.send("zkteco", response.toString());
    }
}
