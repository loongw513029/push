package com.sztvis.push.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Drivers {
    private int id;
    private long driver_id;
    private String name;
    private String work_number;
    private String type;
    private String sex = "";
    private String line;
    private String cert_number = "";
    private String dept_code;
    private String dept_name;
    private String imgUrl;
    private long imgSize;
    private String ic_card;
    private int for_door;//2：表示用于门禁 1：用于司机考勤

    //是否是管理员
    private boolean adm;
    private String modify_time;
}
