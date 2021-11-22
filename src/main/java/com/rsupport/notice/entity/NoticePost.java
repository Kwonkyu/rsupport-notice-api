package com.rsupport.notice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notice_post")
public class NoticePost extends AuditableEntity{

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", length = 65535, nullable = false)
    private String content;

    @Column(name = "noticed_from", nullable = false)
    private LocalDateTime noticedFrom = LocalDateTime.now();

    @Column(name = "noticed_until", nullable = false)
    private LocalDateTime noticedUntil = LocalDateTime.MAX;

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

    @Builder
    public NoticePost(String title, String content, LocalDateTime noticedFrom, LocalDateTime noticedUntil) {
        changeTitle(title);
        changeContent(content);
        changeNoticedFrom(noticedFrom);
        changeNoticedUntil(noticedUntil);
        this.hit = 0;
    }
}
