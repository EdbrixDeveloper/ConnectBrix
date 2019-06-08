package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.activities.MeetingDetailsActivity;
import com.edbrix.connectbrix.activities.SchoolListActivity;

import java.util.ArrayList;

public class SchoolListAdapter extends BaseAdapter {

    private Activity schoolListActivity;
    ArrayList<String> date;
    private static LayoutInflater inflater = null;

    public SchoolListAdapter(Activity schoolListActivity, ArrayList<String> date) {
        this.schoolListActivity = schoolListActivity;
        this.date = date;
        inflater = (LayoutInflater) schoolListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = inflater.inflate(R.layout.activity_school_list_item, null);
            holder = new ViewHolder();

            holder.textViewMeetingName = (TextView)view.findViewById(R.id.textViewMeetingName);
            holder.textViewMeetingAgenda = (TextView)view.findViewById(R.id.textViewAgenda);
           // holder.textViewParticipent = (TextView)view.findViewById(R.id.textViewPartycipent);
            holder.textViewParticipentCount = (TextView)view.findViewById(R.id.textViewPartycipentCount);

            /*holder.btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(schoolListActivity, MeetingDetailsActivity.class);
                    intent.putExtra("meetingTitle",holder.textViewMeetingName.getText().toString());
                    schoolListActivity.startActivity(intent);
                }
            });*/

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }

    static class ViewHolder {
        TextView textViewMeetingName;
        TextView textViewMeetingAgenda;
        TextView textViewParticipent;
        TextView textViewParticipentCount;
    }
}
