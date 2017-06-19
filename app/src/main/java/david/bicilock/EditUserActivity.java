package david.bicilock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EditUserActivity extends AppCompatActivity {

    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;
    private JSONArray jSONArrayUsers;
    private EditText etEMailEditUser, etPasswordEditUser, etNameEditUser, etTownEditUser, etProvinceEditUser, etPhoneEditUser;
    private String email, password, name, town, province, phone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etEMailEditUser = (EditText) findViewById(R.id.etEMailNewUser);
        etPasswordEditUser = (EditText) findViewById(R.id.etPasswordNewUser);
        etNameEditUser = (EditText) findViewById(R.id.etNameNewUser);
        etTownEditUser = (EditText) findViewById(R.id.etTownNewUser);
        etProvinceEditUser = (EditText) findViewById(R.id.etProvinceNewUser);
        etPhoneEditUser = (EditText) findViewById(R.id.etPhoneNewUser);
        returnJSON = new ReturnJSON();

        getEmail();
        new UserDataTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.saveUserEdit) {
            if (isEmpty(etPasswordEditUser) || isEmpty(etNameEditUser) || isEmpty(etProvinceEditUser) || isEmpty(etTownEditUser) || isEmpty(etPhoneEditUser)) {
                Toast.makeText(this, R.string.complete_fields, Toast.LENGTH_SHORT).show();
            } else {
                email = etEMailEditUser.getText().toString();
                password = etPasswordEditUser.getText().toString();
                name = etNameEditUser.getText().toString();
                province = etProvinceEditUser.getText().toString();
                town = etTownEditUser.getText().toString();
                phone = etPhoneEditUser.getText().toString();
                new EditUserTask().execute();
            }
        }
        if (id == R.id.cancelUserEdit) {
            Toast.makeText(this, R.string.cancel_edition, Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    protected void getEmail() {
        email = getIntent().getStringExtra("email");
    }

    protected void prepareScreen() {
        etEMailEditUser.setText(email);
        etPasswordEditUser.setText(user.getPassword());
        etNameEditUser.setText(user.getName());
        etProvinceEditUser.setText(user.getProvince());
        etTownEditUser.setText(user.getTown());
        etPhoneEditUser.setText(user.getPhone());
    }

    ///////Task for download user's data
    class UserDataTask extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditUserActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "SELECT * FROM users WHERE email='" + email + "'");

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
                long id;
                for (int i = 0; i < json.length(); i++) {
                    id = i;
                    try {
                        JSONObject jsonObject = json.getJSONObject(i);
                        user = new User();
                        user.setEmail(email);
                        user.setPassword(jsonObject.getString("Password"));
                        user.setName(jsonObject.getString("Name"));
                        user.setTown(jsonObject.getString("Town"));
                        user.setProvince(jsonObject.getString("Province"));
                        user.setPhone(jsonObject.getString("Phone"));

                        prepareScreen();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(EditUserActivity.this, R.string.showing_user_data, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditUserActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////Task for edit user
    class EditUserTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(EditUserActivity.this);
            pDialog.setMessage(getString(R.string.charging));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "UPDATE users SET Password='" + password + "', Name='" + name + "', Town='" + town + "', Province='" + province + "', Phone='" + phone + "' WHERE email='" + email +"'");

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
                    Toast.makeText(EditUserActivity.this, R.string.data_updated_ok,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditUserActivity.this, R.string.error_updating,
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(EditUserActivity.this, R.string.charging_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
