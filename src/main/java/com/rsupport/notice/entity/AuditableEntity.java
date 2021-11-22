package com.rsupport.notice.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class AuditableEntity extends IdentifiableEntity {

    @CreatedDate
    private LocalDateTime createdAt;

}
