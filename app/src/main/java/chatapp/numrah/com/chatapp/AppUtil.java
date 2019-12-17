package chatapp.numrah.com.chatapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AppUtil {
    public static AppUtil util = null;
    private ChatAppData appData = null;
    private Context  mContext;
    static AppLogger logger;
    public static AppUtil getInstance(){
        if(util == null){
            util = new AppUtil();
        }
        logger = new AppLogger();
        return util;
    }

    public String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789"+ "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index= (int)(AlphaNumericString.length()* Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    //Always use application context for this
    public String initSocketConnection(Context context){
        mContext=context;
        appData = ChatAppData.getInstance(context);
//        clearPreviousSessionData();
        String sessionId = getAlphaNumericString(AppConstants.uuidSize);
        String udid = getId(mContext);
        String token = getToken(sessionId, udid);
        SocketListener.getInstance(sessionId,udid, token);
        return "";
    }

    public void restablishSocket(Context context){
        mContext = context;
        String sessionId = appData.getString(AppConstants.SESSION_ID);
        String udid = getId(mContext);
        String token = getToken(sessionId, udid);
        SocketListener.getInstance(sessionId,udid, token);
    }

    private String getId(Context context) {
        return android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    private String getToken(String sessionId, String udid){
        String randoms = sessionId + String.valueOf((System.currentTimeMillis()/300000));
        String encodedToken = md5(udid, randoms, "HmacMD5");
        logger.info(" The token is "+encodedToken);
        return encodedToken;
    }

    public String md5(String msg, String keyString, String algo){
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString());
        } catch (InvalidKeyException e) {
            logger.error(e.toString());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.toString());
        }
        return digest;
    }

    public void handleMessage(String msgType, JSONObject msgBody){
        try {
            MessageHandler handler = new MessageHandler();
            switch (msgType) {
                case AppConstants.MSG_TYPE_SESSION:
                    handler.handleSessionMessage(msgBody);
                    break;
                case AppConstants.MSG_TYPE_STATUS:
                    handler.handleStatusMessage(msgBody);
                    break;
                case AppConstants.MSG_TYPE_LEAVE:
                    handler.handleLeaveMessage(msgBody);
                    break;
                case AppConstants.MSG_TYPE_MATCHED:
                    handler.handleMatchedMessage(msgBody);
                    break;
                case AppConstants.MSG_TYPE_SYNC:
                    handler.handleSyncMessage(msgBody);
                    break;
                case AppConstants.MSG_TYPE_CHAT:
                    handler.handleChatMessage(msgBody);
                    break;
                case AppConstants.MSG_TYPE_SEEN:
                case AppConstants.MSG_TYPE_ACK:
                    handler.handleAckMessage(msgType, msgBody);
                    break;
                default:
                    logger.info(" This message type is not handled");
                    break;

            }
        }catch (Exception exp){
            logger.error(" Error while processing hanldeMessage");
            logger.error(exp.toString());
        }
    }

    public void sendMatchingMessage(Context context){
        try {
            int selectedMatching = ChatAppData.getInstance(context).getInt(AppConstants.MATCH_SELECTED);
            String algo = ChatAppData.getInstance(context).getString(AppConstants.ALGOS);
            logger.info(algo);
            JSONObject messageObj = new JSONObject();
            messageObj.put(AppConstants.SERVERMSG_ALGO, new JSONArray(algo).get(0));
            if(selectedMatching == 1){
                messageObj.put(AppConstants.SERVERMSG_GENDER, "m");
            }else if(selectedMatching == 2){
                messageObj.put(AppConstants.SERVERMSG_GENDER, "f");
            }

            logger.info(" The json being sent is "+ messageObj.toString());
            SocketListener.getInstance().sendMessageToServer(AppConstants.SERVERMSG_MSGTYPE_MATCH, messageObj);
        }catch (JSONException exp){
            logger.error(exp.toString());
        }
    }


    public void clearPreviousSessionData(){
        appData.deleteData(AppConstants.SESSION_ID);
        appData.deleteData(AppConstants.SERVER_ID);
        appData.deleteData(AppConstants.IS_SESSION_READY);
        appData.deleteData(AppConstants.MATCH_SELECTED);
    }

    public class MessageHandler {
        public void handleSessionMessage(JSONObject msg) throws JSONException {
            if(appData != null){
                appData = ChatAppData.getInstance(mContext);
                appData.printAllData();
            }
            String state = msg.getString("state");
            if(state.equals("flush")){
                appData.putString(AppConstants.SESSION_ID, msg.getString(AppConstants.SESSION_ID));
                appData.putString(AppConstants.SERVER_ID, msg.getString(AppConstants.SERVER_ID));
                appData.putBoolean(AppConstants.IS_SESSION_READY, false);
            }else if(state.equals("ready")){
                if(!appData.getString(AppConstants.SESSION_ID).equals(msg.getString(AppConstants.SESSION_ID))) {
                    appData.putString(AppConstants.SESSION_ID, msg.getString(AppConstants.SESSION_ID));
                }
                if(!appData.getString(AppConstants.SERVER_ID).equals(msg.getString(AppConstants.SERVER_ID))) {
                    appData.putString(AppConstants.SERVER_ID, msg.getString(AppConstants.SERVER_ID));
                }
                appData.putBoolean(AppConstants.IS_SESSION_READY, true);
            }else{
                logger.info(" The message does not contain a state variable");
            }
        }

        public void handleStatusMessage(JSONObject msg) throws JSONException{
            appData.putBoolean(AppConstants.IS_PREMIUM, msg.getBoolean(AppConstants.IS_PREMIUM));
            appData.putString(AppConstants.ALGOS, (msg.get(AppConstants.ALGOS).toString()));
        }

        public void handleLeaveMessage(JSONObject msg){
            appData.deleteData(AppConstants.CHAT_ID);
            appData.deleteData(AppConstants.FRIEND_USER_ID);
            sendBroadcast(msg, AppConstants.MSG_TYPE_LEAVE);
        }

        public void handleMatchedMessage(JSONObject msg) throws JSONException {
            appData.putString(AppConstants.CHAT_ID, msg.getString(AppConstants.CHAT_ID));
            sendBroadcast(msg, AppConstants.MSG_TYPE_MATCHED);
        }

        public void handleSyncMessage(JSONObject msg) throws JSONException{
            String friendUdid = msg.getJSONObject("data").getJSONObject("data").getString("udid");
            appData.putString(AppConstants.FRIEND_USER_ID, friendUdid);
        }

        public void handleChatMessage(JSONObject msg) throws JSONException{
            sendBroadcast(msg, AppConstants.MSG_TYPE_CHAT);
        }

        public void handleAckMessage(String msgType, JSONObject msg) throws JSONException{
            sendBroadcast(msg, msgType);
        }
        private void sendBroadcast(JSONObject message, String messageType){
            try {
                Intent broadcastIntent = new Intent();
                message.put("MsgType", messageType);
                broadcastIntent.setAction(AppConstants.BROADCAST_ACTION);
                broadcastIntent.putExtra(AppConstants.BROADCAST_DATA, message.toString());
                mContext.sendBroadcast(broadcastIntent);
            }catch (JSONException exp){
                logger.error(" ");
            }
        }
    }
    public void sendAck(JSONObject message){
        try{
            //it is messed up and random here so do not use it for reference
            JSONObject ackMessage = new JSONObject();
            ackMessage.put(AppConstants.SERVERMSG_CHAT_ID, message.getString(AppConstants.SERVERMSG_TIMESTAMP));
            ackMessage.put(AppConstants.SERVERMSG_MSGTYPE_MESSAGE_REF, message.get(AppConstants.SERVERMSG_CHAT_ID));
            String status = AppConstants.SERVERMSG_STATUS_RECEIVED;
            if(appData.getBoolean(AppConstants.ACTIVTIY_FOREGROUND)) {
                status = AppConstants.SERVERMSG_STATUS_READ;
            }else{
                addWaitingStateMessage(ackMessage);
            }
            ackMessage.put(AppConstants.SERVERMSG_MSGTYPE_MESSAGE_STATUS, status);
            SocketListener.getInstance().sendMessageToServer(AppConstants.MSG_TYPE_STATUS, ackMessage);
        }catch (JSONException exp){
            logger.error("Error while forming ack message");
            logger.error(exp.toString());
        }
    }

    public void sendLeaveMessage(){
        try{
            JSONObject message = new JSONObject();
            message.put(AppConstants.SERVERMSG_ACTUAL_CHATID, appData.getString(AppConstants.CHAT_ID));
            SocketListener.getInstance().sendMessageToServer(AppConstants.MSG_TYPE_LEAVE, message);
        }catch (Exception exp){
            logger.error(" Error while sending the leave message");
            logger.error(exp.toString());
        }
    }

    public void addWaitingStateMessage(JSONObject ackMessage) throws JSONException{
        String waitingData = appData.getString(AppConstants.WAITING_MESSAGE_LIST);
        if(waitingData.isEmpty()){
            ackMessage.put(AppConstants.SERVERMSG_MSGTYPE_MESSAGE_STATUS, AppConstants.SERVERMSG_STATUS_RECEIVED);
            JSONArray msgs = new JSONArray();
            msgs.put(ackMessage);
            waitingData = msgs.toString();
        }else{
            JSONArray dataArray = new JSONArray(waitingData);
            logger.info("addWaitingStateMessage");
            logger.info(dataArray.toString());
            dataArray.put(ackMessage);
            logger.info(dataArray.toString());
            waitingData = ackMessage.toString();
        }
        appData.putString(AppConstants.WAITING_MESSAGE_LIST, waitingData);
    }

    public void handleWaitingMessages(){
        String waitingData = appData.getString(AppConstants.WAITING_MESSAGE_LIST);
        try{
            if(!waitingData.isEmpty()) {
                JSONArray dataArray = new JSONArray(waitingData);
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject data = dataArray.getJSONObject(i);
                    data.put(AppConstants.SERVERMSG_MSGTYPE_MESSAGE_STATUS, AppConstants.SERVERMSG_STATUS_READ);
                    SocketListener.getInstance().sendMessageToServer(AppConstants.MSG_TYPE_STATUS, data);
                }
            }
        }catch (Exception xp){
            logger.error(" Error while handling waiting messages");
            logger.error(xp.toString());
        }finally {
            appData.deleteData(AppConstants.WAITING_MESSAGE_LIST);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
