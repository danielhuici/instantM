package info.androidhive.loginandregistration.group;

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



public class GroupAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Group> vGroups;
    private ArrayList<Group> vFilteredGroups;
    private Filter nameFilter = new GroupAdapter.GroupFilter();

    public GroupAdapter(Activity context, ArrayList<Group> vGroups) {
        this.context = context;
        this.vGroups = vGroups;
        this.vFilteredGroups = vGroups;
        this.mInflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return vFilteredGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return vFilteredGroups.get(position);
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

        Group group = vFilteredGroups.get(position);
        holder.title.setText(group.getName());
        if(vGroups.get(position).getDescription() != null && ! vGroups.get(position).getDescription().equalsIgnoreCase("null")) {
           holder.topSubtitle.setText(vGroups.get(position).getDescription());
        }else{
            holder.topSubtitle.setText("");
        }
        holder.pic.setImageResource(R.drawable.group64);
        //holder.pic.setImageBitmap(group.getPic());

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

            final List<Group> list = vGroups;
            int count = list.size();
            final List<Group> nlist = new ArrayList<>(count);


            for(Group group : vGroups) {
                if (group.nameLike(String.valueOf(constraint))) {
                    nlist.add(group);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            vFilteredGroups = (ArrayList<Group>) results.values;
            notifyDataSetChanged();
        }

    }
}
