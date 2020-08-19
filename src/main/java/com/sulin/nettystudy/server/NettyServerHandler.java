package com.sulin.nettystudy.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.nio.charset.Charset;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    /**
     * 管理channel
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

//    /**
//     * @param ctx 上下文对象 包含了：pipeline channel
//     * @param msg 客户端发送过来的数据
//     * @throws Exception
//     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //将msg 转ByteBuff
//        ByteBuf byteBuf = (ByteBuf) msg;
//        //Charset.forName("UTF-8")
//        callBack = byteBuf.toString(CharsetUtil.UTF_8);
//        System.out.println("接收到客户端的消息:" + callBack);
//        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
//    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[客户端]" + ctx.channel().remoteAddress() + "加入聊天");
        channelGroup.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "加入聊天");
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[客户端]" + ctx.channel().remoteAddress() + "上线啦~");
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[客户端]" + ctx.channel().remoteAddress() + "离线啦~");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[客户端]" + ctx.channel().remoteAddress() + "退出聊天");
        channelGroup.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "退出聊天");
        channelGroup.remove(ctx.channel());
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {

        //channelHandlerContext.channel().writeAndFlush(s);
        //channelGroup.writeAndFlush("[客户端]" + channelHandlerContext.channel().remoteAddress() + "发送消息：" + s);
        //转发即可
        channelGroup.forEach(x -> {
            if (x != channelHandlerContext.channel()) {
//                System.out.println("read:" + s);
                x.writeAndFlush("[客户端]" + x.remoteAddress() + "发送消息：" + s);
            }
        });

    }

//    /**
//     * 数据读取完成
//     * 回复消息
//     *
//     * @param ctx
//     * @throws Exception
//     */
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("call back"+callBack);
//        ctx.writeAndFlush(Unpooled.copiedBuffer(callBack, CharsetUtil.UTF_8));
//    }


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
