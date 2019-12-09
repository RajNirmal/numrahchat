package chatapp.numrah.com.chatapp;

import android.net.Uri;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;

public class SocketListener {
    static String server = "dev.wefaaq.net";
    static AppLogger logger;

    private static Socket socket = null;

    public static SocketListener listener;

    public static SocketListener getInstance(String sessionId, String udid, String token){
        logger = new AppLogger();
        listener.connectToSocet(sessionId, udid, token);
        return listener;
    }

    public static SocketListener getInstance(){
        return listener;
    }


    public static void connectToSocet(String sessionId, String udid, String token){
        try {
            if (socket == null) {
//                server = server+"?session-id=" + sessionId + "&udid=" + udid + "&token=" + token;
                logger.info("Going to connect to server");
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("wss")
                        .authority(server);
//                        .appendQueryParameter("session-id", sessionId)
//                        .appendQueryParameter("udid", udid)
//                        .appendQueryParameter("token", token);

                logger.info(" Ther server URL is "+ builder.build().toString());
                socket = new Socket(builder.build().toString(), 433);

            }
        }catch (Exception exp){
            logger.error("SocketListener : ConnectToSocket");
            logger.error(exp.toString());
        }
    }

    public void sendData(JSONObject data){
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.print("Sec-Websocket-Protocol:  v2.fadfedly.com/2.1\r\n");
            writer.print("User-Agent: Android/9.0\r\n");
            // end the header section
            writer.print("\r\n");
            getData();
        }catch (IOException exp){
            logger.error(exp.toString());
        }
    }

    public String getData(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data = reader.readLine();
            logger.info(" Received data from the server ");
            logger.info(data);
        }catch (IOException exp){
            logger.error(exp.toString());
        }
        return "";
    }

}
