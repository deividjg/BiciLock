package david.bicilock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
    private JSONArray jSONArray;
    protected JSONObject jsonObject;
    private ReturnJSON devuelveJSON;
    private User user;
    private ArrayList<User> arrayUsers;
    ArrayList<HashMap<String, String>> userList;
    private EditText etEMail;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        etEMail = (EditText)findViewById(R.id.etEMail);

        url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";

        devuelveJSON = new ReturnJSON();

    }
    public void registrar(View view){
        email = etEMail.getText().toString();
        new RegistroTask().execute();
    }




    ///////Task para registrar un nuevo usuario
    class RegistroTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql",  "INSERT INTO `usuarios`(`email`, `Password`, `Nombre`, `Poblacion`, `Provincia`, `Telefono`) VALUES ('" + email + "',0,0,0,0,'')");

                jsonObject = devuelveJSON.sendDMLRequest(url_subida, parametrosPost);

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

                if(add!=0){
                    Toast.makeText(RegisterActivity.this, "Registro guardado",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "ha ocurrido un error",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(RegisterActivity.this, "JSON Array nulo",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
