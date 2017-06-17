package david.bicilock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private String email, password;
    private JSONArray jSONArray;
    private ReturnJSON returnJSON;
    private User user;
    private ArrayList<User> arrayUsers;
    private EditText etEmail, etPassword;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etEmail = (EditText) findViewById(R.id.etEmailLogin);
        etPassword = (EditText) findViewById(R.id.etPasswordLogin);

        returnJSON = new ReturnJSON();
    }

    public void enter(View view){
        if(isEmpty(etEmail) || isEmpty(etPassword)) {
            Toast.makeText(this, R.string.complete_fields, Toast.LENGTH_SHORT).show();
        } else {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            new CheckLogin().execute();
        }
    }

    public void cancel(View view){
        finish();
    }

    ///////Task to check user's connection
    class CheckLogin extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM users WHERE email='" + email + "' AND Password=" + password);

                jSONArray = returnJSON.sendRequest(Parameters.URL_DOWNLOAD, parametrosPost);

                if (jSONArray != null) {
                    return jSONArray;
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
                arrayUsers = new ArrayList<User>();
                for (int i = 0; i < json.length(); i++) {
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        user = new User();
                        user.setEmail(jsonObject.getString("email"));
                        user.setPassword(jsonObject.getString("Password"));
                        user.setName(jsonObject.getString("Name"));
                        user.setTown(jsonObject.getString("Town"));
                        user.setProvince(jsonObject.getString("Province"));
                        user.setPhone(jsonObject.getString("Phone"));
                        arrayUsers.add(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, R.string.login_ok, Toast.LENGTH_SHORT).show();
                    holdLogin();
                    mainScreen();
                }

            } else {
                Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void holdLogin(){
        sp = getSharedPreferences("preferences", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);
        editor.putBoolean("logged", true);
        editor.commit();
    }

    protected void mainScreen(){
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
