package com.microservice.enums;

/**
 * @author zw
 * @date 2020-10-02
 * <p>
 */
public enum HostStatusEnum {

    /**
     * up（在线）
     */
    UP("UP", "在线"),
    /**
     * down（离线）
     */
    DOWN("DOWN", "离线"),
    /**
     * unknown（未知）
     */
    UNKNOWN("UNKNOWN", "未知");

    private String type;
    private String desc;

    HostStatusEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
