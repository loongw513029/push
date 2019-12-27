package com.sztvis.push.socket;

public interface onChannelOperation {


    void onRemoveChannel(NettyServerHandler obj);

    void removeOtherChannel(String dev_sn);
}
