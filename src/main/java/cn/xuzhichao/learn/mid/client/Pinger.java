package cn.xuzhichao.learn.mid.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Client端连接Server端后，会随机间隔向Server端发送一个心跳包
 * @author xuzhichao
 * @date 2019/7/8 09:23
 * @Description:
 */
public class Pinger extends ChannelInboundHandlerAdapter {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        ping(channel);
    }

    private Random random = new Random();

    private Channel channel;

    private void ping(Channel channel) {
        int pingGap = Math.max(3, random.nextInt(10));
        logger.info("将在 " + pingGap + " 秒后发送心跳包");
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (channel.isActive()) {
                    channel.writeAndFlush(ClientIdleStateTrigger.HEART_BEAT_PAG);
                } else {
                    logger.error("通道关闭，取消发送心跳包");
                    channel.closeFuture();
                    throw new RuntimeException();
                }

            }
        }, pingGap, TimeUnit.SECONDS);

        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()){
                    ping(channel);
                }
            }
        });

    }

    /**
     * 捕获到异常之后，不继续发送心跳包
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
