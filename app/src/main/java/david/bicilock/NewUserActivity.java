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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserActivity extends AppCompatActivity {

    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private EditText etEMailNewUser, etPasswordNewUser, etNameNewUser, etTownNewUser, etProvinceNewUser, etPhoneNewUser;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        etEMailNewUser = (EditText) findViewById(R.id.etEMailNewUser);
        etPasswordNewUser = (EditText) findViewById(R.id.etPasswordNewUser);
        etNameNewUser = (EditText) findViewById(R.id.etNameNewUser);
        etTownNewUser = (EditText) findViewById(R.id.etTownNewUser);
        etProvinceNewUser = (EditText) findViewById(R.id.etProvinceNewUser);
        etPhoneNewUser = (EditText) findViewById(R.id.etPhoneNewUser);
        returnJSON = new ReturnJSON();
    }

    public void registerUser(View view) {
        if (isEmpty(etEMailNewUser) || isEmpty(etPasswordNewUser) || isEmpty(etNameNewUser) || isEmpty(etTownNewUser) || isEmpty(etProvinceNewUser) || isEmpty(etPhoneNewUser)) {
            Toast.makeText(this, R.string.complete_fields, Toast.LENGTH_SHORT).show();
        } else if (!isEmailValid(etEMailNewUser.getText().toString())) {
            Toast.makeText(this, R.string.not_valid_email, Toast.LENGTH_SHORT).show();
        } else {
            email = etEMailNewUser.getText().toString();
            new NewUserTask().execute();
        }
    }

    public void cancel(View view) {
        finish();
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    ///////Task to registerUser a new user
    class NewUserTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NewUserActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "INSERT INTO users (`email`, `Password`, `Name`, `Town`, `Province`, `Phone`) VALUES ('" + email + "','0','0','0','0','')");

                jsonObject = returnJSON.sendDMLRequest(Parameters.URL_UPLOAD, parametrosPost);

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
                    Toast.makeText(NewUserActivity.this, R.string.new_user_ok,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewUserActivity.this, R.string.new_bike_error,
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(NewUserActivity.this, R.string.charging_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
