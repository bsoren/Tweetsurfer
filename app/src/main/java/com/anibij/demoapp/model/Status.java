package com.anibij.demoapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bsoren on 02-Nov-15.
 */

public class Status implements Parcelable{

    private String id;
    private String user;
    private String message;
    private long createdAt;
    private String profileImageUrl;
    private String mediaImageUrl;
    private int moreItems = 0;
    private String retweetBy;
    private int retweetCount,favCount;
    private String screenName;
    private boolean favourite;

    public Status(){}

    public Status(String id, String user, String message, long createdAt, String profileImageUrl, String mediaImageUrl, String retweetBy, int retweetCount, int favCount, String screenName) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.createdAt = createdAt;
        this.profileImageUrl = profileImageUrl;
        this.mediaImageUrl =  mediaImageUrl;
        this.retweetBy =  retweetBy;
        this.retweetCount =  retweetCount;
        this.favCount = favCount;
        this.screenName = screenName;
    }

    public Status(String default_value){
        this.id =  "999";
        this.user = "dummy_user";
        this.message = "message";
        this.createdAt = 0;
        this.profileImageUrl = "http://pbs.twimg.com/profile_images/697679033937432576/JFN4zTh0_normal.png";
        this.mediaImageUrl = "http://pbs.twimg.com/media/CmWBu7NVIAI1wIe.jpg";

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getMediaImageUrl() {
        return mediaImageUrl;
    }

    public void setMediaImageUrl(String mediaImageUrl) {
        this.mediaImageUrl = mediaImageUrl;
    }

    public int getMoreItems() {
        return moreItems;
    }

    public void setMoreItems(int moreItems) {
        this.moreItems = moreItems;
    }

    public String getRetweetBy() {
        return retweetBy;
    }

    public void setRetweetBy(String retweetBy) {
        this.retweetBy = retweetBy;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }


    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", mediaImageUrl='" + mediaImageUrl + '\'' +
                ", moreItems='"+moreItems +'\''+
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

         dest.writeString(id);
         dest.writeString(user);
         dest.writeString(message);
         dest.writeLong(createdAt);
        dest.writeString(profileImageUrl);
        dest.writeString(mediaImageUrl);
        dest.writeInt(moreItems);
        dest.writeString(retweetBy);
        dest.writeInt(retweetCount);
        dest.writeInt(favCount);
        dest.writeString(screenName);
        dest.writeInt((favourite == true)? 1: 0);

    }

    public static final Parcelable.Creator CREATOR =  new Parcelable.Creator<Status>(){

        @Override
        public Status createFromParcel(Parcel source) {
           return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[0];
        }
    };

    public Status(Parcel in){

          this.id =  in.readString();
          this.user = in.readString();
          this.message =  in.readString();
          this.createdAt = in.readLong();
          this.profileImageUrl = in.readString();
          this.mediaImageUrl = in.readString();
          this.moreItems = in.readInt();
          this.retweetBy = in.readString();
          this.retweetCount = in.readInt();
          this.favCount = in.readInt();
          this.screenName = in.readString();
          this.favourite = (in.readInt() == 1? true : false);
    }
}

