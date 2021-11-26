package com.rsupport.notice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notice_post")
@EntityListeners(AuditingEntityListener.class)
public class NoticePost extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "noticed_from", nullable = false)
    private LocalDateTime noticedFrom = LocalDateTime.now();

    @Column(name = "noticed_until", nullable = false)
    private LocalDateTime noticedUntil = LocalDateTime.MAX;

    @Column(name = "hit", nullable = false)
    private int hit;

    @OneToMany(cascade = CascadeType.PERSIST)
    private final List<UploadedFile> attachedFiles = new ArrayList<>();

    public void addFile(UploadedFile file) {
        Assert.notNull(file, "Attaching file cannot be null.");
        this.attachedFiles.add(file);
    }

    public void removeFile(UploadedFile file) {
        Assert.notNull(file, "Removing file cannot be null.");
        this.attachedFiles.remove(file);
    }

    public void changeTitle(String title) {
        Assert.hasText(title, "Title string cannot be blank.");
        this.title = title;
    }

    public void changeContent(String content) {
        Assert.hasText(content, "Content string cannot be blank.");
        this.content = content;
    }

    public void changeNoticedFrom(LocalDateTime noticedFrom) {
        Assert.notNull(noticedFrom, "Notice date cannot be null.");
        if(noticedUntil.isBefore(noticedFrom)) throw new IllegalArgumentException("Noticing date cannot be reversed.");
        this.noticedFrom = noticedFrom;
    }

    public void changeNoticedUntil(LocalDateTime noticedUntil) {
        Assert.notNull(noticedUntil, "Notice date cannot be null.");
        if(noticedFrom.isAfter(noticedUntil)) throw new IllegalArgumentException("Noticing date cannot be reversed.");
        this.noticedUntil = noticedUntil;
    }

    public void writeBackHit(NoticePostHit hit) {
        Assert.notNull(hit, "Notice post hit cannot be null.");
        this.hit = hit.getHit();
        hit.refreshLastWriteBackTime();
    }

    @Builder
    public NoticePost(String title, String content, LocalDateTime noticedFrom, LocalDateTime noticedUntil) {
        changeTitle(title);
        changeContent(content);
        changeNoticedFrom(noticedFrom);
        changeNoticedUntil(noticedUntil);
        this.hit = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticePost that = (NoticePost) o;
        return hit == that.hit &&
                title.equals(that.title) &&
                content.equals(that.content) &&
                noticedFrom.equals(that.noticedFrom) &&
                noticedUntil.equals(that.noticedUntil) &&
                attachedFiles.equals(that.attachedFiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, noticedFrom, noticedUntil, hit, attachedFiles);
    }
}
