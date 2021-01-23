package soumya.myaccountmanager;

/**
 * Created by soumy on 4/6/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

public class DBHelper extends SQLiteOpenHelper {

    int userid;
    String sqlquery;

    public static final String DATABASE_NAME = "test1.db";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // table: user
        db.execSQL(
                "CREATE TABLE user"+
                        "(name varchar(20),username varchar(10) unique not null,password varchar(10) unique not null,"+
                        "user_id integer primary key autoincrement)"
        );
        // table: account
        db.execSQL(
                "CREATE TABLE account(a_u_id integer,acc_id text,bank_name text,"
                        + "types varchar(20), acc_name varchar(20),"
                        + " amount integer,last_trans_date varchar(10),"
                        +"  primary key(acc_id),"
                        + "foreign key(a_u_id) references user(user_id) on delete cascade)"

        );
        // table: expense
        db.execSQL(
                "CREATE TABLE expense(payee varchar(20),category_name varchar(30),"+
                        "payment_mode varchar(30) check(payment_mode='credit card' or payment_mode='cash' or " +
                        "payment_mode='bank' or payment_mode='debit card'),date varchar(10),amount integer,accnt_id varchar(20),"+
                        "description varchar(40), e_u_id integer,"+
                        "primary key(date,category_name,payment_mode,accnt_id),"+
                        "foreign key(e_u_id) references user(user_id) on delete cascade,"+
                        "foreign key(accnt_id) references account on delete cascade)"

        );
        // table: todo_list
        db.execSQL(
                "CREATE TABLE to_do_list(t_u_id integer,todo_id integer autoincrement,t_dates varchar(10),t_time varchar(5),"+
                        "description varchar(40),"+
                        "primary key(todo_id),"+
                        "foreign key(t_u_id) references user(user_id) on delete cascade)"

        );

        // table: financial_goals
        db.execSQL(
                "CREATE TABLE financial_goals(f_u_id integer,priority_date varchar(10),starting_date varchar(10),"+
                        "savings numeric(12,2),"+
                        "description varchar(40),f_acc_id varchar(20),"+
                        "primary key(u_id,starting_date,priority_date),"+
                        "foreign key(f_u_id) references user(user_id) on delete cascade,"+
                        "foreign key(f_acc_id) references account on delete cascade)"

        );
        // table: budget
        db.execSQL(
                "CREATE TABLE budget(b_u_id integer,b_year numeric(4,0)"+
                        "check(b_year>2015 and b_year<2100),b_month numeric(2,0) check(b_month>00 and b_month<13),"+
                        "set_amount integer,spent_amount integer,"+
                        "primary key(b_u_id,b_year,b_month),"+
                        "foreign key(b_u_id) references user(user_id) on delete cascade)"


        );
        // table: depends
        db.execSQL(
                "CREATE TABLE depends(d_u_id integer,d_year numeric(4,0),d_month numeric(2,0)," +
                        " d_date varchar(10),d_categoryname varchar(30),d_paymentmode varchar(30),d_acc_id varchar(20)," +
                        "primary key(d_u_id,d_year,d_month,d_date,d_categoryname,d_paymentmode,d_acc_id)," +
                        "foreign key(d_u_id,d_year,d_month) references budget  on delete cascade," +
                        "foreign key(d_date,d_categoryname,d_paymentmode,d_acc_id) references expense on delete cascade)"

        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS account");
        db.execSQL("DROP TABLE IF EXISTS expense");
        db.execSQL("DROP TABLE IF EXISTS to_do_list");
        db.execSQL("DROP TABLE IF EXISTS financial_goals");
        db.execSQL("DROP TABLE IF EXISTS budget");
        db.execSQL("DROP TABLE IF EXISTS depends");
        onCreate(db);
    }

    public boolean usernameExists(String un){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =null;
        boolean check=false;
        try {
            sqlquery = "SELECT Count(*) FROM user WHERE username = '" + un + "';";
            cursor = db.rawQuery (sqlquery, null);
            cursor.moveToFirst();
            int count=cursor.getInt(0);
            if(count>0)
                check=true;
        }
        catch (SQLiteException ex){}
        return check;
    }


    // insert new user
    public void insertNewUser(String name, String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery = "INSERT INTO " +
                    "user " +
                    "(name,username,password)" +
                    "VALUES('" + name + "','" + username + "','" + password + "');";
            db.execSQL(sqlquery);
        }
        catch (SQLiteException ex){}
    }

    // verify login
    public boolean verifyLogin(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =null;
        boolean iscorrect=false;
        try{
            cursor = db.rawQuery("SELECT COUNT(*) FROM user WHERE username=? AND password=?", new String[]{username,password});
            cursor.moveToFirst();
            if(cursor.getInt(0)>0){
                getUserId(username);
                iscorrect=true;
            }

        }
        catch(SQLiteException ex){}
        return iscorrect;
    }

    private void getUserId(String username){
        int id = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor =null;
            sqlquery = "SELECT user_id FROM user WHERE username = '" + username + "';";
            cursor = db.rawQuery (sqlquery, null);
            cursor.moveToFirst();
            String value = cursor.getString (cursor.getColumnIndex("user_id"));
            id = Integer.parseInt(value);
        }
        catch (SQLiteException ex){}
        userid=id;
    }

    // insert new account
    public void insertNewAccount(String bank,String type,String accname,int amount,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery = "INSERT INTO " +
                    "account_details " +
                    "(bank_name,types,acc_name,amount)" +
                    "VALUES('" + bank + "','" + type + "','" + accname + "','" + amount + "','" + date + "');";
            db.execSQL (sqlquery);
        }
        catch (SQLiteException ex){}
    }


    // insert new expense
    public void insertNewExpense(String date,String category,String paymentmode,String account,String payee,int amount,String desc ){
        // get acc_id from acc_name
        String accid = getAccId(account);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery = "INSERT INTO " +
                    "expense " +
                    "(payee,category_name,payment_mode,date,amount,accnt_id,description)" +
                    "VALUES('" + payee + "','" + category + "','" + paymentmode + "','" + date +"','"  + amount +"','"  + accid + "','" + desc + "');";
            db.execSQL (sqlquery);
        }
        catch (SQLiteException ex){}
    }

    // insert new fin_goal pdate,sdate,savings,description
    public void insertNewFinGoal(String pdate,String sdate,int savings,String description ){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery = "INSERT INTO " +
                    "financial_goals " +
                    "(priority_date,starting_date,savings,description)" +
                    "VALUES('" + pdate + "','" + sdate + "','" + savings + "','" + description + "');";
            db.execSQL (sqlquery);
        }
        catch (SQLiteException ex){}
    }

    // insert new todo
    public void insertNewTodo(String date, String time, String description){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery = "INSERT INTO " +
                    "todo_list " +
                    "(t_date,t_time,description)" +
                    "VALUES('" + date + "','" + time + "','" + description + "');";
            db.execSQL (sqlquery);
        }
        catch (SQLiteException ex){}
    }


    public String getAccId(String name){
        String id = "";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT acc_id FROM account WHERE acc_name = '"+name+"';";
            Cursor cursor = db.rawQuery(sqlquery, null);
            cursor.moveToFirst();
            id = cursor.getString(cursor.getColumnIndex("acc_id"));
        }
        catch (SQLiteException ex){}
        return id;
    }


    // get name. accid, bank, balance, lasttd
    public Cursor getDebit(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT acc_name,acc_id,bank_name,amount,last_trans_date FROM account WHERE types = 'Debit Card';";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}
        return cursor;
    }

    // get name. accid, bank, spent, lasttd
    public Cursor getCredit(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT acc_name,acc_id,bank_name,amount,last_trans_date FROM account WHERE types = 'Credit Card';";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}

        return cursor;
    }

    // get desc,cat,amount, paymode, payee, date,acc
    public Cursor getExpense(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT description,category_name,amount,payment_mode,payee,date,acc_name FROM expense INNER JOIN account_details ON expense.accnt_id=account_details.acc_id ORDER BY amount DESC;";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}

        return cursor;
    }

    // get pdate, sdate, desc, savings
    public Cursor getFinGoals(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT priority_date,starting_date,description,savings FROM financial_goals ORDER BY starting_date ASC limit"+ "10;";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}

        return cursor;
    }
    /////////////////////////////////////////////////////////////
    // get month, saved, spent
    public Cursor getHistory(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT priority_date,starting_date,description,savings FROM financial_goals ORDER BY starting_date ASC limit"+" 10;";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}

        return cursor;
    }

    // get date, time, desc
    public Cursor getToDoList(){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT date,time,description FROM todo_list ORDER BY date,time ASC;";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}

        return cursor;
    }

    // budget table
    public int totalAmountSaved(){
        int total=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor =null;
            sqlquery = "SELECT sum(set_amount) FROM budget;";
            cursor = db.rawQuery (sqlquery, null);
            cursor.moveToFirst();
            total=cursor.getInt(0);
        }
        catch (SQLiteException ex){}

        return total-totalAmountSpent();
    }

    // budget table
    public int totalAmountSpent(){
        int total=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor =null;
            sqlquery = "SELECT sum(spent_amount) FROM budget;";
            cursor = db.rawQuery (sqlquery, null);
            cursor.moveToFirst();
            total=cursor.getInt(0);

        }
        catch (SQLiteException ex){}

        return total;
    }
    //update account table
    public void updateAccount_Debit(integer user_id,String date,String acc_id,String pmode,String cname){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery ="UPDATE ACCOUNT SET amount=amount-(select amount from expense where e_u_id="+user_id +" and
                       +"accnt_id="+acc_id +" 
                       +"and dates="+date+" and payment_mode="+pmode+" and category_name="+cname+") where a_u_id="+user_id+";";
            db.execSQL (sqlquery);
            sqlquery ="UPDATE ACCOUNT SET last_trans_date=(select date from expense where e_u_id="+user_id +" and
                       +"accnt_id="+acc_id +" 
                       +"and dates="+date+" and payment_mode="+pmode+" and category_name="+cname+") where a_u_id="+user_id+";";
            db.execSQL (sqlquery);
        }
        catch (SQLiteException ex){}
    }
    
    public void updateAccount_Credit(int user_id,String date,String acc_id,String pmode,String cname){

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            sqlquery ="UPDATE ACCOUNT SET amount=amount+(select amount from expense where e_u_id="+user_id +" and
                       +"accnt_id="+acc_id +" 
                       +"and dates="+date+" and payment_mode="+pmode+" and category_name="+cname+") where a_u_id="+user_id+";";
            db.execSQL (sqlquery);
            sqlquery ="UPDATE ACCOUNT SET last_trans_date=(select date from expense where e_u_id="+user_id +" and
                       +"accnt_id="+acc_id +" 
                       +"and dates="+date+" and payment_mode="+pmode+" and category_name="+cname+") where a_u_id="+user_id+";";
            db.execSQL (sqlquery);
        }
        catch (SQLiteException ex){}
    }

   //get amount details based on each category in desc order
   public Cursor get_Category(int user_id,String date1,String date2){
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            sqlquery = "SELECT category_name, sum(amount) as spent from expense where date between" + date1 + " and "+date2+ 
                       " and e_u_id="+user_id+" group by category_name order by sum(amount) desc;";
            cursor = db.rawQuery(sqlquery, null);
        }
        catch (SQLiteException ex){}

        return cursor;
    }


  

}
