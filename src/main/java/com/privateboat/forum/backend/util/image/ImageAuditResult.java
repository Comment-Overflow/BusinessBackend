package com.privateboat.forum.backend.util.image;

import com.qcloud.cos.model.ciModel.auditing.ImageAuditingResponse;


public class ImageAuditResult {
    private static final Integer SUSPECT_THRESHOLD = 60;
    private static final Integer CONFIRM_THRESHOLD = 90;

    private final ImageAuditResultType isPorn;
    private final ImageAuditResultType isAds;
    private final ImageAuditResultType isPolitics;
    private final ImageAuditResultType isTerrorism;

    public ImageAuditResult(ImageAuditingResponse response) {
        isPorn = getType(Integer.parseInt(response.getPornInfo().getScore()));
        isAds = getType(Integer.parseInt(response.getAdsInfo().getScore()));
        isPolitics = getType(Integer.parseInt(response.getPoliticsInfo().getScore()));
        isTerrorism = getType(Integer.parseInt(response.getTerroristInfo().getScore()));
    }

    public Boolean isOk() {
        return isPorn == ImageAuditResultType.OK &&
                isAds == ImageAuditResultType.OK &&
                isPolitics == ImageAuditResultType.OK &&
                isTerrorism == ImageAuditResultType.OK;
    }

    private ImageAuditResultType getType(Integer score) {
        if (score > CONFIRM_THRESHOLD) {
            return ImageAuditResultType.CONFIRM;
        } else if (score > SUSPECT_THRESHOLD) {
            return ImageAuditResultType.SUSPECT;
        } else {
            return ImageAuditResultType.OK;
        }
    }
}
