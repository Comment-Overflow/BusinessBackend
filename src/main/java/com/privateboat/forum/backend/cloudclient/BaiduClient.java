package com.privateboat.forum.backend.cloudclient;

import com.baidu.aip.contentcensor.AipContentCensor;

public class BaiduClient {
    private static final String APP_ID = "24640861";
    private static final String API_KEY = "pSG1FAHhdoDskf02DKWgj6vW";
    private static final String SECRET_KEY = "a2yexzd8XkAewoekLZd2y0nZnQAv0Nrm";

    private static AipContentCensor client;

    synchronized public static AipContentCensor getClient() {
        if (client == null) {
            client = new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
        }
        return client;
    }
}
