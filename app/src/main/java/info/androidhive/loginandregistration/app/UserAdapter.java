package info.androidhive.loginandregistration.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.loginandregistration.R;

public class UserAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<User> contacts;
    private ArrayList<User> filteredContacts;
    private Filter nameFilter = new ContactFilter();

    public UserAdapter(Activity context, ArrayList<User> contacts) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_item, null);
            holder = new UserAdapter.ViewHolder();
            /**
             *  Creamos un objeto de la clase ViewHolder y hacemos que cada atributo haga referencia
             *  a un elemento del laoyut. Esta referencia se mantiene y cuando reutilicemos la vista
             *  convertView ya no tendrá que llamar al método findViewById()
             */

            holder.title = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.topSubtitle = (TextView) convertView.findViewById(R.id.tvSubtitle);
            holder.pic = (ImageView) convertView.findViewById(R.id.pic);

            convertView.setTag(holder);
        } else {
            holder = (UserAdapter.ViewHolder) convertView.getTag();
        }

        User contact = filteredContacts.get(position);

        holder.title.setText(contact.getName());
        holder.topSubtitle.setText(contact.getLastConnectionText());
        holder.pic.setImageBitmap(contact.getFoto());


        return convertView;
    }

    public Filter getFilter() {
        return this.nameFilter;
    }

    private static class ViewHolder {
        private TextView title, topSubtitle;
        private ImageView pic;
    }



    public class ContactFilter  extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<User> list = contacts;
            int count = list.size();
            final List<User> nlist = new ArrayList<>(count);


            for(User user : contacts) {
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
            filteredContacts = (ArrayList<User>) results.values;
            notifyDataSetChanged();
        }

    }

}