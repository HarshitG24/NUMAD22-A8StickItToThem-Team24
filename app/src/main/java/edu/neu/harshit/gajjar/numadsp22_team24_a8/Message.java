package edu.neu.harshit.gajjar.numadsp22_team24_a8;

public class Message {
    String datetime;
    String username;
    int sticker;

    public Message(String datetime, String username, int sticker) {
        this.datetime = datetime;
        this.username = username;
        this.sticker = sticker;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getUsername() {
        return username;
    }

    public int getSticker() {
        return sticker;
    }
}
