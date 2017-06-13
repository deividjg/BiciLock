package david.bicilock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String url_upload;
    protected JSONObject jsonObject;
    private ReturnJSON devuelveJSON;
    private EditText etEMail;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEMail = (EditText) findViewById(R.id.etEMail);
        url_upload = "http://iesayala.ddns.net/deividjg/php2.php";
        devuelveJSON = new ReturnJSON();
    }

    public void registerUser(View view) {
        email = etEMail.getText().toString();
        new RegistroTask().execute();
    }

    public void cancel(View view) {
        finish();
    }

    ///////Task to registerUser a new user
    class RegistroTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage(R.string.charging + "");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "INSERT INTO users (`email`, `Password`, `Name`, `Town`, `Province`, `Phone`) VALUES ('" + email + "','0','0','0','0','')");

                jsonObject = devuelveJSON.sendDMLRequest(url_upload, parametrosPost);

                if (jsonObject != null) {
                    return jsonObject;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {
                try {
                    add = json.getInt("added");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (add != 0) {
                    Toast.makeText(RegisterActivity.this, R.string.new_user_ok,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.new_bike_error,
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(RegisterActivity.this, R.string.charging_error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
