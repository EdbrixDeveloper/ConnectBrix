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

import com.edbrix.connectbrix.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FliteredParticipantsAdapter extends BaseAdapter {

    private static final String TAG = FliteredParticipantsAdapter.class.getName();
    private Activity filteredParticipantsListActivity;
    ArrayList<String> date;
    private static LayoutInflater inflater = null;

    public FliteredParticipantsAdapter(Activity filteredParticipantsListActivity,ArrayList<String> date){
        this.filteredParticipantsListActivity = filteredParticipantsListActivity;
        this.date = date;
        inflater = (LayoutInflater) filteredParticipantsListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return date.size();
    }

    @Override
    public Object getItem(int position) {
        return date.get(position);
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

            holder.mImgFParticipant = (CircleImageView)view.findViewById(R.id.imgFParticipant);
            holder.mTxtFParticipantName = (TextView)view.findViewById(R.id.txtFParticipantName);
            holder.mTxtFOrganizationName = (TextView)view.findViewById(R.id.txtFOrganizationName);
            holder.mChkFSelectParticipant = (CheckBox)view.findViewById(R.id.chkFSelectParticipant);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    static class ViewHolder {
        CircleImageView mImgFParticipant;
        TextView mTxtFParticipantName;
        TextView mTxtFOrganizationName;
        CheckBox mChkFSelectParticipant;
    }
}
