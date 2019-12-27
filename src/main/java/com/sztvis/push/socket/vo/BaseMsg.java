package com.sztvis.push.socket.vo;

import lombok.Data;

@Data
public class BaseMsg{
    private int type;
    private String sn;
    private Object msg;
}
