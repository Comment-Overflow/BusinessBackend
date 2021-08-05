package com.privateboat.forum.backend.controller;

import com.privateboat.forum.backend.util.image.ImageUtil;
import com.privateboat.forum.backend.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    @GetMapping(value = "/images/{folderName}/{fileName}")
    @JWTUtil.Authentication(type = JWTUtil.AuthenticationType.PASS)
    ResponseEntity<byte[]> getImage(
            @PathVariable("folderName") String folderNameWithoutSlash,
            @PathVariable("fileName") String fileName) {
        return ResponseEntity.ok(ImageUtil.downloadImage(fileName, folderNameWithoutSlash + "/"));
    }

}
