package com.fxd.wangluo.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LaoZhang on 2018/3/1.
 */
public class CommentBean {

    @SerializedName("id")
    private int id;
    @SerializedName("link_id")
    private String comLinkId;
    @SerializedName("comment_content")
    private String comContent;
    @SerializedName("comment_time")
    private String comTime;
    @SerializedName("user_id")
    private String comUserId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComLinkId() {
        return comLinkId;
    }

    public void setComLinkId(String comLinkId) {
        this.comLinkId = comLinkId;
    }

    public String getComContent() {
        return comContent;
    }

    public void setComContent(String comContent) {
        this.comContent = comContent;
    }

    public String getComTime() {
        return comTime;
    }

    public void setComTime(String comTime) {
        this.comTime = comTime;
    }

    public String getComUserId() {
        return comUserId;
    }

    public void setComUserId(String comUserId) {
        this.comUserId = comUserId;
    }
}
