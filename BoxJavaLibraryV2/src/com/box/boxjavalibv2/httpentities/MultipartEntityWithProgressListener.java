package com.box.boxjavalibv2.httpentities;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.CharEncoding;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;

import com.box.boxjavalibv2.exceptions.BoxJSONException;
import com.box.boxjavalibv2.filetransfer.IFileTransferListener;
import com.box.boxjavalibv2.jsonentities.IBoxJSONStringEntity;
import com.box.boxjavalibv2.jsonparsing.IBoxJSONParser;

/**
 * This is a class wrappying MultipartEntity with a IFIleTransferListener so the writing progress of the entity can be monitored.
 */
public class MultipartEntityWithProgressListener extends MultipartEntity {

    /** Default minimum time between two progress messages being sent out. */
    public static final int ON_PROGRESS_UPDATE_THRESHOLD = 300;
    /**
     * The minimum time in milliseconds that must pass between each firing of progress listener. This is to avoid excessive calls which may lock up the device.
     */
    private static int onProgressUpdateThreshold = ON_PROGRESS_UPDATE_THRESHOLD;
    /**
     * progress listener.
     */
    private IFileTransferListener mListener;

    /**
     * Instance of CountingOutputStream.
     */
    private CountingOutputStream mCountingOutputStream;

    /** Either contentBody or boxJSONStringEntity must be non-null. The other must be null. */
    private static class ContentBodyOrStringEntity {
        ContentBodyOrStringEntity(ContentBody contentBody) {
            assert(contentBody != null);
            this.contentBody = contentBody;
            this.boxJSONStringEntity = null;
        }

        ContentBodyOrStringEntity(IBoxJSONStringEntity boxJSONStringEntity) {
            assert(boxJSONStringEntity != null);
            this.contentBody = null;
            this.boxJSONStringEntity = boxJSONStringEntity;
        }

        public final ContentBody contentBody;
        public final IBoxJSONStringEntity boxJSONStringEntity;
    }

    // We want to use a LinkedHashMap here so that the parts are appended to the HTTP request
    // in the same order that they were appeneded to this MultipartEntityWithProgressListener.
    private final LinkedHashMap<String, ContentBodyOrStringEntity> parts = new LinkedHashMap<String, ContentBodyOrStringEntity>();

    public MultipartEntityWithProgressListener(final HttpMultipartMode mode) {
        super(mode, null, Charset.forName(CharEncoding.UTF_8));
    }

    public void addContentBodyPart(String name, ContentBody contentBody) {
        parts.put(name, new ContentBodyOrStringEntity(contentBody));
    }

    public ContentBody getContentBodyPart(String name) {
        ContentBodyOrStringEntity c = parts.get(name);
        return (c != null) ? c.contentBody : null;
    }

    public void addBoxJSONStringEntityPart(String name, IBoxJSONStringEntity entity) {
        parts.put(name, new ContentBodyOrStringEntity(entity));
    }

    public IBoxJSONStringEntity getJSONStringEntityPart(String name) {
        ContentBodyOrStringEntity c = parts.get(name);
        return (c != null) ? c.boxJSONStringEntity : null;
    }

    /**
     * Method to put all parts in to the multipart entity.
     *
     * @throws BoxJSONException
     * @throws UnsupportedEncodingException
     */
    public void prepareParts(IBoxJSONParser parser) throws UnsupportedEncodingException, BoxJSONException {
        for (Map.Entry<String, ContentBodyOrStringEntity> entry : parts.entrySet()) {
            ContentBodyOrStringEntity value = entry.getValue();
            ContentBody contentBody;
            if (value.contentBody != null) {
                contentBody = value.contentBody;
            } else {
                contentBody = new StringBody(value.boxJSONStringEntity.toJSONString(parser), Charset.forName(CharEncoding.UTF_8));
            }
            super.addPart(entry.getKey(), contentBody);
        }
    }

    /**
     * Set upload listener.
     *
     * @param listener
     *            upload listener
     */
    public void setListener(IFileTransferListener listener) {
        this.mListener = listener;
    }

    /**
     * Set the threshold time for progress updating. This is the minimum time between two progress messages being sent out. If this method is not called, the
     * default value is {@link #ON_PROGRESS_UPDATE_THRESHOLD}
     *
     * @param threshold
     *            threshold
     */
    public static void setOnProgressUpdateThreshold(final int threshold) {
        onProgressUpdateThreshold = threshold;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        if (mCountingOutputStream == null) {
            mCountingOutputStream = new CountingOutputStream(outstream, mListener);
        }
        super.writeTo(mCountingOutputStream);
        if (mListener != null) {
            mListener.onProgress(mCountingOutputStream.getBytesTransferred());
        }
    }

    /**
     * FilterOutputStream that fires progress callbacks so we can monitor upload progress.
     */
    private static class CountingOutputStream extends FilterOutputStream {

        /**
         * progress listener.
         */
        private final IFileTransferListener mProgresslistener;
        /**
         * number of bytes transferred so far.
         */
        private long bytesBransferred;
        /**
         * last timestamp of progress listener being fired.
         */
        private long lastOnProgressPost = 0;

        /**
         * constructor that also takes a progress listener.
         *
         * @param out
         *            output stream
         * @param progressListener
         *            progress listener
         */
        public CountingOutputStream(final OutputStream out, final IFileTransferListener progressListener) {
            super(out);
            mProgresslistener = progressListener;
            bytesBransferred = 0;
        }

        @Override
        public void write(final byte[] buffer, final int offset, final int length) throws IOException {
            try {
                out.write(buffer, offset, length);
            } catch (IOException e) {
                if (mProgresslistener != null) {
                    mProgresslistener.onIOException(e);
                }
            }
            bytesBransferred += length;
            long currTime = System.currentTimeMillis();
            if (mProgresslistener != null && currTime - lastOnProgressPost > onProgressUpdateThreshold) {
                lastOnProgressPost = currTime;
                mProgresslistener.onProgress(bytesBransferred);
            }
            // Allow canceling of downloads through thread interrupt.
            if (Thread.currentThread().isInterrupted() && mProgresslistener != null) {
                mProgresslistener.onCanceled();
                throw new InterruptedMultipartException();
            }
        }

        /**
         * Get the number of bytes transferred so far.
         *
         * @return Number of bytes transferred so far.
         */
        public long getBytesTransferred() {
            return bytesBransferred;
        }
    }

    public static class InterruptedMultipartException extends IOException {

        private static final long serialVersionUID = 1L;
    }
}
