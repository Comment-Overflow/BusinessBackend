package com.privateboat.forum.backend.cloudclient;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;

import static com.privateboat.forum.backend.util.Constant.SECRET_ID;
import static com.privateboat.forum.backend.util.Constant.SECRET_KEY;

public class TencentCOSClient {
    private static COSClient client;

    public static COSClient getClient() {
        if (client == null) {
            final COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
            final Region region = new Region("ap-shanghai");
            final ClientConfig clientConfig = new ClientConfig(region);
            clientConfig.setHttpProtocol(HttpProtocol.https);

            client = new COSClient(cred, clientConfig);
        }
        return client;
    }
}
