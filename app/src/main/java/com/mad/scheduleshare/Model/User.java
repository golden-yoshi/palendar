package com.mad.scheduleshare.Model;

public class User {

    private int mId;
    private String mName;
    private String mEmail;
    private String mPassword;

    public User() {
    }

    public User(int mId, String mName, String mEmail, String mPassword) {
        this.mId = mId;
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

}
