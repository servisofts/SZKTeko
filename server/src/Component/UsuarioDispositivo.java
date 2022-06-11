package Component;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Server.ServerSocketZkteco.ServerSocketZkteco;

public class UsuarioDispositivo {
    public static final String COMPONENT = "usuario_dispositivo";

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
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
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

    public static JSONObject getAll(String key_dispositivo) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_dispositivo', '"+key_dispositivo+"' ) as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            return null;
        }
    }
    public static JSONArray getAllCodigos(String key_dispositivo) {
        try {
            String consulta = "select get_all_codigos('"+key_dispositivo+"' ) as json";
            return SPGConect.ejecutarConsultaArray(consulta);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean Sincronizar(String key_dispositivo, JSONArray keys_usuarios, boolean delete_all) throws SQLException{
        
        String usuarios = keys_usuarios.toString().replaceAll("\"", "'");

        //Desactivar vencidos
        String consulta = "";
        if(delete_all){
            consulta = "update usuario_dispositivo \n"+
            "set estado = 0 \n"+
            "where key in ( \n"+
            "    select usuario_dispositivo.key \n"+
            "    from usuario_dispositivo \n"+
            "    where usuario_dispositivo.key_dispositivo = '"+key_dispositivo+"' \n"+
            "    and not usuario_dispositivo.key_usuario = ANY(ARRAY"+usuarios+") \n"+
            ")";
            SPGConect.ejecutarUpdate(consulta);
        }

        //Activar actuales
        consulta = "update usuario_dispositivo \n"+
        "set estado = 1 \n"+
        "where key in ( \n"+
        "    select usuario_dispositivo.key \n"+
        "    from usuario_dispositivo \n"+
        "    where usuario_dispositivo.key_dispositivo = '"+key_dispositivo+"' \n"+
        "    and usuario_dispositivo.key_usuario = ANY(ARRAY"+usuarios+") \n"+
        ")";
        SPGConect.ejecutarUpdate(consulta);

        consulta = "select array_to_json(array_agg(keys.key)) as json \n"+
        "from ( \n"+
        "               select unnest(array"+usuarios+") as key \n"+
        "           ) keys \n"+
        "           where keys.key not in ( \n"+
        "               select usuario_dispositivo.key_usuario \n"+
        "               from usuario_dispositivo \n"+
        "               where usuario_dispositivo.key_dispositivo = '"+key_dispositivo+"' \n"+
        "           )";

        JSONArray keys_usuarios_nuevos = SPGConect.ejecutarConsultaArray(consulta);
        JSONArray nuevos = new JSONArray();
        JSONObject nuevo;
        int codigo = UsuarioDispositivo.getCodigo(key_dispositivo);
        for (int i = 0; i < keys_usuarios_nuevos.length(); i++) {
            nuevo = new JSONObject();
            nuevo.put("key", UUID.randomUUID().toString());
            nuevo.put("key_usuario", keys_usuarios_nuevos.get(i));
            nuevo.put("fecha_on", "now()");
            nuevo.put("estado", 1);
            nuevo.put("key_dispositivo", key_dispositivo);
            nuevo.put("codigo", codigo++);
            nuevos.put(nuevo);
        }
        SPGConect.insertArray("usuario_dispositivo", nuevos);

        return true;
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

    public static int getCodigo(String key_dispositivo) throws SQLException {
        String consulta = "select max(codigo) as codigo from usuario_dispositivo where key_dispositivo = '" + key_dispositivo + "'";
        int codigo = SPGConect.ejecutarConsultaInt(consulta);
        return codigo==0?1:codigo+1;
    }

    public static JSONObject get(String codigo, String key_dispositivo) {
        try {
            String consulta = "select usuario_dispositivo_get('" + codigo + "', '" + key_dispositivo + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getByCodigo(String codigo) {
        try {
            String consulta = "select get_by('usuario_dispositivo', 'codigo', '" + codigo + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}