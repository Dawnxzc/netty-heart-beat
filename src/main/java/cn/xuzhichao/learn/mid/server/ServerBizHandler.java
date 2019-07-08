package cn.xuzhichao.learn.mid.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端收到数据包处理
 * @author xuzhichao
 * @date 2019/7/8 11:36
 * @Description:
 */
@ChannelHandler.Sharable
public class ServerBizHandler extends SimpleChannelInboundHandler<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String RES_MSG = "SERVER received response";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("received data: " + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("生成与客户端的连接");

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("与客户端断开连接");

        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
