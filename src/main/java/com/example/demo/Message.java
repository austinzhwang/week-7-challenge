package com.example.demo;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime timeStamp;

    @NotNull
    private String img;

    @ManyToOne
    private User user;

    public Message() {
        timeStamp = LocalDateTime.now();
        user = new User();
    }

    public Message(@NotNull String content, LocalDateTime timeStamp, @NotNull String img, User user) {
        this.content = content;
        this.timeStamp = timeStamp;
        this.img = img;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
