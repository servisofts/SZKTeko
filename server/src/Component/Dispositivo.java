package Component;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.SConsole;
import Servisofts.SPGConect;
import Servisofts.SUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import ServerSocketZkteco.ServerSocketZkteco;

public class Dispositivo {

  public static HashMap<String, SSSessionAbstract> dispositivos = new HashMap<>();

  public static final String COMPONENT = "dispositivo";

  public static void onMessage(JSONObject obj, SSSessionAbstract session) {
    switch (obj.getString("type")) {
      case "getAll":
        getAll(obj);
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
        sincronizarMolinete(obj);
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

      case "reportedatosmolinete":
        reportedatosmolinete(obj, session);
        break;
      case "eliminarPin":
        eliminarPin(obj, session);
        break;
      case "clearData":
        clearData(obj, session);
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

    obj.put("noSend", true);
    boolean isOpen = false;

    try {
      // Validar que el objeto tiene la clave necesaria
      if (!obj.has("key_dispositivo")) {
        obj.put("estado", "error");
        obj.put("error", "La clave 'key_dispositivo' no está presente en el objeto.");
        session.send(obj.toString());
        return false;
      }

      String keyDispositivo = obj.getString("key_dispositivo");
      SConsole.log("key_dispositivo: " + keyDispositivo);

      // Obtener el dispositivo por clave
      JSONObject dispositivo = Dispositivo.getByKey(keyDispositivo);
      if (dispositivo == null) {
        obj.put("estado", "error");
        obj.put("error", "Dispositivo no encontrado.");
        session.send(obj.toString());
        return false;
      }

      // Validar clave 'key_punto_venta' en el dispositivo
      if (!dispositivo.has("key_punto_venta")) {
        obj.put("estado", "error");
        obj.put("error", "El dispositivo no tiene una clave 'key_punto_venta'.");
        session.send(obj.toString());
        return false;
      }

      String keyPuntoVenta = dispositivo.getString("key_punto_venta");
      SConsole.log("key_punto_venta: " + keyPuntoVenta);

      // Buscar la sesión del punto de venta
      SSSessionAbstract sspuntoVenta = PuntoVenta.sessions.get(keyPuntoVenta);
      if (sspuntoVenta == null) {
        obj.put("estado", "error");
        obj.put("error", "Sin comunicación con el punto de venta.");
        session.send(obj.toString());
        return false;
      }

      obj.put("dispositivo", dispositivo);

      // Verificar si la conexión está abierta
      if (sspuntoVenta.isOpen()) {
        JSONObject resp = sspuntoVenta.sendSync(obj);
        obj.put("estado", resp.getString("estado"));
        if (resp.has("error")) {
          obj.put("error", resp.getString("error"));
        }
      } else {
        obj.put("estado", "error");
        obj.put("error", "Conexión cerrada con el punto de venta.");
        session.send(obj.toString());
        return false;
      }

      isOpen = true;
      session.send(obj.toString());

    } catch (Exception e) {
      obj.put("estado", "error");
      obj.put("error", "Error inesperado: " + e.getMessage());
      session.send(obj.toString());
      isOpen = false;

      // Registrar detalles del error
      SConsole.error("Excepción en testConnection: " + e.getMessage());
      e.printStackTrace(); // Esto imprimirá el stack trace completo en la consola.
    }

    // Puntos de verificación en el log
    SConsole.log("Finalizando testConnection.");
    return isOpen;
  }

  public static JSONObject getAll(JSONObject obj) {
    try {
      String consulta = "select get_all_dispositivo() as json";
      if (obj.has("key_punto_venta")) {
        if (obj.has("key_tipo_dispositivo")) {
          consulta = "select get_all_dispositivo('" + obj.getString("key_punto_venta") + "', '"
              + obj.getString("key_tipo_dispositivo") + "') as json";
        } else {
          consulta = "select get_all_dispositivo('" + obj.getString("key_punto_venta") + "') as json";
        }
      }

      JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
      obj.put("data", data);
      obj.put("estado", "exito");
      return data;
    } catch (Exception e) {
      obj.put("estado", "error");
      e.printStackTrace();
      return null;
    }
  }

  public static void sincronizarLog(JSONObject obj, SSSessionAbstract session) {
    try {
      JSONObject data = new JSONObject(obj.toString());

      JSONObject dispositivo = Dispositivo.getByKey(data.getString("key_dispositivo"));
      data.put("dispositivo", dispositivo);
      JSONObject dispositivo_log_ = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta")).sendSync(data);

      JSONArray dispositivo_log;

      if (dispositivo_log_.getString("estado").equals("error")) {
        return;
      }

      dispositivo_log = dispositivo_log_.getJSONArray("data");

      JSONObject usuarioDispositivo;

      JSONArray dispositivosLog = new JSONArray();

      for (int i = 0; i < dispositivo_log.length(); i++) {
        usuarioDispositivo = UsuarioDispositivo.get(
            dispositivo_log.getJSONObject(i).getString("Pin"), data.getString("key_dispositivo"));
        if (usuarioDispositivo != null) {

          if (!usuarioDispositivo.isEmpty()) {
            String key_usuario = usuarioDispositivo.getString("key_usuario");
            dispositivo_log.getJSONObject(i).put("key_usuario", key_usuario);
            SConsole.warning("El usuario marco ingreso", key_usuario);
            dispositivosLog.put(dispositivo_log.getJSONObject(i));
          }
        }
      }

      DispositivoHistorico.registroAsistencia(data.getString("key_dispositivo"), dispositivosLog);

      JSONObject punto_venta = PuntoVenta.getByKeyDispositivo(data.getString("key_dispositivo"));
      String key_sucursal = null;
      if (punto_venta != null) {
        data.put("key_sucursal", punto_venta.getString("key_sucursal"));
        key_sucursal = punto_venta.getString("key_sucursal");
      }

      Dispositivo.borrarLog(dispositivo);

      obj.put("estado", "exito");
      obj.put("key_sucursal", key_sucursal);
      obj.put("key_dispositivo", data.getString("key_dispositivo"));
      obj.remove("data");
      SSSessionAbstract sesionClient = SSServerAbstract.getSessionByKeyServicio(punto_venta.getString("key_servicio"));
      if (sesionClient != null) {
        sesionClient.send(obj.toString());
      } else {
        SConsole.log("No se encontro el cliente para notificar");
      }

    } catch (Exception e) {
      e.printStackTrace();
      // TODO: handle exception
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

  public static void sincronizarMolinete(JSONObject obj) {
    try {

      // obj = dispositivos.get(obj.getString("key_dispositivo")).sendSync(obj);

      JSONObject dispositivo = Dispositivo.getByKey(obj.getString("key_dispositivo"));

      if (dispositivo == null) {
        obj.put("estado", "error");
        obj.put("error", "No existe dispositivo");
        return;
      }

      if (obj.optString("estado").equals("error")) {
        System.out.println("ya esta con error");
        obj.put("error", obj.getString("error"));
        return;
      }

      UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), obj.getJSONArray("data"),
          obj.getBoolean("delete_all"));

      JSONObject send = new JSONObject();
      send.put("component", "dispositivo");
      send.put("type", "getUsers");
      send.put("dispositivo", dispositivo);
      send.put("estado", "cargando");
      JSONArray users_dispositivo = PuntoVenta.sessions
          .get(dispositivo.getString("key_punto_venta")).sendSync(send).getJSONArray("data");


          
      JSONArray users_base_datos = UsuarioDispositivo.getAllCodigos(dispositivo.getString("key"));

      JSONArray eliminar = new JSONArray();
      JSONArray encontrados = new JSONArray();

      boolean encontrado;
      for (int i = 0; i < users_dispositivo.length(); i++) {

        encontrado = false;
        for (int j = 0; j < users_base_datos.length(); j++) {
          if (users_base_datos.getInt(j) == users_dispositivo.getInt(i)) {
            encontrados.put(users_base_datos.getInt(j));
            users_base_datos.remove(j);
            encontrado = true;
            break;
          }
        }
        if (!encontrado) {
          eliminar.put(users_dispositivo.get(i));
        }
      }

      JSONArray huellas_encontrados = UsuarioHuella.getAllHuellasActualizadas(dispositivo.getString("key"),
          encontrados.toString());
      JSONArray huellas_nuevos = UsuarioHuella.getAllHuellas(dispositivo.getString("key"), users_base_datos.toString());
      JSONArray huellas_eliminadas = UsuarioHuella
          .getAllHuellasEliminadas(dispositivo.getString("key"), encontrados.toString());
      // System.out.println();
      System.out.println("USUARIOS EXISTENTES = " + encontrados.length());
      System.out.println("USUARIOS NUEVOS = " + users_base_datos.length());
      System.out.println("USUARIOS TO REMOVE = " + eliminar.length());
      System.out.println("huellas encontradas= " + huellas_encontrados.length());
      System.out.println("huellas nuevas= " + huellas_nuevos.length());
      System.out.println("huellas actualizadassas = " + huellas_encontrados.length());

      send.put("component", "dispositivo");
      send.put("type", "sincronizarMolinete");
      send.put("data", users_base_datos);
      send.put("dataRemove", eliminar);
      send.put("huellas_nuevos", huellas_nuevos);
      send.put("huellas_encontrados", huellas_encontrados);
      send.put("huellas_eliminadas", huellas_eliminadas);
      send.put("estado", "cargando");

      send = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta")).sendSync(send, 3000000);

      String key_usuarios_dispositivo = "";
      for (int i = 0; i < huellas_eliminadas.length(); i++) {
        key_usuarios_dispositivo += "'" + huellas_eliminadas.getJSONObject(i).getString("key_usuario_dispositivo")
            + "',";
      }
      for (int i = 0; i < huellas_encontrados.length(); i++) {
        key_usuarios_dispositivo += "'" + huellas_encontrados.getJSONObject(i).getString("key_usuario_dispositivo")
            + "',";
      }
      for (int i = 0; i < huellas_nuevos.length(); i++) {
        key_usuarios_dispositivo += "'" + huellas_nuevos.getJSONObject(i).getString("key_usuario_dispositivo") + "',";
      }
      if (key_usuarios_dispositivo.length() > 0) {
        key_usuarios_dispositivo = key_usuarios_dispositivo.substring(0, key_usuarios_dispositivo.length() - 1);
      }

      if (send.getString("estado").equals("exito")) {
        if (key_usuarios_dispositivo.length() > 0) {
          UsuarioDispositivo.updateFechaEdit(key_usuarios_dispositivo);
        }
      }

      // System.out.println(usuario_huella);

      UsuarioDispositivo.Sincronizar(dispositivo.getString("key"), obj.getJSONArray("data"),
          obj.getBoolean("delete_all"));

      // System.out.println("venezuela");
      // System.out.println("venezuela");
      // System.out.println("venezuela");
      // System.out.println("venezuela");
      // System.out.println("venezuela");
      // System.out.println("venezuela");
      // System.out.println("venezuela");
      obj.remove("data");
      obj.put("estado", send.getString("estado"));
      if (send.getString("estado").equals("error")) {
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
      String consulta = "select get_by('" + COMPONENT + "', 'key', '" + key + "') as json";
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

  public static boolean open(JSONObject obj, SSSessionAbstract session) {

    boolean isOpen = false;
    JSONObject dispositivo = Dispositivo.getByKey(obj.getString("key_dispositivo"));
    SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));
    if (sspunto_venta != null) {
      obj.put("dispositivo", dispositivo);

      if (sspunto_venta.isOpen()) {
        obj = sspunto_venta.sendSync(obj);
      } else {
        obj.put("estado", "error");
        obj.put("error", "Conexion cerrada con el punto de venta.");
      }

      isOpen = true;
      session.send(obj.toString());
    } else {
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
      historico.put("EventType",
          obj.getJSONObject("data").getBoolean("isConected") ? "300" : "301");
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
      String consulta = "select get_all('" + COMPONENT + "', 'key_etapa', '" + key_etapa + "' ) as json";
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

  public static JSONArray getUsuariosEnMolinete(String key_dispositivo) {
    JSONObject dispositivo = Dispositivo.getByKey(key_dispositivo);
    SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));
    if (sspunto_venta == null) {
      return null;
    }
    JSONObject table = new JSONObject();
    table.put("name", "user");
    table.put("header", "CardNo\tPin\tName\tPassword\tGroup\tStartTime\tEndTime");
    JSONObject consulta = new JSONObject();
    consulta.put("component", "dispositivo");
    consulta.put("type", "getDataTable");
    consulta.put("version", "1.0");
    consulta.put("table", table);
    consulta.put("dispositivo", dispositivo);
    consulta.put("estado", "cargando");
    consulta.put("key_usuario", "");
    JSONObject resp = sspunto_venta.sendSync(consulta);
    return resp.getJSONArray("data");
  }

  public static JSONArray getHuellasEnMolinete(String key_dispositivo) {
    JSONObject dispositivo = Dispositivo.getByKey(key_dispositivo);
    SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));
    if (sspunto_venta == null) {
      return null;
    }
    JSONObject table = new JSONObject();
    table.put("name", "templatev10");
    table.put("header", "Pin\tFingerID\tValid\tTemplate\tSize");
    JSONObject consulta = new JSONObject();
    consulta.put("component", "dispositivo");
    consulta.put("type", "getDataTable");
    consulta.put("version", "1.0");
    consulta.put("table", table);
    consulta.put("dispositivo", dispositivo);
    consulta.put("estado", "cargando");
    consulta.put("key_usuario", "");
    JSONObject resp = sspunto_venta.sendSync(consulta);
    return resp.getJSONArray("data");
  }

  public static void reportedatosmolinete(JSONObject obj, SSSessionAbstract session) {
    try {
      String key_dispositivo = obj.getString("key_dispositivo");

      // Consulta para obtener la tabla de usuarios con sus claves
      JSONArray usuarios_table = SPGConect
          .ejecutarConsultaArray("select array_to_json(array_agg(usuario_dispositivo.*)) as json\n"
              + "from usuario_dispositivo\n" + "where usuario_dispositivo.key_dispositivo = '"
              + key_dispositivo + "'\n" + "and usuario_dispositivo.estado > 0");
      // Example. [{"codigo":"176","key_usuario":"asdasd-asd-asdas-dasd"}]

      JSONArray usuarios = Dispositivo.getUsuariosEnMolinete(key_dispositivo);
      // Example. [{"Pin":"176"}]
      JSONArray huellas = Dispositivo.getHuellasEnMolinete(key_dispositivo);
      // Example. [{"Pin":"176", "FingerID":"2"},{"Pin":"176", "FingerID":"1"}]

      for (int i = 0; i < usuarios.length(); i++) {
        JSONObject usr = usuarios.getJSONObject(i);
        String userPin = usr.getString("Pin");

        // Buscar y asociar la key_usuario correspondiente al usuario
        for (int j = 0; j < usuarios_table.length(); j++) {
          JSONObject usuarioTable = usuarios_table.getJSONObject(j);
          if ((usuarioTable.getInt("codigo") + "").equals(userPin)) {
            usr.put("key_usuario", usuarioTable.getString("key_usuario"));
            break;
          }
        }

        // Crear un array para almacenar las huellas asociadas al usuario
        JSONArray huellasUsuario = new JSONArray();

        // Buscar huellas que correspondan al usuario actual
        for (int j = 0; j < huellas.length(); j++) {
          JSONObject huella = huellas.getJSONObject(j);
          if (huella.getString("Pin").equals(userPin)) {
            huellasUsuario.put(huella);
          }
        }

        // Agregar el array de huellas al objeto del usuario
        usr.put("huellas", huellasUsuario);
      }

      // Añadir los datos finales al objeto de respuesta
      obj.put("data", usuarios);
      obj.put("estado", "exito");
    } catch (Exception e) {
      obj.put("estado", "error");
      e.printStackTrace();
    }
  }

  public static JSONObject eliminarPin(JSONObject obj, SSSessionAbstract session) {
    String key_dispositivo = obj.getString("key_dispositivo");
    String pin = obj.getString("pin");

    JSONObject dispositivo = Dispositivo.getByKey(key_dispositivo);
    SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));
    if (sspunto_venta == null) {
      return null;
    }
    JSONObject table = new JSONObject();
    table.put("name", "user");
    table.put("header", "Pin=" + pin);
    JSONObject consulta = new JSONObject();
    consulta.put("component", "dispositivo");
    consulta.put("type", "deleteDataTable");
    consulta.put("version", "1.0");
    consulta.put("table", table);
    consulta.put("dispositivo", dispositivo);
    consulta.put("estado", "cargando");
    consulta.put("key_usuario", "");
    JSONObject resp = sspunto_venta.sendSync(consulta);

    table.put("name", "userauthorize");
    JSONObject resp2 = sspunto_venta.sendSync(consulta);

    table.put("name", "templatev10");
    JSONObject resp3 = sspunto_venta.sendSync(consulta);

    obj.put("estado", "exito");
    return obj;

  }

  public static void clearData(JSONObject obj, SSSessionAbstract session) {
    try {
      String key_dispositivo = obj.getString("key_dispositivo");

      JSONObject dispositivo = Dispositivo.getByKey(key_dispositivo);
      SSSessionAbstract sspunto_venta = PuntoVenta.sessions.get(dispositivo.getString("key_punto_venta"));

      JSONObject consulta = new JSONObject();
      consulta.put("component", "dispositivo");
      consulta.put("type", "deleteDataTable");
      consulta.put("version", "1.0");
      consulta.put("dispositivo", dispositivo);
      consulta.put("estado", "cargando");
      consulta.put("key_usuario", "");

      JSONObject table = new JSONObject();
      table.put("name", "user");
      table.put("header", "CardNo\tPin\tName\tPassword\tGroup\tStartTime\tEndTime");
      consulta.put("table", table);
      JSONObject resp1 = sspunto_venta.sendSync(consulta);

      table = new JSONObject();
      table.put("name", "userauthorize");
      table.put("header", "Pin\tAuthorizeDoorId\tAuthorizeTimezoneId");
      consulta.put("table", table);
      JSONObject resp2 = sspunto_venta.sendSync(consulta);

      table = new JSONObject();
      table.put("name", "templatev10");
      table.put("header", "Pin\tFingerID\tValid\tTemplate\tSize");
      consulta.put("table", table);
      JSONObject resp3 = sspunto_venta.sendSync(consulta);

      // Añadir los datos finales al objeto de respuesta
      obj.put("data", "");
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
    if (obj.getString("estado").equals("exito")) {
      SSServerAbstract.sendServer(SSServerAbstract.TIPO_SOCKET, obj.toString());
      SSServerAbstract.sendServer(SSServerAbstract.TIPO_SOCKET_WEB, obj.toString());
      return;
    }
    ServerSocketZkteco.sendServer("ServerSocketZkteco", obj.toString());
  }

  public static void onEvent(JSONObject obj) {

    obj.put("type", "sincronizarLog");
    obj.put("estado", "cargando");
    if (obj.has("key_dispositivo")) {
      new Thread() {
        @Override
        public void run() {
          try {
            Thread.sleep(1000);
            sincronizarLog(obj, null);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }.start();
    }

    obj.put("noSend", true);
    /*
     * 23/07/2022 Ruddy Paz Ya no se marca la asistencia en cada evento
     *
     * obj.put("component", "zkteco"); JSONObject usuarioDispositivo =
     * UsuarioDispositivo.get(obj.getJSONObject("data").getString("Pin"),
     * obj.getString("key_dispositivo")); if(usuarioDispositivo!=null){
     * if(!usuarioDispositivo.isEmpty()){
     * obj.getJSONObject("data").put("key_usuario",
     * usuarioDispositivo.getString("key_usuario")); } }
     *
     *
     * DispositivoHistorico.registro(obj.getString("key_dispositivo"),
     * obj.getJSONObject("data"));
     *
     * JSONObject punto_venta =
     * PuntoVenta.getByKeyDispositivo(obj.getString("key_dispositivo"));
     *
     * if(punto_venta!=null){ obj.put("key_sucursal",
     * punto_venta.getString("key_sucursal")); }
     *
     *
     * obj.put("noSend", true);
     *
     * SSSessionAbstract session =
     * dispositivos.get(obj.getJSONObject("dispositivo").getString("key"));
     * session.send(obj.toString());
     */
  }

  public static JSONObject getAllFingerPrint(String key_punto_venta) {
    try {
      String consulta = "select get_all_dispositivo('" + key_punto_venta
          + "', '096acabc-3aca-41f3-86e3-d47b0e1add17') as json";
      return SPGConect.ejecutarConsultaObject(consulta);
    } catch (Exception e) {
      return null;
    }
  }

  public static JSONObject getAllKey(String key_punto_venta) {
    try {
      String consulta = "select get_all_dispositivo('" + key_punto_venta + "') as json";
      return SPGConect.ejecutarConsultaObject(consulta);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
