package Component;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import Servisofts.SUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Server.ServerSocketZkteco.ServerSocketZkteco;

public class Dispositivo {

    public static HashMap<String, SSSessionAbstract> dispositivos = new HashMap<>();

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
            case "sincronizarLog":
                sincronizarLog(obj, session);
                break;
            case "sincronizarMolinete":
                sincronizarMolinete(obj, session);
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
            case "registro_huella":
                registro_huella(obj, session);
                break;
            case "onEvent":
                onEvent(obj);
                break;
            case "testConnection":
                testConnection(obj, session);
                break;
            case "getUsuariosActivos":
                getUsuariosActivos(obj, session);
                break;
            case "getUsuarios":
            case "getDataTable":
            case "deleteDataTable":
            case "getDeviceParam":
            case "registroDataTable":
                redirectZkteco(obj, session);
                break;
        }
    }

    public static void getUsuariosActivos(JSONObject obj, SSSessionAbstract session) {
        
        JSONObject dispositivo = Dispositivo.getByKey(obj.getString("key_dispositivo"));
        JSONArray users_base_datos = UsuarioDispositivo.getAllCodigosUsuarios(dispositivo.getString("key"));
        obj.put("data", users_base_datos);
        obj.put("estado", "exito");

    }
    public static boolean testConnection(JSONObject obj, SSSessionAbstract session) {
        boolean isOpen = false;
        JSONObject dispositivo = Dispositivo.getByKey(obj.getString("key_dispositivo"));
        SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));
        if(sspunto_venta != null){
            obj.put("dispositivo", dispositivo);

            if(sspunto_venta.isOpen()){
                obj = sspunto_venta.sendSync(obj);
            }else{
                obj.put("estado", "error");
                obj.put("error", "Conexion cerrada con el punto de venta.");    
            }

            isOpen = true;
            session.send(obj.toString());
        }else{
            isOpen = false;
            obj.put("estado", "error");
            obj.put("error", "Sin comunicación con el punto de venta.");
            session.send(obj.toString());
        }
        return isOpen;
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

    public static void sincronizarLog(JSONObject obj, SSSessionAbstract session) {
        try {
                JSONObject data = new JSONObject(obj.toString());

                JSONObject dispositivo = Dispositivo.getByKey(data.getString("key_dispositivo"));
                data.put("dispositivo", dispositivo);
                JSONArray dispositivo_log = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta")).sendSync(data).getJSONArray("data");

                
                JSONObject usuarioDispositivo;

                JSONArray dispositivosLog = new JSONArray();

                for (int i = 0; i < dispositivo_log.length(); i++) {
                    usuarioDispositivo = UsuarioDispositivo.get(dispositivo_log.getJSONObject(i).getString("Pin"), data.getString("key_dispositivo"));        
                    if(usuarioDispositivo!=null){
                        if(!usuarioDispositivo.isEmpty()){
                            dispositivo_log.getJSONObject(i).put("key_usuario", usuarioDispositivo.getString("key_usuario"));
                            dispositivosLog.put(dispositivo_log.getJSONObject(i));
                        }
                    }
                }

                DispositivoHistorico.registroAsistencia(data.getString("key_dispositivo"), dispositivosLog);

                JSONObject punto_venta = PuntoVenta.getByKeyDispositivo(data.getString("key_dispositivo"));
                if(punto_venta!=null){
                    data.put("key_sucursal", punto_venta.getString("key_sucursal"));
                }
                                
                data = Dispositivo.borrarLog(dispositivo);
/*
                obj.put("component", "zkteco");
                obj.put("type", "asistencia");
                obj.put("estado", "cargando");
                obj.put("dispositivo_log", dispositivo_log);

                obj = session.sendSync(obj);

                open(obj, session);
                System.out.println(dispositivo_log);
*/
            

        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static JSONObject borrarLog(JSONObject dispositivo) {
        JSONObject obj = new JSONObject();
        obj.put("component", "dispositivo");
        obj.put("type", "limpiarLog");
        obj.put("estado", "cargando");
        obj.put("dispositivo", dispositivo);

        obj = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta")).sendSync(obj);
        
        return obj;
    }

    public static void sincronizarMolinete(JSONObject obj, SSSessionAbstract session) {
        try {
        
            //obj = dispositivos.get(obj.getString("key_dispositivo")).sendSync(obj);

            JSONObject dispositivo = Dispositivo.getByKey(obj.getString("key_dispositivo"));

            JSONObject send = new JSONObject();
            send.put("component", "dispositivo");
            send.put("type", "getUsers");
            send.put("dispositivo", dispositivo);
            send.put("estado", "cargando");
            
            UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), obj.getJSONArray("data"), obj.getBoolean("delete_all"));

            JSONArray users_dispositivo = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta")).sendSync(send).getJSONArray("data");
            JSONArray users_base_datos = UsuarioDispositivo.getAllCodigos(dispositivo.getString("key"));

            JSONArray eliminar = new JSONArray();
            JSONArray encontrados = new JSONArray();

            boolean encontrado;
            for (int i = 0; i < users_dispositivo.length(); i++) {

                encontrado = false;
                for (int j = 0; j < users_base_datos.length(); j++) {
                    if(users_base_datos.getInt(j)==users_dispositivo.getInt(i)){
                        encontrados.put(users_base_datos.getInt(j));
                        users_base_datos.remove(j);
                        encontrado=true;
                        break;
                    }
                }    
                if(!encontrado){
                    eliminar.put(users_dispositivo.get(i));
                }
            }

            
            JSONArray huellas_encontrados = UsuarioHuella.getAllHuellasActualizadas(dispositivo.getString("key"), encontrados.toString());
            JSONArray huellas_nuevos = UsuarioHuella.getAllHuellas(dispositivo.getString("key"), users_base_datos.toString());

            
            send.put("component", "dispositivo");
            send.put("type", "sincronizarMolinete");
            send.put("data", users_base_datos);
            send.put("dataRemove", eliminar);
            send.put("huellas_nuevos", huellas_nuevos);
            send.put("huellas_encontrados", huellas_encontrados);
            send.put("estado", "cargando");

            send = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta")).sendSync(send,3000000);

            String key_usuarios_dispositivo = "";
            for (int i = 0; i < huellas_encontrados.length(); i++) {
                key_usuarios_dispositivo += "'"+huellas_encontrados.getJSONObject(i).getString("key_usuario_dispositivo")+"',";
            }
            for (int i = 0; i < huellas_nuevos.length(); i++) {
                key_usuarios_dispositivo += "'"+huellas_nuevos.getJSONObject(i).getString("key_usuario_dispositivo")+"',";
            }
            if(key_usuarios_dispositivo.length()>0){
                key_usuarios_dispositivo = key_usuarios_dispositivo.substring(0, key_usuarios_dispositivo.length()-1);
            }

            if(send.getString("estado").equals("exito")){
                if(key_usuarios_dispositivo.length()>0){
                    UsuarioDispositivo.updateFechaEdit(key_usuarios_dispositivo);
                }
            }

            //System.out.println(usuario_huella);

            //UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), obj.getJSONArray("data"), obj.getBoolean("delete_all"));

            obj.remove("data");
            obj.put("estado", send.getString("estado"));
            if(send.getString("estado").equals("error")){
                obj.put("error", send.getString("error"));
            }
            obj.put("noSend", false);
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by('"+COMPONENT+"', 'key', '"+key+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registro_huella(JSONObject obj, SSSessionAbstract session) {
        try {
            String huella = obj.getString("data");
            
            SolicitudHuella solicitud = SolicitudHuella.solicitudes.get(obj.getString("key_punto_venta"));
            JSONObject usuario_huella = solicitud.registrarHuella(huella);

            obj.put("data", usuario_huella);
            obj.put("estado", "exito");

            

        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void conectar(JSONObject obj, SSSessionAbstract session) {
        session = dispositivos.get(obj.getJSONObject("dispositivo").getString("key"));
        session.send(obj.toString());
    }

    
    
    public static boolean open (JSONObject obj, SSSessionAbstract session) {

        boolean isOpen = false;
        JSONObject dispositivo = Dispositivo.getByKey(obj.getString("key_dispositivo"));
        SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));
        if(sspunto_venta != null){
            obj.put("dispositivo", dispositivo);

            if(sspunto_venta.isOpen()){
                obj = sspunto_venta.sendSync(obj);
            }else{
                obj.put("estado", "error");
                obj.put("error", "Conexion cerrada con el punto de venta.");    
            }

            isOpen = true;
            session.send(obj.toString());
        }else{
            isOpen = false;
            obj.put("estado", "error");
            obj.put("error", "Sin comunicación con el punto de venta.");
            session.send(obj.toString());
        }
        return isOpen;
    }

    public static void conectado(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.getJSONObject("data").remove("actividad");
            JSONObject dispositivo = obj.getJSONObject("data");

            dispositivos.put(dispositivo.getString("key"), session);

            SPGConect.editObject(COMPONENT, obj.getJSONObject("data"));


            JSONObject historico = new JSONObject();
            historico.put("Fecha", SUtil.now());
            historico.put("Pin", "0");
            historico.put("Cardno", "0");
            historico.put("DoorID", "0");
            historico.put("EventType", obj.getJSONObject("data").getBoolean("isConected")?"300":"301");
            historico.put("InOutState", "0");
            historico.put("key_usuario", "");

            DispositivoHistorico.registro(obj.getJSONObject("data").getString("key"), historico);
            obj.put("noSend", true);
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

    public static void onEvent(JSONObject obj){
        /*
        23/07/2022
        Ruddy Paz
        Ya no se marca la asistencia en cada evento

        obj.put("component", "zkteco");
        JSONObject usuarioDispositivo = UsuarioDispositivo.get(obj.getJSONObject("data").getString("Pin"), obj.getString("key_dispositivo"));
        if(usuarioDispositivo!=null){
            if(!usuarioDispositivo.isEmpty()){
                obj.getJSONObject("data").put("key_usuario", usuarioDispositivo.getString("key_usuario"));
            }
        }    
        

        DispositivoHistorico.registro(obj.getString("key_dispositivo"), obj.getJSONObject("data"));

        JSONObject punto_venta = PuntoVenta.getByKeyDispositivo(obj.getString("key_dispositivo"));

        if(punto_venta!=null){
            obj.put("key_sucursal", punto_venta.getString("key_sucursal"));
        }


        obj.put("noSend", true);

        SSSessionAbstract session = dispositivos.get(obj.getJSONObject("dispositivo").getString("key"));
        session.send(obj.toString());
        */
    }
    public static JSONObject getAllFingerPrint(String key_punto_venta) {
        try {
            String consulta = "select get_all_dispositivo('"+key_punto_venta+"', '096acabc-3aca-41f3-86e3-d47b0e1add17') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            return null;
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

}