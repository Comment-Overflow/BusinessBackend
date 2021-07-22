package com.privateboat.forum.backend.util;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.apache.commons.lang3.RandomStringUtils;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.*;

import static com.privateboat.forum.backend.util.Constant.SECRET_ID;
import static com.privateboat.forum.backend.util.Constant.SECRET_KEY;

public class EmailUtil {
    private static final String URL = "ses.tencentcloudapi.com";
    private static final String REGION = "ap-hongkong";
    private static final String SUBJECT = "有可奉告论坛注册";
    private static final String FROM_EMAIL_ADDRESS = "私有船开发团队 <comment_overflow@gun9nir.me>";

    private static final Long TEMPLATE_ID = 17300L;

    public static String sendEmail(String email) {
        String confirmationCode = RandomStringUtils.randomNumeric(6);

        try{
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(URL);

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            SesClient client = new SesClient(cred, REGION, clientProfile);
            // Instantiate request object.
            SendEmailRequest req = new SendEmailRequest();
            req.setFromEmailAddress(FROM_EMAIL_ADDRESS);

            String[] destination = {email};
            req.setDestination(destination);

            Template template = new Template();
            template.setTemplateID(TEMPLATE_ID);
            template.setTemplateData(
                    String.format(
                            "{\"code\": \"%s\", \"expireMinutes\": \"%d\"}",
                            confirmationCode,
                            Constant.EMAIL_EXPIRE_MINUTES
                    )
            );
            req.setTemplate(template);

            req.setSubject(SUBJECT);

            SendEmailResponse resp = client.SendEmail(req);
            System.out.println(SendEmailResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            // TODO: Handle error if gzd has time.
        }

        return confirmationCode;
    }
}
