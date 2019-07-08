package cn.xuzhichao.learn.mid.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.junit.Assert;

/**
 * 客户端处理器初始化类
 * @author xuzhichao
 * @date 2019/7/8 10:01
 * @Description:
 */
public class ClientHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private ReconnectHandler reconnectHandler;

    public ClientHandlerInitializer(TcpClient tcpClient) {
        Assert.assertNotNull("TcpClient 不能为空", tcpClient);
        this.reconnectHandler = new ReconnectHandler(tcpClient);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 除了Pinger，其他的绑定仅作为示例，实际应用内还绑定其他的handler
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(this.reconnectHandler);
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast(new Pinger());
    }
}
