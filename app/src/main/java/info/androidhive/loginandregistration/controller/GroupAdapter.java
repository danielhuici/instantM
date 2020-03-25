package info.androidhive.loginandregistration.controller;

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
import info.androidhive.loginandregistration.model.Grupo;


public class GroupAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Grupo> vGrupos;

    public GroupAdapter(Activity context, ArrayList<Grupo> vGrupos) {
        this.context = context;
        this.vGrupos = vGrupos;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return vGrupos.size();
    }

    @Override
    public Object getItem(int position) {
        return vGrupos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_item, null);
            holder = new ChatViewHolder();
            /**
             *  Creamos un objeto de la clase ViewHolder y hacemos que cada atributo haga referencia
             *  a un elemento del laoyut. Esta referencia se mantiene y cuando reutilicemos la vista
             *  convertView ya no tendrá que llamar al método findViewById()
             */

            holder.topSubtitle = (TextView) convertView.findViewById(R.id.tvSubtitle);
            holder.title = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.pic = (ImageView) convertView.findViewById(R.id.pic);

            convertView.setTag(holder);
        }
        else {
            holder = (ChatViewHolder) convertView.getTag();
        }

        Grupo grupo = vGrupos.get(position);

        holder.title.setText(grupo.getName());
        holder.topSubtitle.setText(grupo.getLastConnectionText());
        holder.pic.setImageBitmap(grupo.getFoto());

        return convertView;
    }

}
