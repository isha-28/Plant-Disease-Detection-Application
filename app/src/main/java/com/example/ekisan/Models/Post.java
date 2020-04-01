package com.example.ekisan.Models;

import com.google.firebase.database.ServerValue;

public class Post {
    private  String title;
    private String description;
    private String userID;
    private String userPhoto;
    private String userName;
     private String postKey;
    private  String picture;
    private Object TimeStamp;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Post(String title, String description, String picture, String userID, String userPhoto, String userName) {
        this.title = title;
        this.description = description;
        this.userID = userID;
        this.userPhoto = userPhoto;
        this.picture=picture;
        this.userName=userName;
        TimeStamp = ServerValue.TIMESTAMP;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getPicture() {
        return picture;
    }

    public Object getTimeStamp() {
        return TimeStamp;
    }

    public  Post()
{






}


}
