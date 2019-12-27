package com.sztvis.push.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanteenMealRecord {
    private long id;
    private long driver_id;
    private String driver_name;
    private String work_no;
    private String driver_photo;
    private int  type;
    private String depart_name;
    private int money;
    private String record_time;
}