package com.fxd.wangluo.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * user_id user_head_img user_name user_email user_password user_label
 */
public class UserBean {
    private int localId;
    @SerializedName("user_id")
    private int netUserId;
    @SerializedName("user_head_img")
    private String netHeadImg;
    @SerializedName("user_name")
    private String netUserName;
    @SerializedName("user_email")
    private String netEmail;
    @SerializedName("user_password")
    private String netPassWord;
    @SerializedName("user_label")
    private String netLabel;

    public UserBean(int localId, int netUserId, String netHeadImg, String netUserName, String netEmail, String netPassWord, String netLabel) {
        this.localId = localId;
        this.netUserId = netUserId;
        this.netHeadImg = netHeadImg;
        this.netUserName = netUserName;
        this.netEmail = netEmail;
        this.netPassWord = netPassWord;
        this.netLabel = netLabel;
    }

    public UserBean() {

    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getNetUserId() {
        return netUserId;
    }

    public void setNetUserId(int netUserId) {
        this.netUserId = netUserId;
    }

    public String getNetHeadImg() {
        return netHeadImg;
    }

    public void setNetHeadImg(String netHeadImg) {
        this.netHeadImg = netHeadImg;
    }

    public String getNetUserName() {
        return netUserName;
    }

    public void setNetUserName(String netUserName) {
        this.netUserName = netUserName;
    }

    public String getNetEmail() {
        return netEmail;
    }

    public void setNetEmail(String netEmail) {
        this.netEmail = netEmail;
    }

    public String getNetPassWord() {
        return netPassWord;
    }

    public void setNetPassWord(String netPassWord) {
        this.netPassWord = netPassWord;
    }

    public String getNetLabel() {
        return netLabel;
    }

    public void setNetLabel(String netLabel) {
        this.netLabel = netLabel;
    }
}
