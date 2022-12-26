package top.vita.chat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.vita.chat.message.GroupCreateRequestMessage;
import top.vita.chat.message.GroupCreateResponseMessage;
import top.vita.chat.server.session.Group;
import top.vita.chat.server.session.GroupSession;
import top.vita.chat.server.session.GroupSessionFactory;

import java.util.List;
import java.util.Set;

/**
 * @Author vita
 * @Date 2022/12/26 12:55
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null){
            // 发生成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + " 创建成功"));
            // 发送拉群消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入 " + groupName));
            }
        }else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + " 已经存在"));
        }
    }
}
