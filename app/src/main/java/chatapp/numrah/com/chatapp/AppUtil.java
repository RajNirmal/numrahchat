package chatapp.numrah.com.chatapp;

import android.content.Context;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppUtil {
    public static AppUtil util = null;
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
        Thread thread1 = new Thread(new Thread1());
        thread1.start();
        return "";
    }
    class Thread1 implements Runnable {
        @Override
        public void run() {
            String sessionId = getAlphaNumericString(AppConstants.uuidSize);
            String udid = getId(mContext);
            String token = getToken(sessionId, udid);
            SocketListener.getInstance(sessionId,udid, token);
        }
    }

    private String getId(Context context) {
        String id = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return id;
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
}
