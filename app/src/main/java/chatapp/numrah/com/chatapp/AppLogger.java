package chatapp.numrah.com.chatapp;

import android.util.Log;

public class AppLogger  {
    public String LOG_TAG = "NumrahChatApp";

    public void info(String s){
        Log.i(LOG_TAG,s);
    }

    public void debug(String s){
        Log.d(LOG_TAG,s);
    }

    public void error(String s){
        Log.e(LOG_TAG,s);
    }
}
