package com.sztvis.push.socket.vo;

import lombok.Data;

@Data
public class ConteenMsg {
    private String name;
    private String url;
    private String depart;
    private long driver_id;
    private String work_no;
    private int type;
    private int money;
    private boolean submit;
}
