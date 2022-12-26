package top.vita.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @Author vita
 * @Date 2022/12/20 19:50
 */
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        // 输出netty的执行日志，需要修改logback的配置文件
                        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                // 阻塞，直到连接线程将连接建立完毕
                // .sync()
                .channel();
        System.out.println(channel);
        new Thread(()->{
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String s = scanner.nextLine();
                if (s.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(s);
            }
        }, "input").start();
        ChannelFuture closeFuture = channel.closeFuture();
        // 阻塞等待关闭结束
        // closeFuture.sync();
        // 使用回调方法，在关闭操作结束后执行
        closeFuture.addListener((ChannelFutureListener) channelFuture -> {
            log.debug("close...");
            // 优雅关闭
            group.shutdownGracefully();
        });
        // 注意将断点改为线程断点
        System.out.println();
    }
}
