package chatapp.numrah.com.chatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Numrah";
    private static final String TABLE_NAME = "ChatDetails";
    private static final String KEY = "KeySet";
    private static final String VALUE = "Value";
    private AppLogger logger;

    public DataHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        logger = new AppLogger();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createQuery = "CREATE TABLE "+ TABLE_NAME + "( "+ KEY + " TEXT UNIQUE," + VALUE+ " INTEGER)";
        sqLiteDatabase.execSQL(createQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    protected long insertData(String key,String value){
        logger.info("Inserting data in to the table");
        logger.info("Key : "+key+ " Value : "+value);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY,key);
        contentValues.put(VALUE,value);
        SQLiteDatabase db = this.getWritableDatabase();
        long output;
        if(doesKeyExist(key)) {
            logger.info(" key already present, so updating it");
            output = db.update(TABLE_NAME, contentValues, KEY + "=?", new String[]{key});
            logger.info(" The no of rows affected is " + output);
            db.close();
        }else {
            output = db.insert(TABLE_NAME, null, contentValues);

        }
        return output;
    }

    protected boolean deleteData(String key){
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Integer cr = sqLiteDatabase.delete(TABLE_NAME, KEY + "=?", new String[]{key});
            if (cr == 1) {
                return true;
            } else {
                return false;
            }
        }catch (Exception exp){
            logger.error(" Error while deleting the keys");
            logger.error(exp.toString());
            return false;
        }
    }

    protected boolean doesKeyExist(String key){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cr = sqLiteDatabase.query(TABLE_NAME, null, KEY + "=?", new String[] {key}, null, null, null, null);
        if(cr.getCount() == 0){
            return false;
        }else{
            return true;
        }
    }

    protected String getData(String key){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cr = sqLiteDatabase.query(TABLE_NAME, null, KEY + "=?", new String[] {key}, null, null, null, null);
        cr.moveToFirst();
        String result = cr.getString(1);
        cr.close();
        return result;
    }

    protected String getData(){
        String selectQuery = "select * from "+TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cr = sqLiteDatabase.rawQuery(selectQuery,null);
        int count = cr.getCount();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        cr.moveToFirst();
        while(i<count){
            sb.append("Name : "+cr.getString(0)+" ;  ID : "+cr.getString(1)+"\n");
            cr.moveToNext();
            i++;
        }
        cr.close();
        return sb.toString();
    }

}
