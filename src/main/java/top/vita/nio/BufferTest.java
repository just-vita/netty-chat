package top.vita.nio;


import top.vita.nio.c2.ByteBufferUtil;

import java.nio.ByteBuffer;


/**
 * @Author vita
 * @Date 2022/12/13 8:59
 */
public class BufferTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        ByteBufferUtil.debugAll(buffer);
        buffer.put((byte) 97);
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);
        buffer.clear();
        ByteBufferUtil.debugAll(buffer);
        buffer.put((byte) 98);
        ByteBufferUtil.debugAll(buffer);
    }
}
