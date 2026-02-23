package com.traneptora.jxlatte.awt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
    private final ByteBuffer buffer;

    public ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (!buffer.hasRemaining()) {
            return -1; // EOF
        }
        return buffer.get() & 0xFF; // Return byte as unsigned
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (!buffer.hasRemaining()) {
            return -1; // EOF
        }
        int bytesRead = Math.min(len, buffer.remaining());
        buffer.get(b, off, bytesRead);
        return bytesRead;
    }
}