package com.privateboat.forum.backend.util.audit;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.privateboat.forum.backend.cloudclient.BaiduClient;
import com.privateboat.forum.backend.configuration.GeneralConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class TextAuditUtil {
    private static final Integer OK_CONCLUSION_ID = 1;
    private static final Integer SUSPECT_CONCLUSION_ID = 3;
    private static final AipContentCensor client = BaiduClient.getClient();

    public static TextAuditResult auditText(String text) {
        if (GeneralConfig.enableAudition) {
            JSONObject response = client.textCensorUserDefined(text);

            // Treat the result as ok on error.
            if (!response.has("conclusionType")) {
                return TextAuditResult.ok();
            }

            int conclusionId = response.getInt("conclusionType");
            if (conclusionId == OK_CONCLUSION_ID || conclusionId == SUSPECT_CONCLUSION_ID) {
                return TextAuditResult.ok();
            } else {
                JSONArray jsonArray = new JSONArray(response.get("data").toString());
                return TextAuditResult.notOk(new JSONObject(jsonArray.get(0).toString()).get("msg").toString());
            }
        } else {
            return TextAuditResult.ok();
        }
    }
}
