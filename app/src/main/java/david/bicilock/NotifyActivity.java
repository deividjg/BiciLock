package david.bicilock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NotifyActivity extends AppCompatActivity {

    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private JSONArray jSONArrayUsers;
    private User user;
    private TextView tvNameNotify, tvPhoneNotify;
    private EditText etNotify;
    private String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvNameNotify = (TextView)findViewById(R.id.tvNameNotify);
        tvPhoneNotify = (TextView)findViewById(R.id.tvPhoneNotify);
        etNotify = (EditText) findViewById(R.id.editTextNotify);

        getSerialNumber();
        returnJSON = new ReturnJSON();
        new GetUserDataTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sendNotify) {
            sendEmail(user.getEmail(), etNotify.getText().toString());
            Toast.makeText(this, R.string.mail_sent, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (id == R.id.cancelNotify) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void sendEmail(String address, String message){
        new MailJob("bicilock.info", "677393677").execute(
                new MailJob.Mail(address, address, getString(R.string.notice), message)
        );
    }

    protected void prepareScreen() {
        tvNameNotify.setText(user.getName());
        tvPhoneNotify.setText(user.getPhone());
    }

    protected void getSerialNumber() {
        serialNumber = getIntent().getStringExtra("serialNumber");
    }

    ///////Task for get user's data
    class GetUserDataTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NotifyActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM users, bikes WHERE SerialNumber='" + serialNumber + "'");

                jSONArrayUsers = returnJSON.sendRequest(Parameters.URL_DOWNLOAD, parametrosPost);

                if (jSONArrayUsers != null) {
                    return jSONArrayUsers;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        user = new User();
                        user.setEmail(jsonObject.getString("email"));
                        user.setName(jsonObject.getString("Name"));
                        user.setPhone(jsonObject.getString("Phone"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    prepareScreen();
                }
            } else {
                Toast.makeText(NotifyActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
