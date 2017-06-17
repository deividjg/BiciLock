package david.bicilock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditUserActivity extends AppCompatActivity {

    protected JSONObject jsonObject;
    private ReturnJSON devuelveJSON;
    private EditText etEMailEditUser, etPasswordEditUser, etNameEditUser, etTownEditUser, etProvinceEditUser, etPhoneEditUser;
    private String email;
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
        devuelveJSON = new ReturnJSON();

        getUser();
        prepareScreen();
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
                new EditUserActivity.EditUserTask().execute();
            }
        }
        if (id == R.id.cancelUserEdit) {
            finish();
            Toast.makeText(this, R.string.cancel_edition, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    protected void getUser() {
        user = (User)getIntent().getSerializableExtra("user");
    }

    protected void prepareScreen() {
        etEMailEditUser.setText(user.getEmail());
        etPasswordEditUser.setText(user.getPassword());
        etNameEditUser.setText(user.getName());
        etProvinceEditUser.setText(user.getProvince());
        etTownEditUser.setText(user.getTown());
        etPhoneEditUser.setText(user.getPhone());
    }

    ///////Task to edit user
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
                parametrosPost.put("ins_sql", "INSERT INTO users (`email`, `Password`, `Name`, `Town`, `Province`, `Phone`) VALUES ('" + email + "','0','0','0','0','')");

                jsonObject = devuelveJSON.sendDMLRequest(Parameters.URL_UPLOAD, parametrosPost);

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
                    Toast.makeText(EditUserActivity.this, R.string.new_user_ok,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditUserActivity.this, R.string.new_bike_error,
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(EditUserActivity.this, R.string.charging_error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
