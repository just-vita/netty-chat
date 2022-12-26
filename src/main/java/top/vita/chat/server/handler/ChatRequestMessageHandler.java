package top.vita.chat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.vita.chat.message.ChatRequestMessage;
import top.vita.chat.message.ChatResponseMessage;
import top.vita.chat.server.session.SessionFactory;

/**
 * @Author vita
 * @Date 2022/12/26 12:18
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        // 获取接收方的信息通道
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel != null && channel.isActive()){
            // 构建信息对象，存入发送方信息和内容
            ChatResponseMessage message = new ChatResponseMessage(msg.getFrom(), msg.getContent());
            // 给对方发送
            channel.writeAndFlush(message);
        }else{
            // 给发送方返回
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者不在线"));
        }
    }

}
