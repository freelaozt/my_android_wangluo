package com.fxd.wangluo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LaoZhang on 2018/2/16.
 */
public class LabelBean {
    @SerializedName("label_id")
    public int labelId;
    @SerializedName("label_title")
    public String labelTitle;

    public LabelBean(int labelId, String labelTitle) {
        this.labelId = labelId;
        this.labelTitle = labelTitle;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public String getLabelTitle() {
        return labelTitle;
    }

    public void setLabelTitle(String labelTitle) {
        this.labelTitle = labelTitle;
    }
}
