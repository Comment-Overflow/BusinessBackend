package com.privateboat.forum.backend.cloudclient;

import com.baidu.aip.contentcensor.AipContentCensor;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaiduClient {
    private static final String TEXT_AUDIT_HOST = "https://aip.baidubce.com/oauth/2.0/token?";
    private static final String APP_ID = "24640861";
    private static final String API_KEY = "pSG1FAHhdoDskf02DKWgj6vW";
    private static final String SECRET_KEY = "a2yexzd8XkAewoekLZd2y0nZnQAv0Nrm";

    private static AipContentCensor client;

    // Seems the token is not required at all. But store it just in case.
    private String accessToken;

    synchronized public static AipContentCensor getClient() {
        if (client == null) {
            client = new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
        }
        return client;
    }

    @PostConstruct
    @Scheduled(cron = "* * * */20 * *")
    private void getToken() {
        String getAccessTokenUrl = TEXT_AUDIT_HOST
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + API_KEY
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + SECRET_KEY;
        try {
            // Open connection.
            URL realUrl = new URL(getAccessTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // Read response.
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            JSONObject jsonObject = new JSONObject(result.toString());
            accessToken = jsonObject.getString("access_token");
        } catch (Exception e) {
            // Failed to get token, the system is fucked up.
        }
    }
}
