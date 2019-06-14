package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.MeetingParticipantList;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FliteredParticipantsAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = FliteredParticipantsAdapter.class.getName();
    private Context context;
    List<MeetingParticipantList> meetingParticipantList;
    private List<MeetingParticipantList> filteredData = null;
    private OnButtonActionListener onButtonActionListener;

    private ItemFilter mFilter = new ItemFilter();

    public interface OnButtonActionListener {
        public void onCheckBoxPressed(MeetingParticipantList datum, int position, boolean isChecked);
    }

    public FliteredParticipantsAdapter(Context context,
                                       List<MeetingParticipantList> meetingParticipantList, OnButtonActionListener onButtonActionListener) {
        this.context = context;
        this.meetingParticipantList = meetingParticipantList;
        this.filteredData = meetingParticipantList;
        this.onButtonActionListener = onButtonActionListener;
    }

    @Override
    public int getCount() {
        //return meetingParticipantList.size();
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        //return meetingParticipantList.get(position);
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return Long.parseLong(this.meetingParticipantList.get(position).getId());
        return Long.parseLong(this.filteredData.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MeetingParticipantList datum = filteredData.get(position);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_select_participants_list_item, parent, false);

        CircleImageView mImgFParticipant = (CircleImageView) view.findViewById(R.id.imgFParticipant);
        TextView mTxtFParticipantName = (TextView) view.findViewById(R.id.txtFParticipantName);
        TextView mTxtFOrganizationName = (TextView) view.findViewById(R.id.txtFOrganizationName);
        CheckBox mChkFSelectParticipant = (CheckBox) view.findViewById(R.id.chkFSelectParticipant);


        mTxtFParticipantName.setText(filteredData.get(position).getName());

        if (filteredData.get(position).getEmail() != null && !filteredData.get(position).getEmail().isEmpty() && filteredData.get(position).getName().isEmpty()) {
            mTxtFParticipantName.setText(filteredData.get(position).getEmail());
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

            final List<MeetingParticipantList> list = meetingParticipantList;

            int count = list.size();
            final ArrayList<MeetingParticipantList> nlist = new ArrayList<MeetingParticipantList>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
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
            filteredData = (ArrayList<MeetingParticipantList>) results.values;
            notifyDataSetChanged();
        }

    }


}
