package com.privateboat.forum.backend.util.image;

import com.privateboat.forum.backend.cloudclient.TencentCOSClient;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageUtil {

    private static final String BASE_KEY = "images/";
    private static final String BUCKET_NAME = "comment-overflow-1306578009";
    private static final COSClient client = TencentCOSClient.getClient();

    static public Boolean uploadImage(MultipartFile file, String fileName, String folderName) {
        // Convert multipart file to InputStream.
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            return false;
        }

        // Specify the path to store on COS. File name should include extension.
        String key;
        key = BASE_KEY + folderName + fileName;

        // Get object metadata.
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream, objectMetadata);

        try {
            client.putObject(putObjectRequest);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    static public byte[] downloadImage(String fileName, String folderName) throws RuntimeException {
        // Specify the path to store on COS. File name should include extension.
        String key = BASE_KEY + folderName + fileName;
        // Acquire download input steam.
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, key);
        COSObject cosObject = client.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

        try {
            // Get the image in the form of byte stream.
            return cosObjectInput.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static public String getNewImageName(MultipartFile file) {
        String originName = file.getOriginalFilename();
        assert originName != null;
        String suffix = originName.substring(originName.lastIndexOf("."));
        return RandomStringUtils.randomAlphanumeric(12) + suffix;
    }

    static public ImageAuditResult auditImage(String key) {
        ImageAuditingRequest request = new ImageAuditingRequest();
        request.setBucketName(BUCKET_NAME);
        request.setDetectType("porn, terrorist, ads, politics");
        request.setObjectKey(key);
        
        ImageAuditingResponse response = client.imageAuditing(request);
        return new ImageAuditResult(response);
    }
}