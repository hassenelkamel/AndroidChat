package com.elkamelhassen.chatgram;

import android.net.Uri;

import java.util.Date;

/**
 * Created by enigma on 27/03/2018.
 */

public class Message {

    private String content,username;
    private Date temps;
    private Uri url;
    public Message(){

    }

    public Message(String content,String username){
        this.content=content;
        this.username=username;
    }

    public Message(String content, String username, Date temps, Uri url) {
        this.content = content;
        this.username = username;
        this.temps = temps;
        this.url = url;
    }

    public Date getTemps() {
        return temps;
    }

    public void setTemps(Date temps) {
        this.temps = temps;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
