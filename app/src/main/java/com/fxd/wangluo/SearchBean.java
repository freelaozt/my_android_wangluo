package com.fxd.wangluo;

import com.google.gson.annotations.SerializedName;

//解析JSON字符串的对象存的bean缓冲池
public class SearchBean {
    @SerializedName("id")
    public int linkId;
    @SerializedName("title")
    public String linkTitle;
    @SerializedName("href")
    public String linkHref;
    @SerializedName("describe")
    public String linkDescribe;
    @SerializedName("time")
    public String linkTime;
    @SerializedName("label")
    public String linkLabel;
    @SerializedName("user_id")
    public String linkUserId;

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public void setLinkHref(String linkHref) {
        this.linkHref = linkHref;
    }

    public String getLinkDescribe() {
        return linkDescribe;
    }

    public void setLinkDescribe(String linkDescribe) {
        this.linkDescribe = linkDescribe;
    }

    public String getLinkTime() {
        return linkTime;
    }

    public void setLinkTime(String linkTime) {
        this.linkTime = linkTime;
    }

    public String getLinkLabel() {
        return linkLabel;
    }

    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }

    public String getLinkUserId() {
        return linkUserId;
    }

    public void setLinkUserId(String linkUserId) {
        this.linkUserId = linkUserId;
    }
}
