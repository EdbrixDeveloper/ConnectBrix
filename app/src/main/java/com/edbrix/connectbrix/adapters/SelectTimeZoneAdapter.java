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
import com.edbrix.connectbrix.activities.SelectTimeZoneActivity;
import com.edbrix.connectbrix.data.CountryList;
import com.edbrix.connectbrix.data.StateList;
import com.edbrix.connectbrix.data.TimeZoneListData;

import java.util.ArrayList;
import java.util.List;

public class SelectTimeZoneAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = SelectTimeZoneAdapter.class.getName();
    private Context context;
    List<TimeZoneListData> timeZoneListData;
    private List<TimeZoneListData> filteredData = null;
    private OnTextViewActionListener onTextViewActionListener;
    private ItemFilter mFilter = new ItemFilter();

    public interface OnTextViewActionListener {
        public void onTextViewClicked(TimeZoneListData datum, int position);
    }

    public SelectTimeZoneAdapter(Context context, List<TimeZoneListData> timeZoneListData, OnTextViewActionListener onTextViewActionListener){

        this.context = context;
        this.timeZoneListData = timeZoneListData;
        this.filteredData = timeZoneListData;
        this.onTextViewActionListener = onTextViewActionListener;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(this.filteredData.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TimeZoneListData datum = filteredData.get(position);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_time_zone_list_item, parent, false);
        TextView timeZone = (TextView)view.findViewById(R.id.txtTimeZone);
        timeZone.setText(filteredData.get(position).getTitle());

        timeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextViewActionListener.onTextViewClicked(datum, position);
            }
        });
        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<TimeZoneListData> list = timeZoneListData;

            int count = list.size();
            final ArrayList<TimeZoneListData> nlist = new ArrayList<TimeZoneListData>(count);

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
            filteredData = (ArrayList<TimeZoneListData>) results.values;
            notifyDataSetChanged();
        }

    }
}
