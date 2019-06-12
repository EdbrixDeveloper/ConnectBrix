package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.MeetingParticipantList;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FliteredParticipantsAdapter extends BaseAdapter {

    private static final String TAG = FliteredParticipantsAdapter.class.getName();
    private Activity filteredParticipantsListActivity;
    List<MeetingParticipantList> meetingParticipantList;
    private static LayoutInflater inflater = null;

    public FliteredParticipantsAdapter(Activity filteredParticipantsListActivity, List<MeetingParticipantList> meetingParticipantList) {
        this.filteredParticipantsListActivity = filteredParticipantsListActivity;
        this.meetingParticipantList = meetingParticipantList;
        inflater = (LayoutInflater) filteredParticipantsListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return meetingParticipantList.size();
    }

    @Override
    public Object getItem(int position) {
        return meetingParticipantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_select_participants_list_item, null);
            holder = new ViewHolder();

            holder.mImgFParticipant = (CircleImageView) view.findViewById(R.id.imgFParticipant);
            holder.mTxtFParticipantName = (TextView) view.findViewById(R.id.txtFParticipantName);
            holder.mTxtFOrganizationName = (TextView) view.findViewById(R.id.txtFOrganizationName);
            holder.mChkFSelectParticipant = (CheckBox) view.findViewById(R.id.chkFSelectParticipant);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mTxtFParticipantName.setText(meetingParticipantList.get(position).getName());

        if (meetingParticipantList.get(position).getEmail() != null && !meetingParticipantList.get(position).getEmail().isEmpty() && meetingParticipantList.get(position).getName().isEmpty()) {
            holder.mTxtFParticipantName.setText(meetingParticipantList.get(position).getEmail());
        }

        if (meetingParticipantList.get(position).getImageUrl() != null && !meetingParticipantList.get(position).getImageUrl().isEmpty()) {
            Glide.with(filteredParticipantsListActivity).load(meetingParticipantList.get(position).getImageUrl())
                    //.apply(RequestOptions.bitmapTransform(new FitCenter()))
                    .into(holder.mImgFParticipant);
        }

        holder.mTxtFOrganizationName.setText("");

        return view;
    }

    static class ViewHolder {
        CircleImageView mImgFParticipant;
        TextView mTxtFParticipantName;
        TextView mTxtFOrganizationName;
        CheckBox mChkFSelectParticipant;
    }
}
