package com.sulin.nettystudy.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    public EventLoopGroup bossGroup = null;

    public EventLoopGroup workerGroup = null;

    public ServerBootstrap serverBootstrap;

    public ChannelFuture startFuture;

    public ChannelFuture closeFuture;

    public NettyServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
    }


    public void run() throws InterruptedException {
        serverBootstrap
                .group(bossGroup, workerGroup)
                //使用NIO server channel 通道
                .channel(NioServerSocketChannel.class)
                //线程队列得到的连接个数
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //给worker group 的管道设置处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast(new NettyServerHandler());
                    }
                });

        System.out.println(".... Netty Server is ready .....");

        startFuture = serverBootstrap.bind(9999).sync();

        closeFuture = startFuture.channel().closeFuture().sync();

    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        NettyServer nettyServer = null;
        try {
            nettyServer = new NettyServer();
            nettyServer.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nettyServer.stop();
        }

    }
}
