package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.connectbrix.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantsListAdapter extends BaseAdapter {

    private static final String TAG = ParticipantsListAdapter.class.getName();
    private Activity participantListActivity;
    ArrayList<String> participantList;
    private static LayoutInflater inflater = null;

    public ParticipantsListAdapter(Activity participantListActivity,ArrayList<String> participantList){
        this.participantListActivity = participantListActivity;
        this.participantList = participantList;
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
            holder.partcipantImage = (CircleImageView)view.findViewById(R.id.imgParticipant);
            holder.participantName = (TextView)view.findViewById(R.id.txtParticipantName);
            holder.organizationName = (TextView)view.findViewById(R.id.txtorganizationName);
            holder.txtIsAvaliable = (TextView)view.findViewById(R.id.txtIsAvaliable);
            holder.remove = (ImageView)view.findViewById(R.id.imgRemove);
            holder.status = (ImageView)view.findViewById(R.id.imgStatus);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.participantName.setText(participantList.get(position).toString());
        holder.organizationName.setText("The World Talent Organization");
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
