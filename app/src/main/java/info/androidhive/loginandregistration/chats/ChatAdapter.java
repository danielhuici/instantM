package info.androidhive.loginandregistration.chats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.contact.Contact;

/**
 * Adaptador para la visa personalizada de listas de chats.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class ChatAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Chat> vChats;
    private ArrayList<Chat> vFilteredChats;
    private Filter nameFilter = new ChatAdapter.GroupFilter();

    public ChatAdapter(Activity context, ArrayList<Chat> vChats) {
        this.context = context;
        this.vChats = vChats;
        this.vFilteredChats = vChats;
        this.mInflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return vFilteredChats.size();
    }

    @Override
    public Object getItem(int position) {
        return vFilteredChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_item, null);
            holder = new ViewHolder();
            /**
             *  Creamos un objeto de la clase ViewHolder y hacemos que cada atributo haga referencia
             *  a un elemento del laoyut. Esta referencia se mantiene y cuando reutilicemos la vista
             *  convertView ya no tendrá que llamar al método findViewById()
             */

            holder.topSubtitle =  convertView.findViewById(R.id.tvSubtitle);
            holder.title = convertView.findViewById(R.id.tvTitle);
            holder.pic = convertView.findViewById(R.id.pic);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Chat chat = vFilteredChats.get(position);
        holder.title.setText(chat.getTitle());
        if(position < vChats.size())
        if(vChats.get(position).getSubtitle() != null && ! vChats.get(position).getSubtitle().equalsIgnoreCase("null")) {
           holder.topSubtitle.setText(vChats.get(position).getSubtitle());
        }else{
            holder.topSubtitle.setText("");
        }

        if(vFilteredChats.get(position).getClass().equals(Contact.class)) {
            holder.pic.setImageResource(R.drawable.group64);
        }else {
            holder.pic.setImageResource(R.drawable.user64);
            //holder.pic.setImageBitmap(chat.getPic());
        }
        return convertView;
    }

    public Filter getFilter() {
        return this.nameFilter;
    }

    private static class ViewHolder {
        private TextView title, topSubtitle;
        private ImageView pic;
    }

    public class GroupFilter  extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            final List<Chat> list = vChats;
            int count = list.size();
            final List<Chat> nlist = new ArrayList<>(count);


            for(Chat chat : vChats) {
                if (chat.nameLike(String.valueOf(constraint))) {
                    nlist.add(chat);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            vFilteredChats = (ArrayList<Chat>) results.values;
            notifyDataSetChanged();
        }

    }
}
