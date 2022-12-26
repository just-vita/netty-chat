package top.vita.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author vita
 * @Date 2022/12/22 15:02
 */
public class LengthFieldBaseTest {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(
                        1024, // 最大字段长度
                        0,    // 长度字段的起始位置
                        4,   // 长度字段的长度 一个int占4个字节
                        1,   // 长度字段和内容字段的距离 如跳过版本号字段 若长度不符合长度字段中的值则会报错
                        5),  // 跳过的长度 如读取时跳过长度和版本号字段
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        getBuffer(buffer, "Hello, World");
        getBuffer(buffer, "Hi");
        channel.writeInbound(buffer);
    }

    private static void getBuffer(ByteBuf buffer, String text) {
        byte[] bytes = text.getBytes();
        int length = bytes.length;
        // 长度字段 4字节
        buffer.writeInt(length);
        // 版本号字段 1字节
        buffer.writeByte(1);
        // 内容字段 length字节
        buffer.writeBytes(bytes);
    }
}
