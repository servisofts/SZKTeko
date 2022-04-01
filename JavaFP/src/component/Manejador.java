package component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import util.console;

public class Manejador {

    public Manejador(JSONObject data, SSSessionAbstract session) {
        boolean showLog = true;
        if (data.getString("component").equals("socketTest")) {
            showLog = false;
        }
        if (showLog)
            console.log(console.ANSI_BLUE, " Manejador Socket Server -> : " + data.getString("component"));

        if (!data.isNull("component")) {
            switch (data.getString("component")) {
                case "usuario": {
                    //new Usuario(data, session);
                    break;
                }
            }
        } else {
            data.put("error", "No existe el componente");
        }
    }
}