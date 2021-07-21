package com.privateboat.forum.backend.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageUtil {
    private static final String BASE_KEY = "images/";
    private static final COSCredentials CRED;
    private static final ClientConfig CLIENT_CONFIG;
    private static final String BUCKET_NAME;

    static {
        String secretId = "AKIDLVP3QWLtBDzVkiPpyrUqvjGWWEfpiLhm";
        String secretKey = "qQxEEwOZS5RWKsABuMXS6TZPyFFzSeZD";
        CRED = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region("ap-shanghai");
        CLIENT_CONFIG = new ClientConfig(region);
        CLIENT_CONFIG.setHttpProtocol(HttpProtocol.https);
        BUCKET_NAME = "comment-overflow-1306578009";
    }

    Boolean uploadImage(MultipartFile file, String fileName) {
        // Convert multipart file to InputStream.
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            return false;
        }

        // Open client to COS.
        COSClient cosClient = new COSClient(CRED, CLIENT_CONFIG);

        // Specify the path to store on COS. File name should include extension.
        String key = BASE_KEY + fileName;
        // Get object metadata.
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream, objectMetadata);

        try {
            cosClient.putObject(putObjectRequest);
            cosClient.shutdown();
            return true;
        } catch (RuntimeException e) {
            cosClient.shutdown();
            return false;
        }
    }

    byte[] downloadImage(String fileName) throws RuntimeException {
        // Specify the path to store on COS. File name should include extension.
        String key = BASE_KEY + fileName;
        // Acquire download input steam.
        COSClient cosClient = new COSClient(CRED, CLIENT_CONFIG);
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, key);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

        try {
            // Get the image in the form of byte stream.
            byte[] res = cosObjectInput.readAllBytes();

            // Debug: save to local file.
            FileOutputStream fos = new FileOutputStream("/Users/david/Desktop/testDownload.jpeg");
            fos.write(res);
            cosObjectInput.close();

            cosClient.shutdown();
            return res;
        } catch (IOException e) {
            cosClient.shutdown();
            throw new RuntimeException();
        }
    }


}