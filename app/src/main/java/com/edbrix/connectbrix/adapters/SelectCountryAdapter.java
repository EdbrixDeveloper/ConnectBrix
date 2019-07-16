package com.edbrix.connectbrix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.CountryList;

import java.util.ArrayList;
import java.util.List;

public class SelectCountryAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = SelectCountryAdapter.class.getName();
    private Context context;
    List<CountryList> countryList;
    private List<CountryList> filteredData = null;
    private OnTextViewActionListener onTextViewActionListener;
    //private OnButtonActionListener onButtonActionListener;
    private ItemFilter mFilter = new ItemFilter();
    String countryId = "";

    public interface OnTextViewActionListener {
        public void onTextViewClicked(CountryList datum, int position);
    }

    public SelectCountryAdapter(Context context,
                                List<CountryList> countryList, OnTextViewActionListener onTextViewActionListener, String countryId) {
        this.context = context;
        this.countryList = countryList;
        this.filteredData = countryList;
        this.onTextViewActionListener = onTextViewActionListener;
        this.countryId = countryId;
    }

    @Override
    public int getCount() {
        //return countryList.size();
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        //return countryList.get(position);
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return Long.parseLong(this.countryList.get(position).getId());
        return Long.parseLong(this.filteredData.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CountryList datum = filteredData.get(position);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_select_country_list_item, parent, false);

        TextView txtCountryName = (TextView) view.findViewById(R.id.txtCountryName);
        CheckBox chkSelectedCountry = (CheckBox)view.findViewById(R.id.chkSelectedCountry);

        txtCountryName.setText(filteredData.get(position).getTitle());

        if(filteredData.get(position).getId().equals(countryId)){
            chkSelectedCountry.setVisibility(View.VISIBLE);
            chkSelectedCountry.setChecked(true);
        }else{
            chkSelectedCountry.setChecked(false);
            chkSelectedCountry.setVisibility(View.GONE);
        }

        txtCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextViewActionListener.onTextViewClicked(datum, position);
            }
        });

        /*if (filteredData.get(position).getTitle() != null && !filteredData.get(position).getEmail().isEmpty() && filteredData.get(position).getTitle().isEmpty()) {
            txtCountryName.setText(filteredData.get(position).getEmail());
        }

        if (filteredData.get(position).getImageUrl() != null && !filteredData.get(position).getImageUrl().isEmpty()) {
            Glide.with(context).load(filteredData.get(position).getImageUrl())
                    //.apply(RequestOptions.bitmapTransform(new FitCenter()))
                    .into(mImgFParticipant);
        }

        mTxtFOrganizationName.setText("");

        mChkFSelectParticipant.setChecked(filteredData.get(position).getIsChecked());

        mChkFSelectParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filteredData.get(position).getIsChecked()) {
                    filteredData.get(position).setIsChecked(false);
                } else {
                    filteredData.get(position).setIsChecked(true);
                }

            }
        });

        mChkFSelectParticipant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onButtonActionListener.onCheckBoxPressed(datum, position, isChecked);

            }
        });*/

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

            final List<CountryList> list = countryList;

            int count = list.size();
            final ArrayList<CountryList> nlist = new ArrayList<CountryList>(count);

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
            filteredData = (ArrayList<CountryList>) results.values;
            notifyDataSetChanged();
        }

    }


}
