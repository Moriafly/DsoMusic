package com.dirror.music.bmob;

import cn.bmob.v3.BmobObject;

/**
 * @author 24568
 */
public class FeedbackData extends BmobObject {
    private String feedback;
    private String contact;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
