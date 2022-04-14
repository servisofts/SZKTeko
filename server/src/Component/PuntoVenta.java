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
import Server.ServerSocketZkteco.SessionSocket_;
import Server.ServerSocketZkteco.ServerSocketZkteco;

public class PuntoVenta {
    public static final String COMPONENT = "punto_venta";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "sincronizar":
                sincronizar(obj);
                break;
            case "sincronizarAll":
                sincronizarAll(obj);
                break;
            case "sincronizarUsuario":
                sincronizarUsuario(obj);
                break;
            case "identificarse":
                identificarse(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "ping":
                ping(obj);
                break;
            case "reboot":
                reboot(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all_punto_venta() as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void identificarse(JSONObject obj, SSSessionAbstract session) {
        String key_punto_venta = obj.getString("key_punto_venta");
        ((SessionSocket_)session).key_punto_venta = key_punto_venta;

        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key', '"+key_punto_venta+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);

            if(data.isEmpty()){
                JSONObject error = new JSONObject();
                error.put("estado", "error");
                error.put("error", "No existe el punto de venta");
                error.put("key_punto_venta", key_punto_venta);
                error.put("IdSession", session.getIdSession());
                PuntoVentaHistorico.registro("", error);
                return;    
            }
            JSONObject save = data.getJSONObject(key_punto_venta);
            save.put("IdSession", session.getIdSession());
            PuntoVentaHistorico.registro(key_punto_venta, save);

            JSONObject avisar = new JSONObject();
            avisar.put("component","punto_venta");
            avisar.put("type","onChange");
            avisar.put("estado","exito");
            JSONObject data_ = PuntoVentaHistorico.getLast(key_punto_venta);
            data_.put("key_punto_venta", key_punto_venta);
            avisar.put("data",data_);
            ServerSocketZkteco.sendServer(SSServerAbstract.TIPO_SOCKET, avisar.toString());
            ServerSocketZkteco.sendServer(SSServerAbstract.TIPO_SOCKET_WEB, avisar.toString());

            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            JSONObject data = new JSONObject();
            data.put("estado", "error");
            data.put("data", e.getLocalizedMessage());
            PuntoVentaHistorico.registro("", data);
            e.printStackTrace();
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

    public static void reboot(JSONObject obj, SSSessionAbstract session) {
        obj.put("noSend", true);
        ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
    }

    public static void getByKeySucursal(JSONObject obj) {
        try {
            String consulta = "select get_by('punto_venta','key_sucursal','"+obj.getString("key_sucursal")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getByKeySucursal(String key_sucursal) {
        try {
            String consulta = "select get_by('punto_venta','key_sucursal','"+key_sucursal+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sincronizarAll(JSONObject obj) {
        try {
            String consulta = "select get_all_punto_venta('"+obj.getJSONObject("servicio").getString("key")+"') as json";
        
            JSONObject puntos_venta = SPGConect.ejecutarConsultaObject(consulta);

            JSONObject punto_venta;

            JSONObject send;
            for (int i = 0; i < JSONObject.getNames(puntos_venta).length; i++) {
                punto_venta = puntos_venta.getJSONObject(JSONObject.getNames(puntos_venta)[i]);
                send = new JSONObject();
                send.put("component", "punto_venta");
                send.put("type", "ping");
                send.put("estado", "cargando");
                send.put("key_punto_venta", punto_venta.getString("key"));
                Sincronizador.dispositivos.put(punto_venta.getString("key"), new Sincronizador(obj,punto_venta.getString("key")));
                ServerSocketZkteco.sendServer("ServerSocketZkteco", send.toString());
            }
            obj.put("estado", "exito");
            
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void ping(JSONObject obj) {
        try {
            if(obj.getString("estado").equals("exito")){
                Sincronizador sinc = Sincronizador.dispositivos.get(obj.getString("key_punto_venta"));
                String estado = sinc.sincronizar();
                obj.put("estado", estado);
            }
            
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
        obj.put("noSend", true);
    }

    public static void sincronizarUsuario(JSONObject obj) {
        try {
            String consulta = "select get_all_punto_venta('"+obj.getJSONObject("servicio").getString("key")+"') as json";
        
            JSONObject puntos_venta = SPGConect.ejecutarConsultaObject(consulta);

            JSONObject punto_venta;

            JSONObject send;
            for (int i = 0; i < JSONObject.getNames(puntos_venta).length; i++) {
                punto_venta = puntos_venta.getJSONObject(JSONObject.getNames(puntos_venta)[i]);
                send = new JSONObject();
                send.put("component", "punto_venta");
                send.put("type", "ping");
                send.put("estado", "cargando");
                send.put("key_punto_venta", punto_venta.getString("key"));
                
                Sincronizador.dispositivos.put(punto_venta.getString("key"), new Sincronizador(obj, punto_venta.getString("key")));
                ServerSocketZkteco.sendServer("ServerSocketZkteco", send.toString());
            }
            obj.put("estado", "exito");
            obj.put("noSend", true);
            
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void sincronizar(JSONObject obj) {
        try {
            if(obj.getString("estado").equals("exito")){
                //Aqui guardamos la hora de sincronizacion.
                System.out.println("Sincronizacion exitosa");
            }
            if(obj.getString("estado").equals("error")){
                //Aqui guardamos la hora de sincronizacion.
                System.out.println("Sincronizacion erronea");
            }

            obj.put("noSend", true);
            
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
        obj.put("noSend", true);
    }

}
