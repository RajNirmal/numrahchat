package chatapp.numrah.com.chatapp;


import android.net.Uri;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class SocketListener {
    static String server = "dev.wefaaq.net:443";
    static AppLogger logger;

//    private static Socket socket = null;

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

//                server = server+"?session-id=" + sessionId + "&udid=" + udid + "&token=" + token;
            logger.info("Going to connect to server");
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("wss")
                    .encodedAuthority(server)

                    .appendQueryParameter("session-id", sessionId)
                    .appendQueryParameter("udid", udid)
                    .appendQueryParameter("token", token);
            logger.info(" The server URL "+ builder.build().toString() + "  "+ builder.build().getScheme() + "  "+ builder.build().getAuthority());
            HashMap<String, String> httpHeaders = new HashMap<>();
            httpHeaders.put("Sec-Websocket-Protocol","v2.fadfedly.com/2.1");
            httpHeaders.put("User-Agent","FadFed/0.1(Android/9.0)");
            connectToServer(builder.build(), httpHeaders);
//                SocketClient socks = new SocketClient(new URI(builder.build().toString()), new Draft_6455(Collections.<IExtension>emptyList(), Collections.<IProtocol>emptyList(), 2147), httpHeaders);
//                socks.connect();

        }catch (Exception exp){
            logger.error("SocketListener : ConnectToSocket");
            logger.error(exp.toString());
        }
    }

    private static void connectToServer(Uri serverUri, Map<String, String> header){
        com.github.nkzawa.socketio.client.Socket socket  = null;
        try {
            socket = IO.socket(new URI(serverUri.toString()));

        }catch (URISyntaxException exp){
            logger.info(" The exception is ");
            logger.error(exp.toString());
            logger.error(exp.getMessage());
            logger.error(exp.getReason());
        }
        socket.on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("connectToServer : ");
            }
        });
        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("connectToServer : ");
                logger.info("Event disconnect");
            }
        });
        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("connectToServer : ");
                logger.info("Event connect error");
                for(Object a : args){
                    logger.info(" The error is ");
                    logger.info(((Exception)a).getMessage());
                    logger.info(((Exception)a).toString());
                }
            }
        });
        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                logger.info("connectToServer : ");
                logger.info("Event connect timeout");
            }
        });

        socket.connect();
    }




//    public void sendData(JSONObject data){
//        try {
//            PrintWriter writer = new PrintWriter(socket.getOutputStream());
//            writer.print("Sec-Websocket-Protocol:  v2.fadfedly.com/2.1\r\n");
//            writer.print("User-Agent: Android/9.0\r\n");
//            // end the header section
//            writer.print("\r\n");
//            getData();
//        }catch (IOException exp){
//            logger.error(exp.toString());
//        }
//    }
//
//    public String getData(){
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String data = reader.readLine();
//            logger.info(" Received data from the server ");
//            logger.info(data);
//        }catch (IOException exp){
//            logger.error(exp.toString());
//        }
//        return "";
//    }
//    public static SocketFactory getKeyStore(){
//            try {
//                String STORETYPE = "JKS";
//                String KEYSTORE = "keystore.jks";
//                String STOREPASSWORD = "storepassword";
//                String KEYPASSWORD = "keypassword";
//
//                KeyStore ks = KeyStore.getInstance(STORETYPE);
//                File kf = new File(KEYSTORE);
//                ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());
//
//                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//                kmf.init(ks, KEYPASSWORD.toCharArray());
//                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//                tmf.init(ks);
//
//                SSLContext sslContext = null;
//                sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//                // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//
//                return sslContext.getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();
//            }catch (Exception exo){
//                logger.info("Exception while getting keystore");
//                logger.info(exo.toString());
//            }
//            return null;
//    }
//    public static class SocketClient extends WebSocketClient{
//
//        public SocketClient(URI serverUri, Draft draft, Map<String,String> httpHeader){
//            super(serverUri, draft, httpHeader);
//        }
//        public SocketClient(URI serverUri, Draft draft){
//            super(serverUri, draft);
//        }
//
//        public SocketClient(URI serverUri){
//            super(serverUri);
//        }
//
//        @Override
//        public void onMessage(String message) {
//            logger.info("SocketClient : OnMessage");
//            logger.info(message);
//        }
//
//        @Override
//        public void onOpen(ServerHandshake handshakedata) {
//            logger.info("SocketClient : onOpen");
//            logger.info(handshakedata.getHttpStatus() + "");
//        }
//
//        @Override
//        public void onClose(int code, String reason, boolean remote) {
//            logger.info("SocketClient : onClose");
//            logger.info(reason);
//            logger.info(code + "");
//            logger.info( remote + "");
//        }
//
//        @Override
//        public void onError(Exception ex) {
//            logger.info("SocketClient : onError");
//            logger.info(ex.toString());
//        }
//    }
}
