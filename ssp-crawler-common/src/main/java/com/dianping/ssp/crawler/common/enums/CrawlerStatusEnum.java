package com.dianping.ssp.crawler.common.enums;


public enum CrawlerStatusEnum {
	INIT(0, "初始化"),
    VALID(1, "有效"),
    NO_VALID(2, "无效");

    private int status;

    private String des;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    private CrawlerStatusEnum(int status, String des) {
        this.status = status;
        this.des = des;
    }
}
