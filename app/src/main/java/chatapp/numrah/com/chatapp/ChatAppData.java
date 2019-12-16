package chatapp.numrah.com.chatapp;

import android.content.Context;

public class ChatAppData extends DataHandler {

    AppLogger logger;

    public static ChatAppData appData;

    public ChatAppData(Context context){
         super(context);
         logger = new AppLogger();
    }

    public static ChatAppData getInstance(Context context){
        if(appData == null){
            appData = new ChatAppData(context);
        }
        return appData;
    }

    public void remove(String key){
        deleteData(key);
    }

    public void putString(String key, String value){
        insertData(key, value);
    }

    public void putInt(String key, int value){
        insertData(key, String.valueOf(value));
    }

    public void putBoolean(String key, boolean value){
        insertData(key, String.valueOf(value));
    }

    public Integer getInt(String key){
        Integer a = -1;
        try{
            a = Integer.valueOf(getData(key));
        }catch (Exception exp){
            logger.error(exp.toString());
        }
        return a;
    }

    public String getString(String key){
        String a = "";
        try{
            a = getData(key);
        }catch (Exception exp){
            logger.error(exp.toString());
        }
        return a;
    }

    public Boolean getBoolean(String key){
        Boolean a = false;
        try{
            a = Boolean.valueOf(getData(key));
        }catch (Exception exp){
            logger.error(exp.toString());
        }
        return a;
    }

    public void printAllData(){
        logger.info(" Printing all data in the DB");
        logger.info(super.getData());
    }
}
