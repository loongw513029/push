package com.sztvis.push.service.Impl;


import com.sztvis.push.domain.CanteenMealRecord;
import com.sztvis.push.domain.Users;
import com.sztvis.push.mapper.Mapper;
import com.sztvis.push.service.ICService;
import com.sztvis.push.socket.vo.ConteenMsg;
import com.sztvis.push.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class CService implements ICService {
    @Autowired
    private Mapper mapper;
    @Value("${staticUrl}")
    private String staticUrl;

    @Override
    public String getSnBySn(String out_dev_sn) {
        return this.mapper.getSnBySn(out_dev_sn);
    }

    @Override
    public void saveConteen(ConteenMsg conteenMsg) {
        if (conteenMsg.getType() == 3) {
            CanteenMealRecord record = new CanteenMealRecord(0L, 0L, conteenMsg.getName(), "", "", conteenMsg.getType(),
                    "", conteenMsg.getMoney(), DateUtil.getCurrentTime());
            this.mapper.saveConteen(record);
        } else {
            Users user = this.mapper.getUserByWorkNo(conteenMsg.getWork_no());
            CanteenMealRecord record = new CanteenMealRecord(0L, 0, user.getUser_name(), user.getWork_no(),
                    user.getPicture().startsWith("http://") ? user.getPicture() : staticUrl + user.getPicture(), conteenMsg.getType(),
                    user.getDept_name(), conteenMsg.getMoney(), DateUtil.getCurrentTime());
            this.mapper.saveConteen(record);
        }
    }


}
