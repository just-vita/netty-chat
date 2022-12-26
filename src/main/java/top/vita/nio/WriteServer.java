package top.vita.nio;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @Author vita
 * @Date 2022/12/19 16:30
 */
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 3000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int count = sc.write(buffer);
                    System.out.println("write : " + count);
                    if (buffer.hasRemaining()) {
                        // 可读可写 同linux权限一样可以进行相加相减
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // 加入附件
                        scKey.attach(buffer);
                    }
                }else if (key.isWritable()){
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    int count = sc.write(buffer);
                    System.out.println("write : " + count);
                    // 已写完
                    if (!buffer.hasRemaining()) {
                        // 不再负责可写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        // 清除附件
                        key.attach(null);
                    }
                }
                iter.remove();
            }
        }
    }
}
