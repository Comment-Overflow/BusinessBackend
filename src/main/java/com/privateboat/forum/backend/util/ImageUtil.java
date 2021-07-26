package com.privateboat.forum.backend.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.sun.istack.Nullable;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.privateboat.forum.backend.util.Constant.SECRET_ID;
import static com.privateboat.forum.backend.util.Constant.SECRET_KEY;

@Component
public class ImageUtil {
    private static final String BASE_KEY = "images/";
    private static final COSCredentials CRED;
    private static final ClientConfig CLIENT_CONFIG;
    private static final String BUCKET_NAME;

    static {
        CRED = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        Region region = new Region("ap-shanghai");
        CLIENT_CONFIG = new ClientConfig(region);
        CLIENT_CONFIG.setHttpProtocol(HttpProtocol.https);
        BUCKET_NAME = "comment-overflow-1306578009";
    }

    static public Boolean uploadImage(MultipartFile file, String fileName, String folderName) {
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
        String key;
        key = BASE_KEY + folderName + fileName;

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

    static public byte[] downloadImage(String fileName, String folderName) throws RuntimeException {
        // Specify the path to store on COS. File name should include extension.
        String key = BASE_KEY + folderName + fileName;
        // Acquire download input steam.
        COSClient cosClient = new COSClient(CRED, CLIENT_CONFIG);
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, key);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

        try {
            // Get the image in the form of byte stream.
            byte[] res = cosObjectInput.readAllBytes();
            cosClient.shutdown();
            return res;
        } catch (IOException e) {
            cosClient.shutdown();
            throw new RuntimeException();
        }
    }

    static public String getNewImageName(MultipartFile file) {
        String originName = file.getOriginalFilename();
        assert originName != null;
        String suffix = originName.substring(originName.lastIndexOf("."));
        return RandomStringUtils.randomAlphanumeric(12) + suffix;
    }

}