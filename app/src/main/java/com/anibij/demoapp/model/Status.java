package com.anibij.demoapp.model;

/**
 * Created by bsoren on 02-Nov-15.
 */

public class Status {

    private String id;
    private String user;
    private String message;
    private long createdAt;
    private String profileImageUrl;
    private String mediaImageUrl;
    private int moreItems = 0;
    private String retweetBy;
    private int retweetCount,favCount;

    public Status(String id, String user, String message, long createdAt, String profileImageUrl,String mediaImageUrl,String retweetBy,int retweetCount,int favCount) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.createdAt = createdAt;
        this.profileImageUrl = profileImageUrl;
        this.mediaImageUrl =  mediaImageUrl;
        this.retweetBy =  retweetBy;
        this.retweetCount =  retweetCount;
        this.favCount = favCount;
    }

    public Status(String default_value){
        this.id =  "999";
        this.user = "dummy_user";
        this.message = "message";
        this.createdAt = 0;
        this.profileImageUrl = null;
        this.mediaImageUrl =  null;

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
}

