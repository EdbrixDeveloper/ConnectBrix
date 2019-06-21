package com.edbrix.connectbrix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.StateList;

import java.util.ArrayList;
import java.util.List;

public class SelectStateAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = SelectStateAdapter.class.getName();
    private Context context;
    List<StateList> stateList;
    private List<StateList> filteredData = null;
    private OnTextViewActionListener onTextViewActionListener;
    //private OnButtonActionListener onButtonActionListener;
    private ItemFilter mFilter = new ItemFilter();

    public interface OnTextViewActionListener {
        public void onTextViewClicked(StateList datum, int position);
    }

    public SelectStateAdapter(Context context,
                              List<StateList> stateList, OnTextViewActionListener onTextViewActionListener) {
        this.context = context;
        this.stateList = stateList;
        this.filteredData = stateList;
        this.onTextViewActionListener = onTextViewActionListener;
    }

    @Override
    public int getCount() {
        //return stateList.size();
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        //return stateList.get(position);
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return Long.parseLong(this.stateList.get(position).getId());
        return Long.parseLong(this.filteredData.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final StateList datum = filteredData.get(position);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_select_state_list_item, parent, false);

        TextView txtStateName = (TextView) view.findViewById(R.id.txtStateName);

        txtStateName.setText(filteredData.get(position).getTitle());

        txtStateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextViewActionListener.onTextViewClicked(datum, position);
            }
        });



        return view;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<StateList> list = stateList;

            int count = list.size();
            final ArrayList<StateList> nlist = new ArrayList<StateList>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getTitle();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<StateList>) results.values;
            notifyDataSetChanged();
        }

    }


}
