package com.rsupport.notice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadedFileUtil {

    public static String hash(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            byte[] hashed = md5.digest();
            return DatatypeConverter.printHexBinary(hashed);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("{} occur when hashing file {} - {}", e.getClass(), file.getOriginalFilename(), e.getMessage());
            return "";
        }
    }

    // https://stackoverflow.com/questions/24339990/how-to-convert-a-multipart-file-to-file
    public static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(String.format("%s/%s", System.getProperty("java.io.tmpdir"), fileName));
        multipart.transferTo(convFile);
        return convFile;
    }

}
