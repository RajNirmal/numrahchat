package chatapp.numrah.com.chatapp;

import android.content.Context;
import android.content.Intent;
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
        clearPreviousSessionData();
        String sessionId = getAlphaNumericString(AppConstants.uuidSize);
        String udid = getId(mContext);
        String token = getToken(sessionId, udid);
        SocketListener.getInstance(sessionId,udid, token);
        return "";
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
                default:
                    logger.info(" This message type is not handled");
                    break;

            }
        }catch (Exception exp){
            logger.error(" Error while processing hanldeMessage");
            logger.error(exp.toString());
        }
    }
    private void clearPreviousSessionData(){
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

        }

        public void handleMatchedMessage(JSONObject msg) throws JSONException {
            appData.putString(AppConstants.CHAT_ID, msg.getString(AppConstants.CHAT_ID));
            sendMatchedBroadcast(msg, AppConstants.MSG_TYPE_MATCHED);
        }

        public void handleSyncMessage(JSONObject msg) throws JSONException{
            String friendUdid = msg.getJSONObject("data").getJSONObject("data").getString("udid");
            appData.putString(AppConstants.FRIEND_USER_ID, friendUdid);
        }

        private void sendMatchedBroadcast(JSONObject message, String messageType){
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
}
