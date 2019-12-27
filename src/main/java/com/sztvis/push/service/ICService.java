package com.sztvis.push.service;


import com.sztvis.push.socket.vo.ConteenMsg;

public interface ICService {


    String getSnBySn(String out_dev_sn);

    void saveConteen(ConteenMsg conteenMsg);

}
