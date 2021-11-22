package com.rsupport.notice.entity;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notice_post")
public class NoticePost extends AuditableEntity{

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", length = 65535, nullable = false)
    private String content;

    @Column(name = "noticed_from", nullable = false)
    private LocalDateTime noticedFrom;

    @Column(name = "noticed_until", nullable = false)
    private LocalDateTime noticedUntil;

    @Column(name = "hit", nullable = false)
    private int hit;

    public void changeTitle(String title) {
        Assert.hasText(title, "Title string cannot be blank.");
        this.title = title;
    }

    public void changeContent(String content) {
        Assert.hasText(content, "Content string cannot be blank.");
        this.content = content;
    }

    public void changeNoticedFrom(@NonNull LocalDateTime noticedFrom) {
        this.noticedFrom = noticedFrom;
    }

    public void changeNoticedUntil(@NonNull LocalDateTime noticedUntil) {
        this.noticedUntil = noticedUntil;
    }

    public int increaseHit() {
        return ++hit;
    }

}
