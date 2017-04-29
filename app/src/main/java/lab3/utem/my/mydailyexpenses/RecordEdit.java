package lab3.utem.my.mydailyexpenses;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecordEdit extends AppCompatActivity {

    String id, name, price, date;

    TextView vwId;
    EditText vwExpName, vwExpPrice, vwExpDate;
    Button btnSave;

    ExpensesDB dbMyExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_edit);

        vwId = (TextView) findViewById(R.id.editId);
        vwExpName = (EditText) findViewById(R.id.editName);
        vwExpPrice = (EditText) findViewById(R.id.editPrice);
        vwExpDate = (EditText) findViewById(R.id.editDate);
        btnSave = (Button) findViewById(R.id.btnSave);

        id = getIntent().getStringExtra("ID_NUM");
        name = getIntent().getStringExtra("NAME");
        price = getIntent().getStringExtra("PRICE");
        date = getIntent().getStringExtra("DATE");

        vwId.setText(id);
        vwExpName.setText(name);
        vwExpPrice.setText(price);
        vwExpDate.setText(date);

        dbMyExpense = new ExpensesDB(getApplicationContext());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ContentValues cv = new ContentValues();
                            cv.put(ExpensesDB.colExpName, vwExpName.getText().toString());
                            cv.put(ExpensesDB.colExpPrice, vwExpPrice.getText().toString());
                            cv.put(ExpensesDB.colExpDate, vwExpDate.getText().toString());

                            dbMyExpense.update(cv, vwId.getText().toString());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Data succesfully saved!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        } catch (Exception ex) {
                            Log.d("ERROR-DB", ex.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Error, data not saved!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                };
                Thread thread = new Thread(task);
                thread.start();
            }
        });
    }
}
