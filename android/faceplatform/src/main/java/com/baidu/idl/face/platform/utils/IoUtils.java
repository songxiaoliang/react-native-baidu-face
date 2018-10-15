package com.baidu.idl.face.platform.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.util.zip.ZipFile;

public class IoUtils {

    private static final int EOF = -1;

    private static final int BUFFER_SIZE = 1024;

    public interface ProgressListener {

        public void progress(long current, long total);

    }

    /**
     * Copy the content of the input stream into the output stream, using a temporary byte array buffer whose size is
     * defined by {@link #BUFFER_SIZE}.
     *
     * @param in  The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws IOException If any requestError occurs during the copy.
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = in.read(buffer)) != EOF) {
            out.write(buffer, 0, len);
        }
    }

    /**
     * 将InputStream转存到文件中
     *
     * @param in
     * @param outFile
     * @throws IOException
     */
    public static void copyStream(InputStream in, File outFile) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = FileUtils.openNewFileOutput(outFile);
            copyStream(in, fos);
        } finally {
            closeQuietly(fos);
        }
    }

    /**
     * 将InputStream转存到文件中
     *
     * @param in
     * @param outFile
     * @param total
     * @param l
     * @throws IOException
     */
    public static void copyStream(InputStream in, File outFile, long total, ProgressListener l) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = FileUtils.openNewFileOutput(outFile);
            copyStream(in, fos, total, l);
        } finally {
            closeQuietly(fos);
        }
    }

    public static void copyStream(InputStream in, OutputStream out, long total, ProgressListener l) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long current = 0;
        int len = 0;
        while ((len = in.read(buffer)) != EOF) {
            out.write(buffer, 0, len);
            current += len;
            if (l != null) {
                l.progress(current, total);
            }
        }
    }

    /**
     * Closes the specified closeable.
     *
     * @param closeable The closeable to close.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream the stream that contains data.
     * @return the result string.
     * @throws IOException an I/O requestError occurred.
     */
    public static String loadContent(InputStream stream) throws IOException {
        return loadContent(stream, null);
    }

    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream      the stream that contains data.
     * @param charsetName the encoding of the data.
     * @return the result string.
     * @throws IOException an I/O requestError occurred.
     */
    public static String loadContent(InputStream stream, String charsetName) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream may not be null.");
        }
        String encoding = charsetName;
        if (TextUtils.isEmpty(encoding)) {
            encoding = System.getProperty("file.encoding", "utf-8");
        }
        final InputStreamReader reader = new InputStreamReader(stream, encoding);
        final StringWriter writer = new StringWriter();
        final char[] buffer = new char[4 * BUFFER_SIZE];
        int len = reader.read(buffer);
        while (len > 0) {
            writer.write(buffer, 0, len);
            len = reader.read(buffer);
        }
        return writer.toString();
    }

    /**
     * 将Stream转为byte数组
     *
     * @param in the stream that contains data.
     * @return
     */
    public static byte[] loadBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            copyStream(in, out);
            data = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(out);
        }
        return data;
    }

}
