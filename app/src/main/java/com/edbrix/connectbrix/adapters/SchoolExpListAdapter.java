package com.edbrix.connectbrix.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edbrix.connectbrix.R;
import com.edbrix.connectbrix.data.MeetingListData;
import com.edbrix.connectbrix.data.UserMeeting;
import com.edbrix.connectbrix.data.UserMeetingsDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SchoolExpListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    // child data in format of header title, child title

    //private MeetingListData meetingListData;
    private ArrayList<UserMeetingsDate> userMeetingsDateListAdapter;

    private ArrayList<UserMeetingsDate> originalList;

    private OnChildItemClickActionListener onChildItemClickActionListener;

    public interface OnChildItemClickActionListener {
        public void onChildItemClicked(UserMeeting usermeeting, int position);
    }

    public SchoolExpListAdapter(Context _context, ArrayList<UserMeetingsDate> userMeetingsDateListtemp, OnChildItemClickActionListener onChildItemClickActionListener) {
        //MeetingListData meetingListData
        this._context = _context;
        //this.meetingListData = meetingListData;
        this.userMeetingsDateListAdapter = userMeetingsDateListtemp;
        this.originalList = userMeetingsDateListtemp;
        this.onChildItemClickActionListener = onChildItemClickActionListener;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        //return meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosititon);
        return userMeetingsDateListAdapter.get(groupPosition).getUserMeetings().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //return Long.parseLong(meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().get(childPosition).getId());
        return Long.parseLong(userMeetingsDateListAdapter.get(groupPosition).getUserMeetings().get(childPosition).getId());
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final UserMeeting userMeeting = (UserMeeting) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_meeting_list, null);
        }
        LinearLayout ll_mainMeetingList = (LinearLayout) convertView.findViewById(R.id.ll_mainMeetingList);
        LinearLayout myMeetingList = (LinearLayout) convertView.findViewById(R.id.myMeetingList);
        TextView textViewMeetingDay = (TextView) convertView.findViewById(R.id.textViewMeetingDay);
        TextView textViewMeetingMonth = (TextView) convertView.findViewById(R.id.textViewMeetingMonth);
        TextView textViewMeetingName = (TextView) convertView.findViewById(R.id.textViewMeetingName);
        TextView textViewAgenda = (TextView) convertView.findViewById(R.id.textViewAgenda);
        TextView textViewMeetingTime = (TextView) convertView.findViewById(R.id.textViewMeetingTime);
        TextView textViewPartycipentCount = (TextView) convertView.findViewById(R.id.textViewPartycipentCount);

        String day = "", monthString = "";
        try {
            SimpleDateFormat dateFormatprev = new SimpleDateFormat("dd/MMM/yyyy");
            Date d = dateFormatprev.parse(userMeeting.getMeetingDate().toString());
            day = (String) DateFormat.format("dd", d); // 20
            monthString = (String) DateFormat.format("MMM", d); // Jun
        } catch (Exception ex) {

        }
        notifyDataSetChanged();

        textViewMeetingDay.setText(day);
        textViewMeetingMonth.setText(monthString);
        textViewMeetingName.setText(userMeeting.getTitle());
        textViewAgenda.setText(userMeeting.getAgenda());

        String[] meetingTime = userMeeting.getMeetingDate().split(" ");

        textViewMeetingTime.setText(meetingTime[1] + " " + meetingTime[2]);
        textViewPartycipentCount.setText(userMeeting.getMeetingParticipantsCount());

        if (userMeeting.getIsHost() == 0) {
            myMeetingList.setBackground(null);
        }

        ll_mainMeetingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChildItemClickActionListener.onChildItemClicked(userMeeting, childPosition);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return meetingListData.getUserMeetingsDates().get(groupPosition).getUserMeetings().size();
        return userMeetingsDateListAdapter.get(groupPosition).getUserMeetings().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //return meetingListData.getUserMeetingsDates().get(groupPosition);
        return userMeetingsDateListAdapter.get(groupPosition);
    }

    @Override
    public int getGroupCount() {

        //return meetingListData.getUserMeetingsDates().size();
        return userMeetingsDateListAdapter.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        UserMeetingsDate userMeetingsDate = (UserMeetingsDate) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_meeting_list_seprator, null);
            convertView.setClickable(false);
        }
        TextView textView_Date = (TextView) convertView.findViewById(R.id.textView_Date);
        TextView textView_Count = (TextView) convertView.findViewById(R.id.textView_Count);

        String[] meetingDate = userMeetingsDate.getDate().split(" ");

        textView_Date.setText(meetingDate[0]);
        textView_Date.setTypeface(null, Typeface.BOLD);

        textView_Count.setTypeface(null, Typeface.BOLD);
        textView_Count.setText("" + userMeetingsDate.getMeetingCount());//Total meetings:
        notifyDataSetChanged();

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void filterData(String query) {

        query = query.toLowerCase();
        Log.v("SchoolExpListAdapter", String.valueOf(userMeetingsDateListAdapter.size()));
        userMeetingsDateListAdapter = new ArrayList<UserMeetingsDate>();

        if (query.isEmpty()) {
            userMeetingsDateListAdapter.addAll(originalList);
        } else {

            //UserMeeting userMeeting
            for (UserMeetingsDate userMeetingsDate : originalList) {
                ArrayList<UserMeeting> countryList = userMeetingsDate.getUserMeetings();
                ArrayList<UserMeeting> newList = new ArrayList<UserMeeting>();

                for (UserMeeting country : countryList) {
                    if (country.getTitle().toLowerCase().contains(query) || country.getHostName().toLowerCase().contains(query)) {//
                        newList.add(country);
                    }
                }
                if (newList.size() > 0) {
                    UserMeetingsDate nContinent = new UserMeetingsDate(userMeetingsDate.getDate(), userMeetingsDate.getMeetingCount(), newList);
                    userMeetingsDateListAdapter.add(nContinent);
                }
            }
        }

        Log.v("SchoolExpListAdapter", String.valueOf(userMeetingsDateListAdapter.size()));
        notifyDataSetChanged();

    }

}


///
        /*import android.text.format.DateFormat;

        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2013*/