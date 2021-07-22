package com.privateboat.forum.backend.util;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class EmailUtil {
    @AllArgsConstructor
    private static class Template {
        Integer templateId;
        Map<String, Object> templateData;
    }

    private static final String URL = "ses.tencentcloudapi.com";

    private static final Map<String, String> map = new HashMap<>() {{
        put("Action", "SendEmail");
        put("Version", "2020-10-02");
        put("Region", "ap-hongkong!");
        put("FromEmailAddress", "私有船开发团队 <comment_overflow@gun9nir.me>");
        put("Subject", "有可奉告论坛注册");
    }};

    private final Integer templateId = 17300;

    public static String sendEmail(String email) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential("SecretId", "SecretKey");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ses.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SesClient client = new SesClient(cred, "ap-hongkong", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendEmailRequest req = new SendEmailRequest();
            req.setFromEmailAddress("私有船开发团队 <comment_overflow@gun9nir.me>");

            String[] destination1 = {"gungnir_guo@sjtu.edu.cn"};
            req.setDestination(destination1);

            Template template1 = new Template();
            template1.setTemplateID(17300L);
            template1.setTemplateData("{\"\": \"\", \"\": \"\"}");
            req.setTemplate(template1);

            req.setSubject("有可奉告论坛注册");
            // 返回的resp是一个SendEmailResponse的实例，与请求对象对应
            SendEmailResponse resp = client.SendEmail(req);
            // 输出json格式的字符串回包
            System.out.println(SendEmailResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}
