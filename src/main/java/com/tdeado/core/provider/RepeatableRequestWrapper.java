package com.tdeado.core.provider;

import lombok.SneakyThrows;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RepeatableRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;
    private Charset charset;
    @SneakyThrows
    public RepeatableRequestWrapper(HttpServletRequest request, Charset charset) {
        super(request);
        body = request.getInputStream().readAllBytes();
        this.charset = charset;
    }

    public RepeatableRequestWrapper(HttpServletRequest request) {
        this(request, StandardCharsets.UTF_8);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new RepeatableInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(),charset));
    }

    private class RepeatableInputStream extends ServletInputStream{
        private ByteArrayInputStream byteArrayInputStream;

        @Override
        public synchronized void reset() throws IOException {
            byteArrayInputStream.reset();
        }

        @Override
        public synchronized void mark(int readlimit) {
            byteArrayInputStream.mark(readlimit);
        }

        public RepeatableInputStream() {
            byteArrayInputStream = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("不支持监听");
        }

        @Override
        public int read() throws IOException {
            return byteArrayInputStream.read();
        }

        @Override
        public boolean markSupported() {
            return byteArrayInputStream.markSupported();
        }
    }

}
