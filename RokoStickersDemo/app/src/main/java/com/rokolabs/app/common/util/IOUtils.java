package com.rokolabs.app.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    public static byte[] toByteArray(InputStream inputStream) throws IOException
    {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        streamCopy(inputStream, byteBuffer);

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
    
    public static void streamCopy(InputStream is, OutputStream os) throws IOException
    {
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        int len;
        while ((len = is.read(buf)) != -1)
        {
            os.write(buf, 0, len);
        }
        os.close();
        is.close();
    }
//    public static void streamCopy(InputStream is, OutputStream os) throws IOException
//    {
//        // get an channel from the stream
//        final ReadableByteChannel inputChannel = Channels.newChannel(is);
//        final WritableByteChannel outputChannel = Channels.newChannel(os);
//        // copy the channels
//        fastChannelCopy(inputChannel, outputChannel);
//        // closing the channels
//        inputChannel.close();
//        outputChannel.close();
//    }
//
//    public static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException
//    {
//        final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
//        while (src.read(buffer) != -1)
//        {
//            // prepare the buffer to be drained
//            buffer.flip();
//            // write to the channel, may block
//            dest.write(buffer);
//            // If partial transfer, shift remainder down
//            // If buffer is empty, same as doing clear()
//            buffer.compact();
//        }
//        // EOF will leave buffer in fill state
//        buffer.flip();
//        // make sure the buffer is fully drained.
//        while (buffer.hasRemaining())
//        {
//            dest.write(buffer);
//        }
//    }
}
