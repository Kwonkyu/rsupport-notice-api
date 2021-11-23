package com.rsupport.notice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.time.LocalDateTime;

@RedisHash("noticePostHit")
@Getter
@Setter
@AllArgsConstructor
public class NoticePostHit {

    @Id private long id;
    private int hit;
    private LocalDateTime lastWriteBackTime;

    public void increaseHit() {
        hit++;
    }

    public boolean isOutdated() {
        return LocalDateTime.now().minusMinutes(1).isAfter(lastWriteBackTime);
    }

    public void refreshLastWriteBackTime() {
        lastWriteBackTime = LocalDateTime.now();
    }

}
