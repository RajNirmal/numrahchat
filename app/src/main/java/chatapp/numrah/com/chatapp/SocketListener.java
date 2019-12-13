package chatapp.numrah.com.chatapp;


import android.net.Uri;
import android.os.Build;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SocketListener  {
    static String server = "dev.wefaaq.net";
    static AppLogger logger;

    private static String webSocketProtocol = "Sec-Websocket-Protocol";
    private static String userAgent = "User-Agent";
    private static String session_Id = "session-id";
    private static String mudid = "udid";
    private static String mtoken = "token";

    private WebSocket socket;



    public static SocketListener socketListener = null;

    public static SocketListener getInstance(String sessionId, String udid, String token){
        logger = new AppLogger();
        if(socketListener == null){
            socketListener = new SocketListener();
        }
        socketListener.connectToSocet(sessionId, udid, token);
        return socketListener;
    }

    public static SocketListener getInstance(){
        return socketListener;
    }


    public void connectToSocet(String sessionId, String udid, String token){
        try {

//                server = server+"?session-id=" + sessionId + "&udid=" + udid + "&token=" + token;
            logger.info("Going to connect to server");

            HashMap<String, String> httpHeaders = new HashMap<>();
            httpHeaders.put(webSocketProtocol,"v2.fadfedly.com/2.1");
            httpHeaders.put(userAgent,"FadFed/0.1(Android/9.0)");
            httpHeaders.put(session_Id, sessionId);
            httpHeaders.put(mtoken, token);
            httpHeaders.put(mudid, udid);
            connectToServer( httpHeaders);


        }catch (Exception exp){
            logger.error("SocketListener : ConnectToSocket");
            logger.error(exp.toString());
        }
    }
    private WebSocketListener listesner = new WebSocketListener() {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            logger.error("WebSocketListener : The socket connection is closed "+ reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            logger.error("WebSocketListener :  Going to close the connection "+ reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            logger.error("WebSocketListener : Connection failure "+response.toString());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            logger.info("onMessage" + text);
            String messageType;
            JSONObject message;
            try {
                JSONArray msgArray = new JSONArray(text);
                logger.info(msgArray.toString());
                messageType = msgArray.getString(0);
                if(!msgArray.isNull(1)) {
                    message = (JSONObject) new JSONArray(text).get(1);
                }else{
                    message = new JSONObject();
                }
                logger.info("MsgType : "+messageType);
                logger.info("Message : " + message);
                AppUtil.getInstance().handleMessage(messageType, message);
            }catch (JSONException exp){
                logger.error(" onMessage : Error while parsing message "+exp.toString());
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            logger.info("onMessage bytes : " + bytes.base64());
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            logger.info("OnOpen "+response.toString());
        }
    };

    private void connectToServer(Map<String, String> header){
        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(server)
                    .addQueryParameter("session-id", header.get(session_Id))
                    .addQueryParameter("udid", header.get(mudid))
                    .addQueryParameter("token", header.get(mtoken))
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30000, TimeUnit.MILLISECONDS)
                    .readTimeout(30000, TimeUnit.MILLISECONDS)
                    .writeTimeout(30000, TimeUnit.MILLISECONDS)
                    .build();


            Request req = new Request.Builder()
                    .url(url)
                    .header("Sec-Websocket-Protocol", "v2.fadfedly.com/2.1")
                    .header("User-Agent", "FadFed/" + BuildConfig.VERSION_NAME + " (Android/" + Build.VERSION.RELEASE + ")")
                    .build();
            socket = client.newWebSocket(req, listesner);
            // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
            client.dispatcher().executorService().shutdown();

        }catch (Exception exp){
            logger.info(" Exception while running command");
            logger.error(exp.toString());
        }
    }

    public void sendMessageToServer(String messageType, JSONObject data){
        JSONArray message = new JSONArray();
        message.put(messageType);
        message.put(data);
        logger.info("sendMessageToServer : "+message.toString());
        socket.send(message.toString());
    }
}
