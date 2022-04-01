import org.json.JSONArray;
import org.json.JSONObject;

import Components.Servicio;
import Components.dispositivo;
import Components.punto_venta;
import Servisofts.SConsole;

public class ManejadorCliente {
    public static void onMessage(JSONObject data, JSONObject config) {
        // SConsole.log(data.toString());
        // SConsole.warning(data.toString());
        if (data.isNull("component")) {
            data.put("error", "No existe el componente");
            return;
        }
        if (data.has("estado")) {
            if (data.getString("estado").equals("error")) {
                SConsole.log("ERROR: " + data.get("error").toString());
            }
        }

        switch (data.getString("component")) {
            case Servicio.Component:
                Servicio.onMessage(data, config);
                break;
            case punto_venta.Component:
                punto_venta.onMessage(data, config);
                break;
            case dispositivo.Component:
                dispositivo.onMessage(data, config);
                break;
        }
    }

}
