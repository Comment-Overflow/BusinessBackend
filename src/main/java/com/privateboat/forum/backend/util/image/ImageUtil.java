package com.privateboat.forum.backend.util.image;

import com.privateboat.forum.backend.cloudclient.TencentCOSClient;
import com.privateboat.forum.backend.configuration.GeneralConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Component
public class ImageUtil {

    private static final String BASE_KEY = "images/";
    private static final String BUCKET_NAME = "comment-overflow-1306578009";
    private static final COSClient client = TencentCOSClient.getClient();

    static public void uploadImage(MultipartFile file, String fileName, String folderName) throws ImageUploadException {
        uploadImage(file, fileName, folderName, true);
    }

    static public void uploadImage(MultipartFile file, String fileName, String folderName, Boolean shouldAudit) throws ImageUploadException {
        // Convert multipart file to InputStream.
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new ImageUploadException(ImageUploadException.ExceptionType.NETWORK_ERROR);
        }

        // Specify the path to store on COS. File name should include extension.
        String key = BASE_KEY + folderName + fileName;
        String thumbnailKey = fileName.substring(0, fileName.lastIndexOf('.')) +
                              "-tbn" +
                              fileName.substring(fileName.lastIndexOf('.'));
        // Get object metadata.
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream, objectMetadata);

        // Generate thumbnail for the picture, with 40% of the original quality.
        List<PicOperations.Rule> ruleList = new LinkedList<>();
        PicOperations.Rule rule = new PicOperations.Rule();
        rule.setBucket(BUCKET_NAME);
        rule.setFileId(thumbnailKey);
        rule.setRule("imageView2/rq/40");
        ruleList.add(rule);

        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        picOperations.setRules(ruleList);
        putObjectRequest.setPicOperations(picOperations);

        try {
            client.putObject(putObjectRequest);
        } catch (CosClientException e){
            throw new ImageUploadException(ImageUploadException.ExceptionType.NETWORK_ERROR);
        }

        if (shouldAudit) {
            ImageAuditResult result = auditImage(key);
            if (!result.isOk()) {
                throw new ImageAuditException(result);
            }
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

    static private ImageAuditResult auditImage(String key) {
        if (GeneralConfig.enableAudition) {
            ImageAuditingRequest request = new ImageAuditingRequest();
            request.setBucketName(BUCKET_NAME);
            request.setDetectType("porn,terrorist,ads,politics");
            request.setObjectKey(key);

            try {
                ImageAuditingResponse response = client.imageAuditing(request);
                return new ImageAuditResult(response);
            } catch (CosServiceException e) {
                return ImageAuditResult.pass();
            }
        } else {
            return ImageAuditResult.pass();
        }
    }
}