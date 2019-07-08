package cn.xuzhichao.learn.mid.client;

import cn.xuzhichao.learn.mid.client.policy.ExponentialBackOffRetry;
import cn.xuzhichao.learn.mid.client.policy.RetryPolicy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 客户端
 * @author xuzhichao
 * @date 2019/7/8 10:24
 * @Description:
 */
public class TcpClient {

    private String host;
    private int port;
    private Bootstrap bootstrap;

    /**
     * 重连策略
     */
    private RetryPolicy retryPolicy;
    /**
     * 保存channel，用于在其他非Handler的地方发送数据
     */
    private Channel channel;

    public TcpClient(String host, int port){
        this(host, port, new ExponentialBackOffRetry(1000, Integer.MAX_VALUE, 60 * 1000));
    }

    public TcpClient(String host, int port, RetryPolicy retryPolicy) {
        this.host = host;
        this.port = port;
        this.retryPolicy = retryPolicy;
        init();
    }

    public void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).
                channel(NioSocketChannel.class).
                handler(new ClientHandlerInitializer(TcpClient.this));
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * 向TCP服务器请求连接
     */
    public void connect() {
        synchronized (bootstrap){
            ChannelFuture future = bootstrap.connect(host, port);
            future.addListener(getConnectionListener());
            this.channel = future.channel();
        }
    }

    private GenericFutureListener<? extends Future<? super Void>> getConnectionListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().pipeline().fireChannelActive();
                }
            }
        };
    }

    public static void main(String[] args) {
        TcpClient tcpClient = new TcpClient("localhost", 7654);
        tcpClient.connect();
    }

}
