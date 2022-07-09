package edu.neu.harshit.gajjar.numadsp22_team24_a8.Model;

public class User {
    private String id;
    private String username;
    private String imageURL;

    // Constructors
    public User(){
    }

    public User(String id, String username, String imageUrl) {
        this.id = id;
        this.username = username;
        this.imageURL = imageUrl;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageURL;
    }

    public void setImageUrl(String imageUrl) {
        this.imageURL = imageUrl;
    }
}

