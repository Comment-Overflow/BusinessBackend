package com.privateboat.forum.backend.util.audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuditResult {
    private final AuditResultType resultType;
    private final String reason;

    static public AuditResult ok() {
        return new AuditResult(AuditResultType.OK, "");
    }

    static public AuditResult notOk(String reason) {
        return new AuditResult(AuditResultType.NOT_OK, reason);
    }
}
