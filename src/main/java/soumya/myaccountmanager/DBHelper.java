package soumya.myaccountmanager;

/**
 * Created by soumy on 4/6/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyAccount.db";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // table: user
        db.execSQL(
                "create table user " +
                        "(id integer primary key, name text,username text,email text, password text)"
        );
        // table: expense
        db.execSQL(
                "create table expense " +
                        "(id integer primary key, date integer,description text, quantity text, category text, account_id integer, payee text, amount integer)"
        );
        // table: todo_list
        db.execSQL(
                "create table todo_list " +
                        "(id integer primary key, description text, date integer,time integer)"
        );
        // table: bill
        db.execSQL(
                "create table bill " +
                        "(id integer primary key, payee text, due_date integer, paid integer)"
        );
        // table: account_details
        db.execSQL(
                "create table account_details " +
                        "(id integer primary key,name text, bank_name text, type text,amount integer, last_transaction_date integer)"
        );
        // table: financial_goals
        db.execSQL(
                "create table bill " +
                        "(id integer primary key, description text, start_date integer, priority_date integer, savings integer)"
        );
        // table: budget
        db.execSQL(
                "create table bill " +
                        "(id integer primary key, month integer, year integer, amount integer, spent integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS expense");
        db.execSQL("DROP TABLE IF EXISTS todo_list");
        db.execSQL("DROP TABLE IF EXISTS bill");
        db.execSQL("DROP TABLE IF EXISTS account_details");
        db.execSQL("DROP TABLE IF EXISTS financial_goals");
        db.execSQL("DROP TABLE IF EXISTS budget");
        onCreate(db);
    }
/*
    public boolean insertContact  (String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }
*/
}
