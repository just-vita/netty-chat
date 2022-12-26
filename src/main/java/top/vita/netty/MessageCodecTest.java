package top.vita.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import top.vita.chat.message.LoginRequestMessage;
import top.vita.chat.protocol.MessageCodec;

/**
 * @Author vita
 * @Date 2022/12/22 17:34
 */
public class MessageCodecTest {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodec());
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        // 出站 编码
        // channel.writeOutbound(message);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        // 手动编码
        new MessageCodec().encode(null, message, buffer);
        // 入站 解码
        // channel.writeInbound(buffer);
        ByteBuf b1 = buffer.slice(0, 100);
        ByteBuf b2 = buffer.slice(100, buffer.readableBytes() - 100);
        // 计数器加一防止buffer被清空
        b1.retain();
        // 方法中会调用release()方法减少计数，需要使用retain()方法保证buffer不被清空
        channel.writeInbound(b1);
        // 半包会自动进行拼接
        channel.writeInbound(b2);
    }
}
