package top.vita.nio;

import lombok.extern.slf4j.Slf4j;
import top.vita.nio.c2.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


/**
 * @Author vita
 * @Date 2022/12/18 18:28
 */
@Slf4j
public class ServerSocketChannelTest {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ByteBuffer buffer = ByteBuffer.allocate(16);
        while (true){
            SocketChannel sc = ssc.accept();
            log.debug("before read... {}", sc);
            sc.read(buffer);
            buffer.flip();
            ByteBufferUtil.debugRead(buffer);
            buffer.clear();
            log.debug("after read...{}", sc);
        }
    }
}
