package com.ifeng.core.distribute.message;

import java.io.Serializable;

/**
 * Created by zhanglr on 2016/8/29.
 */
public class TaskMessage implements Serializable{
    private String taskId;
    private String from;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
