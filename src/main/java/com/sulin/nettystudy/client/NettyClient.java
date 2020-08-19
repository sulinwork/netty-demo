package com.sulin.nettystudy.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Scanner;

public class NettyClient {

    private Bootstrap bootstrap = null;

    private ChannelFuture channelFuture = null;

    private EventLoopGroup workerGroup = null;

    public NettyClient() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
    }

    public void run() throws InterruptedException {
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast("encoder", new StringEncoder());
                        channel.pipeline().addLast("decoder", new StringDecoder());
                        channel.pipeline().addLast(new NettyClientHandler());
                    }
                });
        System.out.println("..... client is ready ......");
        channelFuture = bootstrap.connect("localhost", 9999).sync();

        Channel channel = channelFuture.channel();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            channel.writeAndFlush(line);
        }

//        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                if (channelFuture.isSuccess()) {
//                    System.out.println("连接成功");
//                    new Thread(()->{
//                        Scanner scanner = new Scanner(System.in);
//                        while (true) {
//                            String line = scanner.nextLine();
//                            channelFuture.channel().writeAndFlush(line);
//                        }
//                    }).start();
//
//                } else {
//                    System.out.println("连接失败");
//                }
//            }
//        });

        ChannelFuture closeSync = channelFuture.channel().closeFuture().sync();
    }

    public void stop() {
        workerGroup.shutdownGracefully();
    }


    public static void main(String[] args) {
        NettyClient nettyClient = null;
        try {
            nettyClient = new NettyClient();
            nettyClient.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nettyClient.stop();
        }
    }
}
