package br.com.carv.social.dto;

public class CreatePostRequest {

    private String text;

    public CreatePostRequest() {

    }

    public CreatePostRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
