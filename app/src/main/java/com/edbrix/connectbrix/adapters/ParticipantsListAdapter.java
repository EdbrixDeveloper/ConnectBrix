package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.ParticipantList;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantsListAdapter extends BaseAdapter {

    private static final String TAG = ParticipantsListAdapter.class.getName();
    private Activity participantListActivity;
    ArrayList<ParticipantList> participantList;
    private static LayoutInflater inflater = null;
    private String meetingDbId = "", IsHost = "", UserType = "";

    public ParticipantsListAdapter(Activity participantListActivity, ArrayList<ParticipantList> participantList, String UserType, String meetingDbId, String IsHost) {
        this.participantListActivity = participantListActivity;
        this.participantList = participantList;
        this.UserType = UserType;
        this.meetingDbId = meetingDbId;
        this.IsHost = IsHost;
        inflater = (LayoutInflater) participantListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return participantList.size();
    }

    @Override
    public Object getItem(int position) {
        return participantList.get(position);
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
            view = inflater.inflate(R.layout.activity_participant_list_item, null);
            holder = new ViewHolder();
            holder.partcipantImage = (CircleImageView) view.findViewById(R.id.imgParticipant);
            holder.participantName = (TextView) view.findViewById(R.id.txtParticipantName);
            holder.organizationName = (TextView) view.findViewById(R.id.txtorganizationName);
            holder.txtIsAvaliable = (TextView) view.findViewById(R.id.txtIsAvaliable);
            holder.remove = (ImageView) view.findViewById(R.id.imgRemove);
            //holder.status = (ImageView) view.findViewById(R.id.imgStatus);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //ParticipantList participantList=this.participantList.get(position).

        if ((UserType.equals("T") || UserType.equals("A")) && IsHost.equals("1")) {
            holder.remove.setVisibility(View.VISIBLE);
        }

        //holder.participantName.setText(participantList.get(position).toString());
        //holder.organizationName.setText("The World Talent Organization");

        holder.participantName.setText(participantList.get(position).getName());
        holder.organizationName.setText(participantList.get(position).getOrgName());
        if (participantList.get(position).getStatus().equals("0")) {
            holder.txtIsAvaliable.setText("Waiting for response");
            holder.txtIsAvaliable.setTextColor(Color.parseColor("#bdbdbd"));
        } else if (participantList.get(position).getStatus().equals("1")) {
            holder.txtIsAvaliable.setText("Accepted");
            holder.txtIsAvaliable.setTextColor(Color.parseColor("#47a54b"));
        } else if (participantList.get(position).getStatus().equals("2")) {
            holder.txtIsAvaliable.setText("Rejected");
            holder.txtIsAvaliable.setTextColor(Color.parseColor("#d1395c"));

        }
        if (participantList.get(position).getImageUrl() != null && !participantList.get(position).getImageUrl().isEmpty()) {
            Glide.with(participantListActivity).load(participantList.get(position).getImageUrl())
                    //.apply(RequestOptions.bitmapTransform(new FitCenter()))
                    .into(holder.partcipantImage);
        }
        //holder.txtIsAvaliable.setText(participantList.get(position).getOrgName());

        return view;
    }

    static class ViewHolder {
        CircleImageView partcipantImage;
        TextView participantName;
        TextView organizationName;
        TextView txtIsAvaliable;
        ImageView remove;
        ImageView status;
    }
}
