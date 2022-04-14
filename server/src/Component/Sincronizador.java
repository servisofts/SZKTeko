package Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.SQLException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import Server.ServerSocketZkteco.ServerSocketZkteco;
import SocketCliente.SocketCliente;

public class Sincronizador {
    public static HashMap<String, Sincronizador> dispositivos = new HashMap<>();

    public JSONObject obj;
    public String key_punto_venta;
    
    public Sincronizador(JSONObject obj, String key_punto_venta) {
        this.obj = obj;
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

                UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), obj.getJSONArray("data"));
                
                usuarios = UsuarioDispositivo.getAllCodigos(dispositivo.getString("key"));

                
                
                //JSONObject usuario_aux = new JSONObject();
                //for (int j = 0; j < 5; j++) {
                //    usuario_aux.put(JSONObject.getNames(usuarios)[j], usuarios.getJSONObject(JSONObject.getNames(usuarios)[j]));
                //}
                send.put("key_dispositivo", dispositivo.getString("key"));
                //send.put("data", usuarios);
                
                JSONObject usuario_huella = null;

                switch(obj.getString("type")){
                    case "sincronizarUsuario":
                        usuario_huella = UsuarioHuella.getAllUsuario(obj.getJSONArray("data").getString(0));
                        break;
                    case "sincronizarAll":
                        usuario_huella = UsuarioHuella.getAll(dispositivo.getString("key"));
                        break;
                }

                
                send.put("huellas", usuario_huella);
                send.put("data", usuarios);
                send.put("noSend", true);
                send.put("delete_all", obj.getBoolean("delete_all"));
                ServerSocketZkteco.sendServer("ServerSocketZkteco", send.toString());
            }
        }
        return "exito";
    }

}