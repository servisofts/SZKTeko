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

public class Dispositivo {
    public static final String COMPONENT = "dispositivo";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "conectar":
                conectar(obj, session);
                break;
            case "open":
                open(obj, session);
                break;
            case "conectado":
                conectado(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "registro_huella":
                registro_huella(obj, session);
                break;
            case "changeIp":
                changeIp(obj, session);
                break;
            case "getUsuarios":
            case "getDataTable":
            case "deleteDataTable":
            case "registroDataTable":
                redirectZkteco(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all_dispositivo() as json";
            if(obj.has("key_punto_venta")){
                if(obj.has("key_tipo_dispositivo")){
                    consulta = "select get_all_dispositivo('"+obj.getString("key_punto_venta")+"', '"+obj.getString("key_tipo_dispositivo")+"') as json";
                }else{
                    consulta = "select get_all_dispositivo('"+obj.getString("key_punto_venta")+"') as json";
                }
            }
            
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getAllKey(String key_punto_venta) {
        try {
            String consulta =  "select get_all_dispositivo('"+key_punto_venta+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void conectar(JSONObject obj, SSSessionAbstract session) {
        ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
    }

    
    public static void open (JSONObject obj, SSSessionAbstract session) {
        ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
    }

    public static void conectado(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.getJSONObject("data").remove("actividad");
            SPGConect.editObject(COMPONENT, obj.getJSONObject("data"));
            DispositivoHistorico.registro(obj.getJSONObject("data").getString("key"), obj.getJSONObject("data"));
            ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("estado", "error");
            error.put("error", e.getLocalizedMessage());
            error.put("key_dispositivo", obj.getJSONObject("data").getString("key"));
            error.put("IdSession", session.getIdSession());
            DispositivoHistorico.registro(obj.getJSONObject("data").getString("key"), error);
        }
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

    public static void registro_huella(JSONObject obj, SSSessionAbstract session) {
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

    private static void changeIp(JSONObject obj, SSSessionAbstract session) {       
        ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
    }

    private static void redirectZkteco(JSONObject obj, SSSessionAbstract session) {      
        obj.put("noSend", true); 
        if(obj.getString("estado").equals("exito")){
            SSServerAbstract.sendServer(SSServerAbstract.TIPO_SOCKET, obj.toString());
            SSServerAbstract.sendServer(SSServerAbstract.TIPO_SOCKET_WEB, obj.toString());
            return;
        }
        ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
    }
}