package com.sztvis.push.mapper;

import com.sztvis.push.domain.CanteenMealRecord;
import com.sztvis.push.domain.Drivers;
import com.sztvis.push.domain.Users;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;


@Repository
public interface Mapper {



    @Select("select in_dev_sn from canteen_dev_bind where out_dev_sn=#{out_dev_sn} order by id desc limit 0,1")
    String getSnBySn(String out_dev_sn);

    @Insert("insert into canteen_meal_records(driver_id,driver_name,depart_name,work_no,driver_photo,type,money,record_time)values(#{driver_id},#{driver_name},#{depart_name},#{work_no},#{driver_photo},#{type},#{money},#{record_time})")
    void saveConteen(CanteenMealRecord record);

    @Select("select * from drivers where driver_id=#{driver_id}")
    Drivers getDriverById(long driver_id);

    @Select("select * from drivers where work_number=#{workNumber}")
    Drivers getDriver(String workNumber);

    @Select("select * from users where work_no=#{work_no}")
    Users getUserByWorkNo(String work_no);
}
