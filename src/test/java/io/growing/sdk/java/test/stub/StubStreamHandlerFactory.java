package io.growing.sdk.java.test.stub;

import java.io.*;
import java.net.*;

public class StubStreamHandlerFactory implements URLStreamHandlerFactory {
    private StubHttpURLConnectionListener mStubHttpURLConnectionListener;

    private StubStreamHandlerFactory() {
        super();
    }

    public static StubStreamHandlerFactory get() {
        return StubStreamHandlerFactory.SingleInstance.INSTANCE;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return new StubHttpURLStreamHandler();
    }

    public void setStubHttpURLConnectionListener(StubHttpURLConnectionListener httpURLConnectionListener) {
        this.mStubHttpURLConnectionListener = httpURLConnectionListener;
    }

    public interface StubHttpURLConnectionListener {
        void onSend(URL url, byte[] msg);
    }

    private static class SingleInstance {
        private static final StubStreamHandlerFactory INSTANCE = new StubStreamHandlerFactory();
    }

    private class StubHttpURLStreamHandler extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL url) {
            return new StubHttpURLConnection(url);
        }
    }

    private class StubHttpURLConnection extends HttpURLConnection {

        public StubHttpURLConnection(URL url) {
            super(url);
        }

        @Override
        public void connect() {
            // do nothing
        }

        @Override
        public void disconnect() {
            // do nothing
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    if (mStubHttpURLConnectionListener != null) {
                        mStubHttpURLConnectionListener.onSend(StubHttpURLConnection.this.getURL(), this.toByteArray());
                    }
                    super.close();
                }
            };
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new String("OK").getBytes());
        }

        @Override
        public int getResponseCode() throws IOException {
            return 204;
        }
    }
}
