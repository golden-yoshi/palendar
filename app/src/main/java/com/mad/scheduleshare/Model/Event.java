package com.mad.scheduleshare.Model;

import java.util.ArrayList;

public class Event {

    protected String mName;
    protected String mStartTime;
    protected String mEndTime;
    protected String mDayOfWeek;
    protected String mLocation;
    protected String mFriendList;

    public Event() {
    }

    public Event(String mName, String mStartTime, String mEndTime, String mDayOfWeek, String mLocation, String mFriendList) {
        this.mName = mName;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mDayOfWeek = mDayOfWeek;
        this.mLocation = mLocation;
        this.mFriendList = mFriendList;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(String mDayOfWeek) {
        this.mDayOfWeek = mDayOfWeek;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getFriendsList() {
        return mFriendList;
    }

    public void setFriendList(String mFriendList) {
        this.mFriendList = mFriendList;
    }
}
