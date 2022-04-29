package Component;

import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Server.ServerSocketZkteco.ServerSocketZkteco;

public class LectorHuella {
    public static final String COMPONENT = "lector_huella";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "conectar":
                conectar(obj, session);
                break;
            case "registro_huella":
                registro_huella(obj, session);
                break;
            case "onEvent":
                onEvent(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "solicitud_registro_huella":
                solicitud_registro_huella(obj, session);
                break;
        }
    }

    public static void solicitud_registro_huella(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            JSONObject punto_venta = PuntoVenta.getByKeySucursal(data.getString("key_sucursal"));
            SolicitudHuella.solicitudes.put(punto_venta.getString("key"), new SolicitudHuella(punto_venta.getString("key"), data, session));

            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", "No existe punto de venta");
        }
    }

    public static void conectar(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.put("noSend", true);   
            if(obj.has("id_session")){
                SSServerAbstract.getSession(obj.getString("id_session")).send(obj.toString());;
            }else{
                obj.put("id_session", session.getIdSession());
                ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
            }

        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void onEvent(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.put("noSend", true);   
            JSONObject puntoVenta = PuntoVenta.getByKey(obj.getString("key_punto_venta"));
            obj.put("key_sucursal", puntoVenta.getString("key_sucursal"));
            SolicitudHuella solicitud = SolicitudHuella.solicitudes.get(obj.getString("key_punto_venta"));
            solicitud.onEvent(obj);
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", "No existe solicitud");
        }
    }

    public static void registro_huella(JSONObject obj, SSSessionAbstract session) {
        try {
            String huella = obj.getString("data");
            SolicitudHuella solicitud = SolicitudHuella.solicitudes.get(obj.getString("key_punto_venta"));
            solicitud.registrarHuella(huella);
            obj.put("data", "");
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", "No existe punto venta");
        }
        obj.put("noSend", true);
    }


    public static JSONObject getAll(String key_etapa) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_etapa', '"+key_etapa+"' ) as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            return null;
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            String fecha_on = formatter.format(new Date());
            JSONObject data = obj.getJSONObject("data");
            data.put("key", UUID.randomUUID().toString());
            data.put("estado", 1);
            data.put("fecha_on", fecha_on);
            data.put("key_usuario", obj.getString("key_usuario"));
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
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
