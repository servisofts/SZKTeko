package Server.ServerSocketZkteco;

import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;

public class ServerSocketZkteco extends SSServerAbstract {

    @FunctionalInterface
    public interface Manejador<T,U> {
        public void apply(T t, U u);
    }
    public static Manejador<JSONObject, SSSessionAbstract> Manejador;
    public ServerSocketZkteco(int puerto) {
        super(puerto, "ServerSocketZkteco");
    }

    @Override
    public void Start(int puerto) {
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        printLog("Iniciando server en el puerto " + puerto+" ...");
                        ServerSocket s;
                        s = new ServerSocket(getPuerto());
                        printLog("Socket iniciado esperando conexion...");
                        while (true) {
                            Socket socket = s.accept();
                            new SessionSocket_(socket, ServerSocketZkteco.this);
                        }
                    } catch (Exception e) {
                        printLog("Error: " + e.getMessage());
                    }
                }
            };
            t.start();
        } catch (Exception e) {
            printLog("Error: " + e.getMessage());
        }
    }

    @Override
    public void printLog(String mensaje) {
        SConsole.info(getTipoServer() + ": " + mensaje);
    }

}
