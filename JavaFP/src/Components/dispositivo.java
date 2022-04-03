package Components;

import org.json.JSONObject;

import Servisofts.SConsole;

public class dispositivo {
    public static final String Component = "dispositivo";

    public static void onMessage(JSONObject obj, JSONObject session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, JSONObject session) {
        JSONObject data = obj.getJSONObject("data");
        String[] names = JSONObject.getNames(data);
        if (names.length <= 0) {
            SConsole.log("No hay dispositivos");
        } else {
            for (String name : names) {
                SConsole.log("FP encontrado: "+name);
            }
            // SConsole.log(obj.getJSONObject("data").toString());
        }
    }
}
