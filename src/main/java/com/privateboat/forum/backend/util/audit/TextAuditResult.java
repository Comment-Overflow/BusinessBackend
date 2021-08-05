package com.privateboat.forum.backend.util.audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TextAuditResult {
    private final TextAuditResultType resultType;
    private final String reason;

    static public TextAuditResult ok() {
        return new TextAuditResult(TextAuditResultType.OK, "");
    }

    static public TextAuditResult notOk(String reason) {
        return new TextAuditResult(TextAuditResultType.NOT_OK, reason);
    }
}
