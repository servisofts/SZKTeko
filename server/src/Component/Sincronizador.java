package Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import Server.ServerSocketZkteco.ServerSocketZkteco;
import Servisofts.SPGConect;

public class Sincronizador {
    public static HashMap<String, Sincronizador> dispositivos = new HashMap<>();

    public JSONArray key_usuarios;
    public String key_punto_venta;
    public Sincronizador(JSONArray key_usuarios, String key_punto_venta) {
        this.key_usuarios = key_usuarios;
        this.key_punto_venta = key_punto_venta;
    }

    public String sincronizar() throws SQLException{
        JSONObject dispositivos = Dispositivo.getAllKey(this.key_punto_venta);
        JSONObject dispositivo;
        JSONArray usuarios;

        JSONObject send = new JSONObject();
        send.put("component", "punto_venta");
        send.put("type", "sincronizar");
        send.put("estado", "cargando");

        for (int i = 0; i < JSONObject.getNames(dispositivos).length; i++) {
            dispositivo = dispositivos.getJSONObject(JSONObject.getNames(dispositivos)[i]);
            if(dispositivo.getString("key_tipo_dispositivo").equals("607b087c-6a92-4d8a-b311-e5c105cefd08")){

                UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), key_usuarios);
                
                usuarios = UsuarioDispositivo.getAllCodigos(dispositivo.getString("key"));
                
                //JSONObject usuario_aux = new JSONObject();
                //for (int j = 0; j < 5; j++) {
                //    usuario_aux.put(JSONObject.getNames(usuarios)[j], usuarios.getJSONObject(JSONObject.getNames(usuarios)[j]));
                //}
                send.put("key_dispositivo", dispositivo.getString("key"));
                //send.put("data", usuarios);
                send.put("data", usuarios);
                send.put("noSend", true);
                ServerSocketZkteco.sendServer("ServerSocketZkteco", send.toString());
            }
        }

                

        return "exito";
    }

}
