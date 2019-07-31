package com.edbrix.connectbrix.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.UserMeetingListResponseData;

import java.util.ArrayList;

public class SchoolListWithCalendarAdapter extends BaseAdapter {

    Activity calenderViewMeetingActivity;
    ArrayList<UserMeetingListResponseData> userMeetingListResponseData;
    private static LayoutInflater inflater = null;

    public SchoolListWithCalendarAdapter(Activity calenderViewMeetingActivity, ArrayList<UserMeetingListResponseData> userMeetingListResponseData) {
        this.calenderViewMeetingActivity = calenderViewMeetingActivity;
        this.userMeetingListResponseData = userMeetingListResponseData;
        inflater = (LayoutInflater) calenderViewMeetingActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userMeetingListResponseData.size();
    }

    @Override
    public Object getItem(int position) {
        return userMeetingListResponseData.get(position);
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

            holder.mTextViewMeetingDay = (TextView) view.findViewById(R.id.textViewMeetingDay);
            holder.mTextViewMeetingMonth = (TextView) view.findViewById(R.id.textViewMeetingMonth);
            holder.mTextViewMeetingName = (TextView) view.findViewById(R.id.textViewMeetingName);
            holder.mTextViewAgenda = (TextView) view.findViewById(R.id.textViewAgenda);
            holder.mTextViewMeetingTime = (TextView) view.findViewById(R.id.textViewMeetingTime);
            holder.mTextViewPartycipentCount = (TextView) view.findViewById(R.id.textViewPartycipentCount);
            holder.myMeetingList = (LinearLayout) view.findViewById(R.id.myMeetingList);
            holder.textViewHostName = (TextView)view.findViewById(R.id.textViewHostName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String[] dateTime = userMeetingListResponseData.get(position).getStartDateTime().split(" ");
        String[] date = dateTime[0].split("/");
        holder.mTextViewMeetingDay.setText(date[0]);
        holder.mTextViewMeetingMonth.setText(date[1]);
        holder.mTextViewMeetingName.setText(userMeetingListResponseData.get(position).getTitle());
        holder.mTextViewAgenda.setText(userMeetingListResponseData.get(position).getAgenda());
        holder.mTextViewMeetingTime.setText(dateTime[1] + " " + dateTime[2]);
        holder.mTextViewPartycipentCount.setText(userMeetingListResponseData.get(position).getParticipantCount());
        holder.textViewHostName.setText(userMeetingListResponseData.get(position).getHostName());
        if (userMeetingListResponseData.get(position).getIsHost().equals("0")) {
            holder.myMeetingList.setBackground(null);
        }

        return view;
    }

    static class ViewHolder {

        TextView mTextViewMeetingDay;
        TextView mTextViewMeetingMonth;
        TextView mTextViewMeetingName;
        TextView mTextViewAgenda;
        TextView mTextViewMeetingTime;
        TextView mTextViewPartycipentCount;
        TextView textViewHostName;
        LinearLayout myMeetingList;

    }

}
