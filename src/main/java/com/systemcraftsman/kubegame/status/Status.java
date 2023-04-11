package com.systemcraftsman.kubegame.status;

import java.io.Serializable;

public class Status {

    private boolean ready = false;
    private String msg = "";

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
