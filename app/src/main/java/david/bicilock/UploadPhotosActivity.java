package david.bicilock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UploadPhotosActivity extends AppCompatActivity {

    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 0;

    private static int ACT_CAMARA = 1;

    private DateFormat datehourFormat;
    private Date date;

    private String photoId, serialNumber;

    private String url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
    protected JSONObject jsonObject;
    private ReturnJSON returnJSON;

    private String url;

    //view objects
    private ImageView imageView;

    //firebase objects
    private StorageReference storageReference;

    private Intent intent;

    private Bitmap bm;

    private Uri uriImage;

    private String imageExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSerialNumber();

        url_subida = "http://iesayala.ddns.net/deividjg/prueba.php";
        returnJSON = new ReturnJSON();

        imageView = (ImageView) findViewById(R.id.imageView);

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify adapterLv parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.terminar) {
            finish();
            garageScreen();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void getSerialNumber(){
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(getApplicationContext(), "Error recibiendo serialNumber", Toast.LENGTH_LONG).show();
        } else {
            serialNumber = extras.getString("serialNumber");
        }
    }

    //Obtain the extension of content uris. Doesn't work for file uris
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void showSourceDialog(View view) {
        final AlertDialog.Builder alertDialogBu = new AlertDialog.Builder(this);
        alertDialogBu.setTitle("Elige fuente de la foto");
        alertDialogBu.setIcon(R.drawable.ic_add_a_photo_black_24dp);
        CharSequence opciones[] = {"Galería","Cámara", "Cancelar"};
        alertDialogBu.setItems(opciones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    case 0:
                        photoId = newPhotoId();
                        intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        break;
                    case 1:
                        //camera stuff
                        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        photoId = newPhotoId();

                        //folder stuff
                        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Bicilock");

                        if (!imagesFolder.exists()) {
                            imagesFolder.mkdirs();
                        }

                        File image = new File(imagesFolder, photoId + ".jpeg");
                        uriImage = Uri.fromFile(image);
                        imageExtension = "jpeg";

                        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
                        startActivityForResult(imageIntent, 1);

                        break;
                    case 2:
                        break;
                }
            }
        });
        AlertDialog alertDialog = alertDialogBu.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageExtension = getFileExtension(uriImage);
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == ACT_CAMARA && resultCode == RESULT_OK) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(uriImage.getPath()+"");
        }
    }

    //this method will upload the file
    public void uploadFile(View view) {
        //displaying adapterLv progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        //if there is adapterLv file to upload
        if (uriImage != null) {
            StorageReference riversRef = storageReference.child("images/" + serialNumber + "/" + photoId + "." + imageExtension);
            riversRef.putFile(uriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying adapterLv success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();
                            imageView.setImageBitmap(null);
                            url = taskSnapshot.getDownloadUrl().toString();
                            new NewPhotoTask().execute();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Error en la fuente de la foto", Toast.LENGTH_LONG).show();
        }
    }

    ///////Task para registrar una nueva foto
    class NewPhotoTask extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        int add;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(UploadPhotosActivity.this);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> parametrosPost = new HashMap<>();
                parametrosPost.put("ins_sql", "INSERT INTO photos VALUES('" + photoId + "." + imageExtension + "', '" + serialNumber + "', '" + url + "', 0)");
                jsonObject = returnJSON.sendDMLRequest(url_subida, parametrosPost);

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
                    Toast.makeText(UploadPhotosActivity.this, "Registro guardado",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(UploadPhotosActivity.this, "ha ocurrido un error",
                            Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(UploadPhotosActivity.this, "JSON Array nulo",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    protected String newPhotoId(){
        datehourFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date();
        return datehourFormat.format(date);
    }

    protected void garageScreen(){
        Intent intent = new Intent (this, BikelistActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
