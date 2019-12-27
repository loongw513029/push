package com.sztvis.push.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    private String user_name;
    private String work_no;
    private String dept_name;
    private String picture;
    private String ic_no_16;
}
