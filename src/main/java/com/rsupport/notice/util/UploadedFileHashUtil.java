package com.rsupport.notice.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class UploadedFileHashUtil {

    public String hash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        byte[] bytes = file.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(bytes);
        byte[] hashed = md5.digest();
        return DatatypeConverter.printHexBinary(hashed);
    }

}
