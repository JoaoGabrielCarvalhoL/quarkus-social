package br.com.carv.social.model;

import br.com.carv.social.dto.PostResponse;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "POSTS")
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false, name = "post_text")
    private String text;

    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post() {
    }

    public Post(String text, LocalDateTime dataTime, User user) {
        this.text = text;
        this.dateTime = dataTime;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    public void getLocalDateTime() {
        setDateTime(LocalDateTime.now());
    }

}
