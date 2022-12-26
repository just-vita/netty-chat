package top.vita.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @Author vita
 * @Date 2022/12/20 10:57
 */
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 设置初始化方法，在连接建立后才会调用
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 对应服务端
                        // 字符串进过编码器变成ByteBuf
                        nsc.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                // 阻塞 直到连接建立成功
                .sync()
                // 获取连接对象
                .channel()
                // 通过连接对象写入字符串
                .writeAndFlush("hello world");
    }
}
