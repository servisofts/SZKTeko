package Component;

import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import Server.SSSAbstract.SSSessionAbstract;

public class DispositivoHistorico {
    public static final String COMPONENT = "dispositivo_historico";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getLast(String key_dispositivo) {
        try {
            String consulta = "select get_last_dispositivo_historico('"+key_dispositivo+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            return null;
        }
    }

    public static void registro(String key_dispositivo, JSONObject data) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            String fecha_on = formatter.format(new Date());

            JSONObject hisorico = new JSONObject();
            hisorico.put("key", UUID.randomUUID().toString());
            hisorico.put("estado", 1);
            hisorico.put("fecha_on", fecha_on);
            data.put("fecha_on", fecha_on);
            hisorico.put("data", data);

            if(key_dispositivo != null && key_dispositivo.length() > 0){
                hisorico.put("key_dispositivo", key_dispositivo);
            }
            
            SPGConect.insertArray(COMPONENT, new JSONArray().put(hisorico));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

}
