package david.bicilock;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    private ArrayList<Bike> list;
    private final Activity actividad;

    public Adapter(Activity a, ArrayList<Bike> v) {
        super();
        this.list = v;
        this.actividad = a;
    }
// En el constructor de la clase se indica la actividad donde se ejecutará la lista de datos a visualizar.
    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return list.get(arg0).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater ly = actividad.getLayoutInflater();
        View view = ly.inflate(R.layout.item, null, true);
        TextView tvBrand = (TextView) view.findViewById(R.id.tvBrandItem);
        tvBrand.setText((CharSequence) list.get(position).getBrand());
        TextView tvModel = (TextView) view.findViewById(R.id.tvModelItem);
        tvModel.setText(list.get(position).getModel());
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        //Glide.with(actividad).load(list.get(position).getUrlFav()).into(iv);
        /*File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fotos_Contactos", lista.get(position).getrFoto());
        if (imgFile.exists()) {
            ImageView im = (ImageView) view.findViewById(R.id.iv);
            im.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            im.setAdjustViewBounds(true);
        }*/
        return view;
    }
}
