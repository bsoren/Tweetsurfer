package com.anibij.demoapp.model;

/**
 * Created by bsoren on 03-Jul-16.
 */
public class User {

    private String name;
    private String screenName;
    private String profileImageUrl;
    private String latestStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getLatestStatus() {
        return latestStatus;
    }

    public void setLatestStatus(String latestStatus) {
        this.latestStatus = latestStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", screenName='" + screenName + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", latestStatus='" + latestStatus + '\'' +
                '}';
    }
}
