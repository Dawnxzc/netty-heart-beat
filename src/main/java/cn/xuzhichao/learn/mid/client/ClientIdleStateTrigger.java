package cn.xuzhichao.learn.mid.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 客户端用于捕获 IdleState的WRITER_IDLE时间，然后箱Server端发送心跳包
 * @author xuzhichao
 * @date 2019/7/8 09:17
 * @Description:
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    public static final String HEART_BEAT_PAG = "HEART BEAT";

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if (IdleState.WRITER_IDLE == state){
                ctx.writeAndFlush(HEART_BEAT_PAG);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
