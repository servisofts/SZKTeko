import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SConsole;
import SocketCliente.SocketCliente;
import Servisofts.Servisofts;

public class App {
    public static void main(String[] args) throws Exception {
        Servisofts.ManejadorCliente = ManejadorCliente::onMessage;
        SConfig.configFile = "/Volumes/BOOTCAMP/SZKteco/Config.json";
        JSONObject Config = SConfig.getJSON();
        JSONObject cert =  new JSONObject();
        cert.put("OU", "zkteco");
        Config.put("cert", cert);
        SocketCliente.enableReconect(true);
        SocketCliente.StartNoSSL(Config);
    }
}
