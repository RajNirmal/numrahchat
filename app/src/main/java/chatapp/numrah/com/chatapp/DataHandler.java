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
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY,key);
        contentValues.put(VALUE,value);
        SQLiteDatabase db = this.getWritableDatabase();
        long output = db.insert(TABLE_NAME,null,contentValues);
        if(output == -1){
            logger.info(" An error occured while inserting the new data so updating it");
            int out2 = db.update(TABLE_NAME,  contentValues, KEY + "=?", new String[]{key});
            logger.info(" The no of rows affected is "+ out2);
        }else{
            logger.info(" The output is "+output);
        }
        db.close();
        return output;
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
