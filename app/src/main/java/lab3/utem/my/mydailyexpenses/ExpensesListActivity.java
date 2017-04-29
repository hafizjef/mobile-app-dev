package lab3.utem.my.mydailyexpenses;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Logger;

public class ExpensesListActivity extends AppCompatActivity {

    SQLiteDatabase dbMyExpenses;
    String strDate,strExpName,strExpenses ="" ;
    Double strPrice,totalSpending=0.0;
    TextView totalValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);

        totalValue = (TextView) findViewById(R.id.totalVal);

        Runnable run = new Runnable() {
            @Override
            public void run() {

                try {
                    dbMyExpenses = openOrCreateDatabase("dbMyExpense", MODE_PRIVATE, null);
                    Cursor resultSet = dbMyExpenses.rawQuery("SELECT * FROM expenses;", null);

                    if (resultSet.moveToFirst()) {
                        do {
                            strExpName = resultSet.getString(resultSet.getColumnIndex("exp_name"));
                            strPrice = resultSet.getDouble(resultSet.getColumnIndex("exp_price"));
                            strDate = resultSet.getString(resultSet.getColumnIndex("exp_date"));
                            strExpenses += "Item [ " + strExpName + " ] RM [ " + strPrice + " ] Date [ " + strDate + " ]\n";
                            totalSpending += strPrice;
                        } while (resultSet.moveToNext());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView txtVwExpenses = (TextView) findViewById(R.id.txtViewExp);
                            txtVwExpenses.setText(strExpenses);
                            totalValue.setText("MYR" + totalSpending.toString());
                        }
                    });
                } catch (Exception ex) {
                    Log.e("Activity", ex.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error, Database Uninitialized", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        };

        Thread viewExp = new Thread(run);
        viewExp.start();
    }
}
