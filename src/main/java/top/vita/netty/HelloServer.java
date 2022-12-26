package top.vita.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Author vita
 * @Date 2022/12/20 10:42
 */
public class HelloServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                // 指定worker(child)的任务
                .childHandler(new ChannelInitializer<NioSocketChannel>(){
                    // 设置初始化方法，在连接建立后才会调用
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 将客户端发送的ByteBuf转换为字符串
                        // pipeline同scrapy中的pipeline管道
                        // 数据会按照pipeline顺序依次执行handler来处理数据
                        nsc.pipeline().addLast(new StringDecoder());
                        // Inbound 入站 写入
                        // Outbound 出站 写出
                        nsc.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            // ByteBuf经过解析后传入参数中
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 使用上面转换出的信息
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
