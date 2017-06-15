package david.bicilock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterLv extends BaseAdapter {
    private ArrayList<Bike> list;
    private final Activity actividad;

    public AdapterLv(Activity a, ArrayList<Bike> v) {
        super();
        this.list = v;
        this.actividad = a;
    }

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
        View view = ly.inflate(R.layout.item_lv, null, true);
        TextView tvBrand = (TextView) view.findViewById(R.id.tvBrandItem);
        tvBrand.setText((CharSequence) list.get(position).getBrand());
        TextView tvModel = (TextView) view.findViewById(R.id.tvModelItem);
        tvModel.setText(list.get(position).getModel());
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        if(!list.get(position).getUrlFav().equals("null")) {
            Glide.with(actividad).load(list.get(position).getUrlFav()).into(iv);
        }

        return view;
    }
}
