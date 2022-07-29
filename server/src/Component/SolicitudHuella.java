package Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class SolicitudHuella {
    public static HashMap<String, SolicitudHuella> solicitudes = new HashMap<>();

    public String key_punto_venta;
    public JSONObject solicitud;
    public SSSessionAbstract session;
    
    public SolicitudHuella(String key_punto_venta, JSONObject solicitud, SSSessionAbstract session) {
        this.key_punto_venta = key_punto_venta;
        this.solicitud = solicitud;
        this.session = session;
    }

    public JSONObject registrarHuella(String huella) throws SQLException{
        if(solicitud.has("key")){
            solicitud.put("huella", huella);
            solicitud.put("fecha_edit",SUtil.now());
            SPGConect.editObject("usuario_huella", solicitud);
            JSONObject send = new JSONObject();
            send.put("component", "dispositivo");
            send.put("type", "registro");
            send.put("data", solicitud);
            send.put("estado", "exito");

            this.session.send(send.toString());
            solicitudes.remove(this.key_punto_venta);
            return solicitud;            
        }

        JSONObject usuario_huella = new JSONObject();
        usuario_huella.put("key", UUID.randomUUID().toString());
        usuario_huella.put("key_usuario", this.solicitud.getString("key_usuario"));
        usuario_huella.put("fecha_on", "now()");
        usuario_huella.put("estado", 1);
        usuario_huella.put("codigo", this.solicitud.getInt("codigo"));
        usuario_huella.put("huella", huella);
        usuario_huella.put("fecha_edit", SUtil.now());
        usuario_huella.put("key_sucursal", this.solicitud.getString("key_sucursal"));
        SPGConect.insertArray("usuario_huella", new JSONArray().put(usuario_huella));

        usuario_huella.remove("huella");

        JSONObject send = new JSONObject();
        send.put("component", "dispositivo");
        send.put("type", "registro");
        send.put("data", usuario_huella);
        send.put("estado", "exito");

        this.session.send(send.toString());
        solicitudes.remove(this.key_punto_venta);
        return usuario_huella;
    }

    public void onEvent(JSONObject obj) throws SQLException{
        this.session.send(obj.toString());
    }

}
