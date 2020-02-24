package info.androidhive.loginandregistration.app;

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
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grupo, null);
            holder = new ViewHolder();
            //Creamos un objeto de la clase ViewHolder y hacemos que cada atributo referencie
            //a un elemento del laout. Esta referencia se mantiene y cuando reutilicemos la vista
            //convertView ya no tendrá que llamar al método findViewById()
            holder.movil = (TextView) convertView.findViewById(R.id.tvTelefonoMovil);
            holder.fijo = (TextView) convertView.findViewById(R.id.tvTelefonoFijo);
            holder.nombreApellidos = (TextView) convertView.findViewById(R.id.tvNombreApellidos);
            holder.fotoImageView = (ImageView) convertView.findViewById(R.id.ivFoto);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Grupo grupo = vGrupos.get(position);

        holder.nombreApellidos.setText(grupo.getNombre());
        holder.movil.setText(grupo.getApellido());
        holder.fijo.setText(grupo.getApellido());
        holder.fotoImageView.setImageBitmap(grupo.getFoto());


        return convertView;
    }
    private static class ViewHolder {
        private TextView movil, fijo, nombreApellidos;
        private ImageView fotoImageView;
    }
}
