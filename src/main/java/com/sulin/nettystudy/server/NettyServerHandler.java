package com.sulin.nettystudy.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private String callBack;

    /**
     * @param ctx 上下文对象 包含了：pipeline channel
     * @param msg 客户端发送过来的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //将msg 转ByteBuff
        ByteBuf byteBuf = (ByteBuf) msg;
        //Charset.forName("UTF-8")
        callBack = byteBuf.toString(CharsetUtil.UTF_8);
        System.out.println("接收到客户端的消息:" + callBack);
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完成
     * 回复消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("call back"+callBack);
        ctx.writeAndFlush(Unpooled.copiedBuffer(callBack, CharsetUtil.UTF_8));
    }


    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.channel().close();
    }
}
