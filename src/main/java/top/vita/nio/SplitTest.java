package top.vita.nio;


import top.vita.nio.c2.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * @Author vita
 * @Date 2022/12/18 13:17
 */
public class SplitTest {
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        //                     11            24
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);

        source.put("w are you?\nhaha!\n".getBytes());
        split(source);
    }

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
}
