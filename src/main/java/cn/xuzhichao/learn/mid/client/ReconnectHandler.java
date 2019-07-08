package cn.xuzhichao.learn.mid.client;

import cn.xuzhichao.learn.mid.client.policy.RetryPolicy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 重连处理器
 * @author xuzhichao
 * @date 2019/7/8 10:58
 * @Description:
 */
@ChannelHandler.Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int retries = 0;
    private RetryPolicy retryPolicy;

    private TcpClient tcpClient;

    public ReconnectHandler(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isInfoEnabled()){
            logger.info("成功与服务端生成一个连接");
        }
        retries = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (retries == 0){
            logger.info("失去与服务端的TCP连接");
            ctx.close();
        }
        long sleepTimeMs = getRetryPolicy().getSleepTimeMs(retries);
        logger.info(String.format("%d 毫秒后尝试与服务端重连， 重试次数：%d", sleepTimeMs, ++retries));

        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("正在重连。。。");
                tcpClient.connect();
            }
        }, sleepTimeMs, TimeUnit.MILLISECONDS);

        ctx.fireChannelActive();
    }

    private RetryPolicy getRetryPolicy() {
        if (this.retryPolicy == null) {
            this.retryPolicy = tcpClient.getRetryPolicy();
        }
        return this.retryPolicy;
    }
}
