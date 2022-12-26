package top.vita.nio;

import lombok.extern.slf4j.Slf4j;
import top.vita.nio.c2.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;


/**
 * @Author vita
 * @Date 2022/12/19 17:25
 */
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT);
        // 创建固定数量的负责读事件的worker
        Worker worker01 = new Worker("worker01");
        while (true) {
            // boss线程只监听accept事件
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel c = (ServerSocketChannel) key.channel();
                    SocketChannel sc = c.accept();
                    sc.configureBlocking(false);
                    System.out.println("worker01...before...");
                    worker01.register(sc);
                    System.out.println("worker01...after...");
                }
                iter.remove();
            }
        }
    }

    static class Worker implements Runnable{

        private String name;
        private Selector selector;
        private volatile boolean start = false;
        private SocketChannel sc;
        private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!start){
                selector = Selector.open();
                new Thread(this, name).start();
                start = true;
            }
            this.sc = sc;
            selector.wakeup();
        }

        @Override
        public void run() {
            try {
                selector.select();
                SelectionKey register = sc.register(selector, 0);
                register.interestOps(SelectionKey.OP_READ);
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        int read = sc.read(buffer);
                        System.out.println(read);
                        ByteBufferUtil.debugAll(buffer);
                    }
                    iter.remove();
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
    }

}
