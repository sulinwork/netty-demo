package com.sulin.nettystudy.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪时 触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("可以发送消息啦！");
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String msg = scanner.nextLine();
                    ctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
                }
            }
        }).start();


    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        String content = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("收到服务器返回的信息：" + content);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
