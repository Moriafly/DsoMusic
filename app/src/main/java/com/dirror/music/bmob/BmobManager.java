package com.dirror.music.bmob;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.dirror.music.util.TopLevelFuncationKt.toast;

/**
 * @author 24568
 */
public class BmobManager {

    /**
     * 上传反馈数据
     * @param feedback
     * @param contact
     */
    public void uploadFeedback(String feedback, String contact) {
        FeedbackData feedbackData = new FeedbackData();
        feedbackData.setFeedback(feedback);
        feedbackData.setContact(contact);
        feedbackData.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    toast("上传成功，感谢反馈");
                } else {
                    toast("上传失败，请稍后重试");
                }
            }
        });
    }
}
