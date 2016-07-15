package com.xycode.xylibrary.okHttp;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by 18953 on 2016/7/12.
 */
public class OkFileHelper {

    private File tempFile;

    //customer RequestBody，can show progress
    public static class ProgressRequestBody extends RequestBody {
        // body
        private final RequestBody requestBody;
        //the progress callback interface
        private final FileProgressListener fileProgressListener;
        // upload BufferedSink
        private BufferedSink bufferedSink;

        /**
         *
         * @param requestBody
         * @param fileProgressListener
         */
        public ProgressRequestBody(RequestBody requestBody, FileProgressListener fileProgressListener) {
            this.requestBody = requestBody;
            this.fileProgressListener = fileProgressListener;
        }

        /**
         * contentType
         *
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        /**
         * contentLength
         *
         * @return contentLength
         * @throws IOException
         */
        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        /**
         * write
         *
         * @param sink BufferedSink
         * @throws IOException
         */
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                // package
                bufferedSink = Okio.buffer(sink(sink));
            }

            requestBody.writeTo(bufferedSink);

            bufferedSink.flush();
        }

        /**
         *
         *
         * @param sink Sink
         * @return Sink
         */
        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                // now bytes count
                long bytesWritten = 0L;
                //total length,contentLength()
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        // do it only once
                        contentLength = contentLength();
                    }
                    bytesWritten += byteCount;
                    // callback
                    if (fileProgressListener != null) {
                        fileProgressListener.update(bytesWritten, contentLength, bytesWritten == contentLength);
                    }
                }
            };
        }
    }

    // progress listener callback
    public interface FileProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}
