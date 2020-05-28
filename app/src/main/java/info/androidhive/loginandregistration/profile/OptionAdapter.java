package info.androidhive.loginandregistration.profile;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.loginandregistration.R;
/**
 * Adaptador para la visa personalizada de listas de opciones.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class OptionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<ProfileOption> vOptions;


    OptionAdapter(Activity context, ArrayList<ProfileOption> vOptions) {
        this.context = context;
        this.vOptions = vOptions;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return vOptions.size();
    }

    @Override
    public Object getItem(int i) {
        return vOptions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_item, null);

            holder = new ViewHolder();

            holder.title =  convertView.findViewById(R.id.tvTitle);
            holder.pic =  convertView.findViewById(R.id.pic);
            holder.subtitle =  convertView.findViewById(R.id.tvSubtitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProfileOption option = vOptions.get(position);

        holder.title.setPadding(0,60,0,0);
        holder.title.setText(option.getTitle());
        holder.subtitle.setText(option.getValue());
        holder.pic.setImageBitmap(option.getPic());


        return convertView;
    }
    private static class ViewHolder {
        private TextView title;
        private TextView subtitle;
        private ImageView pic;
    }
}
