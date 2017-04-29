package lab3.utem.my.mydailyexpenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ExpensesDB extends SQLiteOpenHelper {

    public static final String dbName = "dbMyExpense";
    public static final String tblName = "expenses";
    public static final String colExpName = "exp_name";
    public static final String colExpPrice = "exp_price";
    public static final String colExpDate = "exp_date";
    public static final String colExpId = "exp_id";

    public ExpensesDB(Context context) {
        super(context, dbName, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS expenses(exp_id VARCHAR, exp_name VARCHAR," +
                "exp_price VARCHAR, exp_date DATE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        onCreate(db);
    }

    public Cursor getDataById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curr = db.rawQuery("SELECT * FROM " + tblName + " WHERE " +
        colExpId + "= " + id, null);

        return curr;
    }

    public void update(ContentValues cv, String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.update(tblName, cv, "exp_id=" + id, null);
    }

    public void fnExecuteSql(String sql, Context context) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(sql);
        } catch (Exception ex) {
            Log.d("fnExecuteSQL", ex.getMessage());
        }
    }

    public int fnTotalRow() {
        int intRow;
        SQLiteDatabase db = this.getReadableDatabase();
        return intRow = (int) DatabaseUtils.queryNumEntries(db, tblName);
    }
}
