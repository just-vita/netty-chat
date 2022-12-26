package top.vita.nio;


import top.vita.nio.c2.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


/**
 * @Author vita
 * @Date 2022/12/18 20:13
 */
public class SelectorTest {

    private static void split(ByteBuffer source) {
        source.flip();
        // 得到读上限
        int limit = source.limit();
        for (int i = 0; i < limit; i++) {
            // get(i) 不会移动读指针
            if (source.get(i) == '\n') {
                // 由读指针的位置计算得到长度
                int length = i - source.position() + 1;

                System.out.println("i:" + i);
                System.out.println("source.position():" + source.position());
                ByteBuffer buffer = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    // get() 会移动读指针
                    buffer.put(source.get());
                }
                ByteBufferUtil.debugAll(buffer);
            }
        }
        source.compact();
    }

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 设置不堵塞
        ssc.configureBlocking(false);
        // 使用构造器设置为只处理accept
        SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT, null);
        // 使用方法设置为只处理accept
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        // ByteBuffer buffer = ByteBuffer.allocate(16);
        while (true){
            // 如果没有事件就阻塞，有事件线程就恢复运行
            // 有未处理事件时 不会阻塞
            selector.select();
            // 获取所有已经注册的事件key
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                // 可以取消不处理
                // 如果不处理且没有取消则selector不会进行阻塞
                // key.cancel();
                System.out.println(key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    /**
                     * 报错null空指针的原因：
                     * selector 共有两个集合：selector和 selectedKeys
                     * 注册后都会加入selector集合中
                     * 发生事件后会加入selectedKeys中
                     * 事件处理后会标记为已处理 但不会从selectedKeys集合中删除
                     * 所以下一次从selectedKeys集合中获取元素时，已处理的事件并没有删除掉
                     * 此时再对已处理的事件进行处理，就会报空指针异常
                     */
                    // 进行accept操作
                    SocketChannel sc = channel.accept();
                    // 设置为非堵塞
                    sc.configureBlocking(false);
                    System.out.println(sc);
                    // 创建buffer
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 将sc也注册到selector中 将buffer存入attachment
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    // 设置为只处理read事件
                    scKey.interestOps(SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    // 处理read事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read == -1){
                            // 客户端正常关闭时，会向服务器发送一个读事件 返回-1 需要手动取消事件
                            key.cancel();
                        }else{
                            split(buffer);
                            // 没有读完，需要扩容
                            if (buffer.position() == buffer.limit()){
                                // 扩容两倍
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                // 加入附件中
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 客户端异常关闭，要手动取消事件 将channel从selector集合中删除
                        key.cancel();
                    }
                }
                // 使用完的要删除，否则会报空指针异常
                iter.remove();
            }
        }
    }
}
