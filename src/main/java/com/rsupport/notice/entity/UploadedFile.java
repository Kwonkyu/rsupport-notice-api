package com.rsupport.notice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uploaded_local_file")
@EntityListeners(AuditingEntityListener.class)
public class UploadedFile extends IdentifiableEntity {

    @Column(name = "hash", nullable = false)
    private String fileHashString;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "location", nullable = false)
    private String fileLocation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadedFile that = (UploadedFile) o;
        return fileHashString.equals(that.fileHashString) && filename.equals(that.filename) && fileLocation.equals(that.fileLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileHashString, filename, fileLocation);
    }
}
