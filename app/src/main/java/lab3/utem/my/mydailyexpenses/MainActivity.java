package lab3.utem.my.mydailyexpenses;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText expName, expPrice, expDate, expTime;
    //Calendar myCalendar;
    //SQLiteDatabase dbMyExpenses;


    WebServiceCall wsc = new WebServiceCall();
    String strDate, strTime, strMsg;
    ExpensesDB dbMyExpenses;
    JSONObject jsonObject = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        dbMyExpenses = new ExpensesDB(getApplicationContext());

        expName = (EditText) findViewById(R.id.expName);
        expPrice = (EditText) findViewById(R.id.expPrice);
        expDate = (EditText) findViewById(R.id.expDate);
        expTime = (EditText) findViewById(R.id.edtTime);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        expDate.setText(sdf.format(new Date()));


        /////
        expDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        expDate.setText(selectedday + "/" + (selectedmonth+1) + "/" + selectedyear);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();  }
        });

        ////

        Runnable run = new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("selectFn", "fnGetDateTime"));

                try {
                    jsonObject = wsc.makeHTTPRequest(wsc.fnGetURL(), "POST", params);
                    strDate = jsonObject.getString("currDate");
                    strTime = jsonObject.getString("currTime");
                    strMsg = "Successfully retrieve data & time from server!";
                } catch (Exception ex) {
                    Log.d("ERR", ex.getMessage());
                    strMsg = "Unable to connect to server.. Getting mobile date and time";
                    strDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    strTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fnDisplayToastMsg(strMsg);
                        expDate.setText(strDate);
                        expTime.setText(strTime);
                    }
                });
            }
        };

        Thread thrd = new Thread(run);
        thrd.start();

    }

    public void fnDisplayToastMsg(String strText) {
        Toast toast = Toast.makeText(getApplicationContext(), strText, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewExp:
                fnActivExpList(this.getCurrentFocus());
                return true;

            case R.id.listViewExpense:
                fnListViewExp(this.getCurrentFocus());
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void fnListViewExp(View currentFocus) {
        Intent intent = new Intent(this, ListViewExpensesActivity.class);
        startActivityForResult(intent, 0);
    }

    public void fnActivExpList(View vw) {
        Intent intent = new Intent(this, ExpensesListActivity.class);
        startActivityForResult(intent, 0);
    }

    public void fnSave(View vw) {

        Runnable run = new Runnable() {
            @Override
            public void run() {
                String strRespond = "";

                if (expName.getText().toString().matches("") || expDate.getText().toString().matches("") || expPrice.getText().toString().matches("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please fill in required input",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    String strExpName = expName.getText().toString();
                    Double strPrice = Double.parseDouble(expPrice.getText().toString());
                    String strDate = expDate.getText().toString();
                    String timeStr = expTime.getText().toString();

                    int intNewId = dbMyExpenses.fnTotalRow() + 1;
                    String strQry = "INSERT INTO expenses values('" +
                            intNewId + "', '" + strExpName + "', '" + strPrice + "', '" + strDate + "');";

                    dbMyExpenses.fnExecuteSql(strQry, getApplicationContext());

                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("selectFn", "fnAddExpense"));
                    params.add(new BasicNameValuePair("varExpName", strExpName));
                    params.add(new BasicNameValuePair("varExpPrice", strPrice.toString()));
                    params.add(new BasicNameValuePair("varMobileDate", strDate));
                    params.add(new BasicNameValuePair("varMobileTime", timeStr));

                    try {
                        jsonObject = wsc.makeHTTPRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsonObject.getString("respond");
                    } catch (Exception ex) {
                        Log.d("JSON Call Err", ex.getMessage());
                    }

                    final String finalStrRespond = strRespond;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast showSuccess = Toast.makeText(getApplicationContext(), "Information successfully saved" + finalStrRespond,
                                    Toast.LENGTH_SHORT);
                            showSuccess.show();
                        }
                    });
                }
            }
        };

        Thread thrSave = new Thread(run);
        thrSave.start();
    }
}
