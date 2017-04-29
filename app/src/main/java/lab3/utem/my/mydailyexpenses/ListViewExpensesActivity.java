package lab3.utem.my.mydailyexpenses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewExpensesActivity extends AppCompatActivity {

    private static final String strExpId = ExpensesDB.colExpId;
    private static final String strExpName = ExpensesDB.colExpName;
    private static final String strExpPrice = ExpensesDB.colExpPrice;
    private static final String strExpDate = ExpensesDB.colExpDate;

    ExpensesDB dbExpense;
    ListView lvExp;
    ArrayList<HashMap<String, String>> allExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_expenses);

        dbExpense = new ExpensesDB(getApplicationContext());
        lvExp = (ListView) findViewById(R.id.lstVwExpense);
        allExp = new ArrayList<>();

        Runnable runner = new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + ExpensesDB.tblName;
                Cursor curr = dbExpense.getReadableDatabase().rawQuery(sql, null);

                while (curr.moveToNext()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(strExpId, curr.getString(curr.getColumnIndex(ExpensesDB.colExpId)));
                    map.put(strExpName, curr.getString(curr.getColumnIndex("exp_name")));
                    map.put(strExpPrice, curr.getString(curr.getColumnIndex("exp_price")));
                    map.put(strExpDate, curr.getString(curr.getColumnIndex("exp_date")));

                    allExp.add(map);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ListAdapter adapter = new SimpleAdapter(ListViewExpensesActivity.this, allExp,
                                R.layout.listviewexpenseinfo, new String[]{strExpId, strExpName, strExpPrice, strExpDate},
                                new int[]{R.id.txtId, R.id.txtExpName, R.id.txtExpPrice, R.id.txtDate});

                        lvExp.setAdapter(adapter);
                        lvExp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView a = (TextView) view.findViewById(R.id.txtExpName);
                                TextView b = (TextView) view.findViewById(R.id.txtId);
                                TextView c = (TextView) view.findViewById(R.id.txtExpPrice);
                                TextView d = (TextView) view.findViewById(R.id.txtDate);

                                String expName = a.getText().toString();
                                String expId = b.getText().toString();
                                String expPrice = c.getText().toString();
                                String expDate = d.getText().toString();

                                Intent intent = new Intent(getBaseContext(), RecordEdit.class);
                                intent.putExtra("ID_NUM", expId);
                                intent.putExtra("NAME", expName);
                                intent.putExtra("PRICE", expPrice);
                                intent.putExtra("DATE", expDate);

                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }
}
