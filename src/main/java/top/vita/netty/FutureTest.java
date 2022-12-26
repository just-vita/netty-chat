package top.vita.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @Author vita
 * @Date 2022/12/21 11:12
 */
@Slf4j
public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(()->{
            try {
                log.debug("计算结果");
                Thread.sleep(1000);
                promise.setSuccess(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
//        Integer value = promise.get();
        promise.addListener(future -> {
            Object value = future.getNow();
            log.debug("{}", value);
        });
    }
}
