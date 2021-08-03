package com.privateboat.forum.backend.util.audit;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.privateboat.forum.backend.cloudclient.BaiduClient;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TextAuditUtil {
    private static final Integer OK_CONCLUSION_ID = 1;
    private static final Integer SUSPECT_CONCLUSION_ID = 3;
    private static final AipContentCensor client = BaiduClient.getClient();

    public static TextAuditResult auditText(String text) {
        JSONObject response = client.textCensorUserDefined(text);
        int conclusionId = response.getInt("conclusionType");
        if (conclusionId == OK_CONCLUSION_ID || conclusionId == SUSPECT_CONCLUSION_ID) {
            return TextAuditResult.ok();
        } else {
            JSONArray jsonArray = new JSONArray(response.get("data").toString());
            return TextAuditResult.notOk(new JSONObject(jsonArray.get(0).toString()).get("msg").toString());
        }
    }
}
