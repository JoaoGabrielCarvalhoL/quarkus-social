package br.com.carv.social.dto;

import java.time.LocalDateTime;

public class PostResponse {
    private String text;
    private LocalDateTime dateTime;

    public PostResponse() {

    }

    public PostResponse(String text, LocalDateTime dateTime) {
        this.text = text;
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime localDateTime) {
        this.dateTime = localDateTime;
    }
}
