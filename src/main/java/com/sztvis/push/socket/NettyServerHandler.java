package com.sztvis.push.socket;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.sztvis.push.service.ICService;
import com.sztvis.push.socket.vo.BaseMsg;
import com.sztvis.push.socket.vo.ConteenMsg;
import com.sztvis.push.util.DateUtil;
import com.sztvis.push.util.SpringContextUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * 空闲次数
     **/
    private AtomicInteger idle_count = new AtomicInteger(1);

    /**
     * 发送次数
     **/
    private AtomicInteger count = new AtomicInteger(1);

    private ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();

    @Autowired
    private onChannelOperation mListener;

    @Autowired
    public NettyServerHandler(onChannelOperation mListener) {
        this.mListener = mListener;
    }

    private ChannelHandlerContext channelHandler;

    private String equipId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    private ICService icService = (ICService) applicationContext.getBean(ICService.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            //如果读通道处于空闲状态，说明没有收到心跳命令
            if (IdleState.READER_IDLE.equals(event.state())) {
                log.info("已经60秒没有接收到客户端的信息了");
                if (idle_count.get() > 1) {
                    log.info("关掉不活跃的通道" + this.getEquipId());
                    ctx.channel().close();
                    mListener.onRemoveChannel(this);
                }
                idle_count.getAndIncrement();
            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        mListener.onRemoveChannel(this);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("接收到客户端数据:" + msg);
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] data = new byte[buf.readableBytes()];
//        buf.readBytes(data);
        String request = (String) msg;//new String(data, "utf-8");
        log.info("request:" + request);
        dealData(ctx, request);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            mListener.onRemoveChannel(this);
            ctx.close();
        }
    }

    public void sendDataAPI(String equipId, String sendData) {
        if (channelHandler != null) {
            channelHandler.writeAndFlush(Unpooled.copiedBuffer(sendData.getBytes()));
            log.info("向设备" + equipId + "发送了数据:" + sendData);
        }
    }

    private void dealData(ChannelHandlerContext ctx, String msg) {
        System.out.println(DateUtil.getCurrentTime() + "->  " + msg);
        try {
            channelHandler = ctx;
            BaseMsg baseMsg = JSON.parseObject(msg, BaseMsg.class);
//            if (StringUtils.isEmpty(this.getEquipId())) {
//                mListener.removeOtherChannel(baseMsg.getSn());
//            }
            this.setEquipId(baseMsg.getSn());
            switch (baseMsg.getType()) {
                case SocketType.HEALTH:
                    break;
                case SocketType.CONTEEN:
                    ConteenMsg conteenMsg = JSON.parseObject(baseMsg.getMsg().toString(), ConteenMsg.class);
                    //客餐
                    if (conteenMsg.isSubmit()) {
                        icService.saveConteen(conteenMsg);
                    }
                    String in_dev_sn = icService.getSnBySn(baseMsg.getSn());
                    EchoServer.sendData(in_dev_sn, msg);
                    break;
                case SocketType.CONTEEN_BIND:

                    break;

            }
            //从信息中获取设备ID
            //equipId = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ChannelHandlerContext getChannelHandler() {
        return channelHandler;
    }

    public void setChannelHandler(ChannelHandlerContext channelHandler) {
        this.channelHandler = channelHandler;
    }

    public String getEquipId() {
        return equipId;
    }

    public void setEquipId(String equipId) {
        this.equipId = equipId;
    }
}
