package com.fxd.wangluo.Bean;

/**
 * Character Bean数据
 */
public class Character {
    private String name;
    private String avatar;
    private String id;
    private String title;
    private String href;
    private String describe;
    private String time;
    private String label;
    private String userId;
    private String status;

    public Character(String name, String avatar, String id, String title, String href, String describe, String time, String label, String userId, String status) {
        this.name = name;
        this.avatar = avatar;
        this.id = id;
        this.title = title;
        this.href = href;
        this.describe = describe;
        this.time = time;
        this.label = label;
        this.userId = userId;
        this.status = status;
    }

    public Character() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
