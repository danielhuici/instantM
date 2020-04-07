package info.androidhive.loginandregistration.contact;

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


public class ContactAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> filteredContacts;
    private Filter nameFilter = new ContactAdapter.ContactFilter();

    public ContactAdapter(Activity context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
        this.filteredContacts = contacts;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return filteredContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public int getSelectedConctactId(int position){
        return contacts.get(position).getUserId();
    }
    public String getContactName(int position){
        return contacts.get(position).getName();
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

            holder.title =  convertView.findViewById(R.id.tvTitle);
            holder.topSubtitle =  convertView.findViewById(R.id.tvSubtitle);
            holder.pic =  convertView.findViewById(R.id.pic);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = filteredContacts.get(position);
        if(contacts.get(position).getName().equalsIgnoreCase("-1")){
            holder.title.setPadding(0,60,0,0);
            holder.title.setText(R.string.add_member);
            holder.topSubtitle.setText(contact.getLastConnectionText());
            holder.pic.setImageResource(R.drawable.add);
        }else{
            holder.title.setText(contact.getName());
            holder.topSubtitle.setText(contact.getLastConnectionText());
            if(contact.getFoto() != null)
                holder.pic.setImageBitmap(contact.getFoto());
        }

        return convertView;
    }
    private static class ViewHolder {
        private TextView title, topSubtitle;
        private ImageView pic;
    }
    public Filter getFilter() {
        return this.nameFilter;
    }



    public class ContactFilter  extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            final List<Contact> list = contacts;
            int count = list.size();
            final List<Contact> nlist = new ArrayList<>(count);


            for(Contact user : contacts) {
                if (user.nameLike(String.valueOf(constraint))) {
                    nlist.add(user);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredContacts = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
        }

    }

}