package com.anibij.demoapp.model;

/**
 * Created by bsoren on 02-Jul-16.
 */
public class User {

    private String name;
    private String imageName;
    private String searchItems;
    private String searchText;

    public User(String searchName, String searchImage, String searchItems, String searchText) {
        this.name = searchName;
        this.imageName = searchImage;
        this.searchItems = searchItems;
        this.searchText =  searchText;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(String searchItems) {
        this.searchItems = searchItems;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", imageName='" + imageName + '\'' +
                ", searchItems='" + searchItems + '\'' +
                '}';
    }
}
