package Server.ServerSocketZkteco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;

import org.json.JSONObject;

import Component.PuntoVentaHistorico;
import Server.MensajeSocket;
import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;

public class SessionSocket_ extends SSSessionAbstract {
    
    private java.net.Socket miSession;
    private PrintWriter outpw = null;
    public String key_punto_venta = null;
    private X509Certificate cer;
    private JSONObject servicio = null;

    public SessionSocket_(Object session, ServerSocketZkteco server) {
        super(session, ((java.net.Socket) session).getRemoteSocketAddress().toString(), server);
        this.miSession = (java.net.Socket) session;
        try {
            outpw = new PrintWriter(miSession.getOutputStream(), true);
            Start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String mensaje) {
        System.out.println(mensaje);
        JSONObject data = new JSONObject(mensaje);
        data.put("id", getIdSession());
        data.put("noSend", false);
        if (servicio != null) {
            data.put("servicio", servicio);
        }
        
        if(ServerSocketZkteco.Manejador != null){
            ServerSocketZkteco.Manejador.apply(data, this);
        }
        
        if (!data.getBoolean("noSend")) {
            if (data.has("servicio")) {
                data.remove("servicio");
            }
            if (data.has("router")) {
                data.remove("router");
            }
            if (data.has("id")) {
                data.remove("id");
            }
            if (data.has("noSend")) {
                data.remove("noSend");
            }
            send(data.toString());
        }
    }

    @Override
    public void onClose(JSONObject obj) {
        try {
            miSession.close();
            super.onClose(obj);
            //Energy.closeSession(getKeyDevice());
            printLog("Conexion cerrada: ip = " + getIdSession() + " )");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(JSONObject obj) {
        printLog("Error: " + obj.getString("error"));

    }

    @Override
    public void send(String mensaje) {
        MensajeSocket mensajeSocket = new MensajeSocket(mensaje, this);
         outpw.write(mensaje + "---SSkey---" + mensajeSocket.getKey() +"---SSofts---");
        // outpw.write(mensaje + "\r\n");
        System.out.println(mensaje);
        outpw.flush();
    }

    public void Start() {
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        printLog("Nueva session desde " + getIdSession());
                        InputStream inp = null;
                        BufferedReader brinp = null;
                        inp = miSession.getInputStream();
                        brinp = new BufferedReader(new InputStreamReader(inp));
                        onOpen();
                        String line;
                        boolean isRun = true;
                        while (isRun) {
                            try {
                                line = brinp.readLine();
                                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                                    JSONObject obj = new JSONObject();
                                    obj.put("estado", "close");
                                    onClose(obj);
                                    return;
                                } else {
                                    if (line.length() > 0) {
                                        onMessage(line);
                                    }
                                }
                            } catch (Exception e) {
                                Pattern p = Pattern.compile(".*?onnection.has.closed.*");
                                if (e.getMessage() != null) {
                                    Matcher m = p.matcher(e.getMessage());
                                    boolean b = m.matches();
                                    if (b) {
                                        isRun = false;
                                        JSONObject objClose = new JSONObject();
                                        objClose.put("estado", "close");
                                        objClose.put("error", e.getLocalizedMessage());
                                        onClose(objClose);
                                        return;
                                    }
                                    JSONObject obj = new JSONObject();
                                    obj.put("estado", "error");
                                    obj.put("error", e.getLocalizedMessage());
                                    //if(e.getLocalizedMessage().contains("Connection reset")){
                                        isRun = false;
                                        JSONObject error = new JSONObject();
                                        error.put("estado", 0);
                                        error.put("key_punto_venta", key_punto_venta);
                                        error.put("error", "desconectado");
                                        error.put("IdSession", miSession.getRemoteSocketAddress().toString());
                                        PuntoVentaHistorico.registro(key_punto_venta, error);

                                        obj.put("component","punto_venta");
                                        obj.put("type","onChange");
                                        obj.put("estado","exito");
                                        obj.put("data",PuntoVentaHistorico.getLast(key_punto_venta));
                                        
                                        ServerSocketZkteco.sendServer(SSServerAbstract.TIPO_SOCKET, obj.toString());
                                        ServerSocketZkteco.sendServer(SSServerAbstract.TIPO_SOCKET_WEB, obj.toString());
                                    //}
                                    onError(obj);
                                } else {
                                    isRun = false;
                                    JSONObject objClose = new JSONObject();
                                    objClose.put("estado", "close");
                                    objClose.put("error", e.getLocalizedMessage());
                                    onClose(objClose);
                                }
                            }
                        }
                    } catch (Exception e) {
                        JSONObject objClose = new JSONObject();
                        objClose.put("estado", "close");
                        objClose.put("error", e.getLocalizedMessage());
                        onClose(objClose);
                    }
                }
            };
            t.start();
        } catch (Exception e) {
            JSONObject obj = new JSONObject();
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            onError(obj);
        }
    }

    @Override
    public void printLog(String mensaje) {
        SConsole.info(getIdSession() + ": " + mensaje);

    }

    @Override
    public void send(String arg0, MensajeSocket arg1) {
        // TODO Auto-generated method stub
        
    }

}
