import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SConsole;
import SocketCliente.SocketCliente;
import Vista.FInicio;
import ZKFP.ZKFP;
import Servisofts.Servisofts;

public class App {
    public static void main(String[] args) throws Exception {
       // new FInicio();
        Servisofts.ManejadorCliente = ManejadorCliente::onMessage;
        SConfig.configFile = "C:/SZKteco/Config.json";
        JSONObject Config = SConfig.getJSON();
        JSONObject cert = new JSONObject();
        cert.put("OU", "zkteco");
        Config.put("cert", cert);
        SocketCliente.enableReconect(true);
        SocketCliente.StartNoSSL(Config);
        ZKFP.getInstance();

        // new FInicio();

    }
}
