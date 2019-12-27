package com.sztvis.push.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EchoServer implements onChannelOperation {


    private static final Logger log = LoggerFactory.getLogger(EchoServer.class);
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static final ConcurrentHashMap<NettyServerHandler, SocketChannel> channelMap = new ConcurrentHashMap<>();

    public void run() {
        try {
            Thread t = Thread.currentThread();
            log.info("run() in EchoServer" + Calendar.getInstance().getTime() + "____" + t.getName());
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf byteBuf = Unpooled.copiedBuffer("$".getBytes());
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, byteBuf));
                            socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                            socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                            System.out.println("有客户端连接了:" + socketChannel);
                            NettyServerHandler scobj = new NettyServerHandler(EchoServer.this);
                            socketChannel.pipeline().addLast(scobj);
                            channelMap.put(scobj, socketChannel);
                            System.out.println("socket通道数量:" + "--" + channelMap.size());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 32 * 1024)// 设置TCP缓冲区
                    .option(ChannelOption.SO_SNDBUF, 64 * 1024) //发送数据缓冲区
                    .option(ChannelOption.SO_RCVBUF, 64 * 1024) //接收数据缓冲区
                    .childOption(ChannelOption.SO_KEEPALIVE, true);//保持连接

            ChannelFuture future = bootstrap.bind(3333).sync();
            log.info("服务器已启动,端口：" + 3333);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("服务器启动失败");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            //log.info(“);
        }
    }

    @PreDestroy
    public void destory() {
        log.info("正在尝试关闭Netty");
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        channelMap.clear();
        log.info("关闭成功");
    }

    @Override
    public void onRemoveChannel(NettyServerHandler obj) {
        channelMap.remove(obj);
        System.out.println("移除链接！！！:"+obj.getEquipId());
    }

    @Override
    public void removeOtherChannel(String dev_sn) {
        for (Map.Entry<NettyServerHandler, SocketChannel> entry : channelMap.entrySet()) {
            if (entry.getKey().getEquipId().equals(dev_sn)) {
                channelMap.remove(entry.getKey());
                System.out.println("移除链接！！！:"+dev_sn);
            }
        }
    }

    /**
     * 检测设备是否存活
     *
     * @param dev_sn
     * @return
     */
    public static boolean socketIsAlive(String dev_sn) {
        for (Map.Entry<NettyServerHandler, SocketChannel> entry : channelMap.entrySet()) {
            if (entry.getKey().getEquipId().equals(dev_sn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送数据
     *
     * @param dev_sn
     * @param msg
     */
    public static void sendData(String dev_sn, String msg) {
        if (socketIsAlive(dev_sn)) {
            for (Map.Entry<NettyServerHandler, SocketChannel> entry : channelMap.entrySet()) {
                if (entry.getKey().getEquipId().equals(dev_sn)) {
                    entry.getKey().sendDataAPI(dev_sn, msg);
                }
            }
        }
    }


}
