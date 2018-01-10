package com.example.narendra.anonymoustwitter;

/**
 * Created by Narendra on 08-01-2018.
 */

public class Postdetails {

    String desc, username, useruid, imagedownloadurl,postkey;
    long time;
    int userred,usergreen,userblue;
    String test = "hello";

    public String getTest() {
        return test;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }

    public String getImagedownloadurl() {
        return imagedownloadurl;
    }

    @Override
    public String toString() {
        return "Postdetails{" +
                "desc='" + desc + '\'' +
                ", postkey='" + postkey + '\'' +
                '}';
    }

    public void setImagedownloadurl(String imagedownloadurl) {
        this.imagedownloadurl = imagedownloadurl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getUserred() {
        return userred;
    }

    public void setUserred(int userred) {
        this.userred = userred;
    }

    public int getUsergreen() {
        return usergreen;
    }

    public void setUsergreen(int usergreen) {
        this.usergreen = usergreen;
    }

    public int getUserblue() {
        return userblue;
    }

    public void setUserblue(int userblue) {
        this.userblue = userblue;
    }
}
