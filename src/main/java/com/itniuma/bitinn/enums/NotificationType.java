package com.itniuma.bitinn.enums;

/**
 * 通知类型枚举
 */
public enum NotificationType {
    LIKE("like", "赞了你的文章"),
    FAVORITE("favorite", "收藏了你的文章"),
    COMMENT("comment", "评论了你的文章"),
    FOLLOW("follow", "关注了你"),
    REPOST("repost", "转发了你的文章");

    private final String code;
    private final String description;

    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
