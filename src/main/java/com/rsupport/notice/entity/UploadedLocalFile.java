package com.rsupport.notice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uploaded_local_file")
@EntityListeners(AuditingEntityListener.class)
public class UploadedLocalFile extends IdentifiableEntity {

    @Column(name = "hash", nullable = false)
    private String fileHashString;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "location", nullable = false)
    private String fileLocation;

}