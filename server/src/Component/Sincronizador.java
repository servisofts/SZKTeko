package Component;

import java.sql.SQLException;
import java.util.HashMap;
import org.json.JSONObject;

import Server.SSSAbstract.SSSessionAbstract;
import Server.ServerSocketZkteco.ServerSocketZkteco;

public class Sincronizador {
    public static HashMap<String, Sincronizador> dispositivos = new HashMap<>();

    public JSONObject obj;
    public String key_punto_venta;
    public String key_usuario;
    public SSSessionAbstract session;
    
    public Sincronizador(JSONObject obj, String key_punto_venta, String key_usuario, SSSessionAbstract session) {
        this.obj = obj;
        this.key_punto_venta = key_punto_venta;
        this.key_usuario = key_usuario;
        this.session = session;
    }
    

    public String sincronizar() throws SQLException, InterruptedException{
        JSONObject dispositivos = Dispositivo.getAllKey(this.key_punto_venta);
        JSONObject dispositivo;

        JSONObject send = new JSONObject();
        send.put("component", "punto_venta");
        send.put("type", "sincronizarMolinete");
        send.put("estado", "cargando");
        send.put("key_usuario", key_usuario);

        for (int i = 0; i < JSONObject.getNames(dispositivos).length; i++) {
            dispositivo = dispositivos.getJSONObject(JSONObject.getNames(dispositivos)[i]);
            if(dispositivo.getString("key_tipo_dispositivo").equals("607b087c-6a92-4d8a-b311-e5c105cefd08")){

                
                UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), obj.getJSONArray("data"), obj.getBoolean("delete_all"));
                
                
                //JSONObject usuario_aux = new JSONObject();
                //for (int j = 0; j < 5; j++) {
                //    usuario_aux.put(JSONObject.getNames(usuarios)[j], usuarios.getJSONObject(JSONObject.getNames(usuarios)[j]));
                //}
                send.put("key_dispositivo", dispositivo.getString("key"));
                //send.put("data", usuarios);
                
                JSONObject usuario_huella = null;

                switch(obj.getString("type")){
                    case "sincronizarUsuario":
                        usuario_huella = UsuarioHuella.getAllUsuario(obj.getJSONArray("data").getString(0), dispositivo.getString("key"));
                        break;
                    case "sincronizarAll":
                        usuario_huella = UsuarioHuella.getAll(dispositivo.getString("key"));
                        break;
                }

                
                send.put("huellas", usuario_huella);
                send.put("noSend", true);
                send.put("delete_all", obj.getBoolean("delete_all"));
                session.send(send.toString());
                Thread.sleep(1000);
            }
        }
        return "exito";
    }

}