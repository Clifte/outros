package com.box.boxjavalibv2.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import com.box.boxjavalibv2.BoxConfigBuilder;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.restclientv2.IBoxRESTClient;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.IBoxRequest;
import com.box.restclientv2.responseparsers.IBoxResponseParser;
import com.box.restclientv2.responses.DefaultBoxResponse;
import com.box.restclientv2.responses.IBoxResponse;

public class BoxFileDownloadTest {

    @Test
    public void testListenerCompleteDownload() throws IOException, BoxRestException {
        final TestInputStream stream = new TestInputStream();
        final StateKeepingFileTransferListener listener = new StateKeepingFileTransferListener();
        final BoxFileDownload testDownload = new BoxFileDownload(new BoxConfigBuilder().build(), new TestingDownloadRESTClient(stream), null);
        testDownload.setProgressListener(listener);
        new Thread() {

            public void run() {
                try {
                    Thread.sleep(testDownload.getUpdateInterval() + 20);
                } catch (InterruptedException e) {

                }
                stream.setComplete();
            }
        }.start();

        try {
            testDownload.execute(null, new OutputStream[] {new TestOutputStream()}, null, null);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(IFileTransferListener.STATUS_PASS, listener.mStatus);
        Assert.assertEquals(false, listener.mCancelled);
        Assert.assertEquals(testDownload.getBytesTransferred(), listener.mBytesTransferred);
    }

    @Test
    public void testListenerCancelDownload() throws IOException, BoxRestException {
        final TestInputStream stream = new TestInputStream();
        final StateKeepingFileTransferListener listener = new StateKeepingFileTransferListener();
        final BoxFileDownload testDownload = new BoxFileDownload(new BoxConfigBuilder().build(), new TestingDownloadRESTClient(stream), null);
        testDownload.setProgressListener(listener);
        final FutureTask<Void> task = new FutureTask<Void>(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                testDownload.execute(null, new OutputStream[] {new TestOutputStream()}, null, null);
                return null;
            }

        });
        new Thread() {

            public void run() {
                task.run();
            }
        }.start();

        new Thread() {

            public void run() {
                try {
                    Thread.sleep(testDownload.getUpdateInterval() + 20);
                } catch (InterruptedException e) {

                }
                task.cancel(true);
            }
        }.start();

        try {
            task.get(5, TimeUnit.SECONDS);
        } catch (CancellationException e2) {
            // this is expected.
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(true, listener.mCancelled);
    }

    @Test
    public void testListenerErrorDownload() throws IOException, BoxRestException {
        final TestInputStream stream = new TestInputStream();
        final StateKeepingFileTransferListener listener = new StateKeepingFileTransferListener();
        final BoxFileDownload testDownload = new BoxFileDownload(new BoxConfigBuilder().build(), new TestingDownloadRESTClient(stream), null);
        testDownload.setProgressListener(listener);
        new Thread() {

            public void run() {
                try {
                    Thread.sleep(testDownload.getUpdateInterval() + 20);
                } catch (InterruptedException e) {

                }
                stream.setError();
            }
        }.start();

        try {
            testDownload.execute(null, new OutputStream[] {new TestOutputStream()}, null, null);
        } catch (IOException e) {
            // we are expecting this to throw an IOException.
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals(IFileTransferListener.STATUS_FAIL, listener.mStatus);
        Assert.assertEquals(false, listener.mCancelled);
        Assert.assertNotNull(listener.mException);
        Assert.assertEquals(testDownload.getBytesTransferred(), listener.mBytesTransferred);
    }

    public class TestingDownloadRESTClient implements IBoxRESTClient {

        public TestingDownloadRESTClient(TestInputStream stream) {
            this(200, 200, stream);
        }

        private TestingDownloadBoxResponse mResponse;

        public TestingDownloadRESTClient(final int responseStatusCode, final int expectedStatusCode, final TestInputStream stream) {
            mResponse = new TestingDownloadBoxResponse(null, responseStatusCode, expectedStatusCode, stream);
        }

        @Override
        public IBoxResponse execute(IBoxRequest boxRequest) throws BoxRestException, AuthFatalFailureException {
            return mResponse;
        }

    }

    private class TestOutputStream extends OutputStream {

        @Override
        public void write(int arg0) throws IOException {

        }

    }

    private class TestingDownloadBoxResponse extends DefaultBoxResponse {

        private final int mResponseStatusCode;
        private final int mExpectedStatusCode;
        private final TestInputStream mTestStream;

        public TestingDownloadBoxResponse(final HttpResponse httpResponse, final int responseStatusCode, final int expectedStatusCode,
            final TestInputStream stream) {
            super(httpResponse);
            mResponseStatusCode = responseStatusCode;
            mExpectedStatusCode = expectedStatusCode;
            mTestStream = stream;
        }

        @Override
        public HttpResponse getHttpResponse() {
            return null;
        }

        @Override
        public int getResponseStatusCode() {
            return mResponseStatusCode;
        }

        @Override
        public Object parseResponse(IBoxResponseParser responseParser, IBoxResponseParser errorParser) throws BoxRestException {
            return mTestStream;
        }

        @Override
        public int getExpectedResponseCode() {
            return mExpectedStatusCode;
        }

    }

    private class StateKeepingFileTransferListener implements IFileTransferListener {

        protected String mStatus;
        protected boolean mCancelled = false;
        protected long mBytesTransferred;
        protected IOException mException;

        @Override
        public void onComplete(String status) {
            mStatus = status;
        }

        @Override
        public void onCanceled() {
            mCancelled = true;
        }

        @Override
        public void onProgress(long bytesTransferred) {
            mBytesTransferred = bytesTransferred;

        }

        @Override
        public void onIOException(IOException e) {
            mException = e;
        }

    }

    private class TestInputStream extends InputStream {

        AtomicInteger mReadResponse = new AtomicInteger(0);
        AtomicBoolean mThrowException = new AtomicBoolean(false);

        @Override
        public int read() throws IOException {
            if (mThrowException.get()) {
                throw new IOException();
            }
            return mReadResponse.get();
        }

        public void setError() {
            mThrowException.set(true);
        }

        public void setComplete() {
            mReadResponse.set(-1);
        }

    }
}
