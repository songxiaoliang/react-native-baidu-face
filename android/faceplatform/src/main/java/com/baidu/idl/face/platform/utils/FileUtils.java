package com.baidu.idl.face.platform.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class FileUtils {

    public static final int S_IRWXU = 00700; // rwx u
    public static final int S_IRUSR = 00400; // r-- u
    public static final int S_IWUSR = 00200; // -w- u
    public static final int S_IXUSR = 00100; // --x u

    public static final int S_IRWXG = 00070; // rwx g
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007; // rwx o
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    /**
     * Regular expression for safe filenames: no spaces or metacharacters
     */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    private static final Pattern RESERVED_CHARS_PATTERN = Pattern.compile("[\\\\/:\\*\\?\\\"<>|]");

    private FileUtils() {
    }

    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     *
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    /**
     * 判断文件名是否有效，检测是否包含非法字符,文件名不能包含 \/:*?"<>|
     *
     * @param name
     * @return
     */
    public static boolean isFilenameValid(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        return !RESERVED_CHARS_PATTERN.matcher(name).find();
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dest
     */
    public static void copyFile(String src, String dest) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            IoUtils.copyStream(fis, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }
    }

    /**
     * 复制文件,使用nio以提高性能
     *
     * @param src  - 源文件
     * @param dest - 目标文件
     */
    public static void copyFile(File src, File dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);
            in = fis.getChannel(); // 得到对应的文件通道
            out = fos.getChannel(); // 得到对应的文件通道
            in.transferTo(0, in.size(), out); // 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
            IoUtils.closeQuietly(in);
            IoUtils.closeQuietly(fos);
            IoUtils.closeQuietly(out);
        }
    }

    /**
     * 复制文件夹
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyDirectory(File src, File dest) throws IOException {
        if (src.exists()) {
            dest.mkdirs();
            File[] files = src.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    copyDirectory(file, new File(dest, file.getName()));
                } else {
                    copyFile(file, new File(dest, file.getName()));
                }
            }
        }
    }

    /**
     * Ensure directory exists
     *
     * @param file
     */
    public static void ensureDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
    }

    /**
     * Ensure make directory, 如果存在同名文件夹，则添加上数字后缀
     *
     * @param dir
     */
    public static boolean ensureMkdir(final File dir) {
        if (dir == null) {
            return false;
        }
        File tempDir = dir;
        int i = 1;
        while (tempDir.exists()) {
            tempDir = new File(dir.getParent(), dir.getName() + "(" + i + ")");
            i++;
        }
        return tempDir.mkdir();
    }

    /**
     * Ensure parent
     *
     * @param file
     */
    public static void ensureParent(final File file) {
        if (null != file) {
            final File parentFile = file.getParentFile();
            if (null != parentFile && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    /**
     * Clean a specified directory.
     *
     * @param dir the directory to clean.
     */
    public static void cleanDir(final File dir) {
        deleteDir(dir, false);
    }

    /**
     * Clean a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void cleanDir(final File dir, final FilenameFilter filter) {
        deleteDir(dir, false, filter);
    }

    /**
     * Clean a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void cleanDir(final File dir, final FileFilter filter) {
        deleteDir(dir, false, filter);
    }

    public static void deleteDir(final String dir) {
        deleteDir(new File(dir));
    }

    /**
     * Delete a specified directory.
     *
     * @param dir the directory to clean.
     */
    public static void deleteDir(final File dir) {
        deleteDir(dir, true);
    }

    /**
     * Delete a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final FileFilter filter) {
        deleteDir(dir, true, filter);
    }

    /**
     * Delete a specified directory.
     *
     * @param dir    the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final FilenameFilter filter) {
        deleteDir(dir, true, filter);
    }

    /**
     * Delete a specified directory.
     *
     * @param dir       the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     */
    public static void deleteDir(final File dir, final boolean removeDir) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Delete a specified directory.
     *
     * @param dir       the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     * @param filter    the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final boolean removeDir, final FileFilter filter) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(filter);
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Delete a specified directory.
     *
     * @param dir       the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     * @param filter    the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final boolean removeDir, final FilenameFilter filter) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(filter);
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * compute the size of one folder
     *
     * @param dir
     * @return the byte length for the folder
     */
    public static long computeFolderSize(final File dir) {
        if (dir == null) {
            return 0;
        }
        long dirSize = 0;
        final File[] files = dir.listFiles();
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                final File file = files[i];
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += computeFolderSize(file);
                }
            }
        }
        return dirSize;
    }

    /**
     * Retrieve the main file name.
     *
     * @param path the file name.
     * @return the main file name without the extension.
     */
    public static String getFileNameWithoutExtensionByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getFileNameWithoutExtension(new File(path));
    }

    /**
     * Helper method to get a filename without its extension
     *
     * @param fileName String
     * @return String
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            name = fileName.substring(0, index);
        }
        return name;
    }

    /**
     * Retrieve the main file name.
     *
     * @param file the file.
     * @return the main file name without the extension.
     */
    public static String getFileNameWithoutExtension(final File file) {
        if (null == file) {
            return null;
        }
        String fileName = file.getName();
        final int index = fileName.lastIndexOf('.');
        if (index >= 0) {
            fileName = fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     * Retrieve the main file name.
     *
     * @param path the file name.
     * @return the extension of the file.
     */
    public static String getExtension(final String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getExtension(new File(path));
    }

    /**
     * Retrieve the extension of the file.
     *
     * @param file the file.
     * @return the extension of the file.
     */
    public static String getExtension(final File file) {
        if (null == file) {
            return null;
        }
        final String fileName = file.getName();
        final int index = fileName.lastIndexOf('.');
        String extension = "";
        if (index >= 0) {
            extension = fileName.substring(index + 1);
        }
        return extension;
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean existsFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return existsFile(new File(path));
    }

    /**
     * 判断文件是否存在
     *
     * @param file
     * @return
     */
    public static boolean existsFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * Delete file if exist path
     *
     * @param path the path
     * @return true if this file was deleted, false otherwise.
     */
    public static boolean deleteFileIfExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Delete file if exist file
     *
     * @param file the file
     * @return true if this file was deleted, false otherwise.
     */
    public static boolean deleteFileIfExist(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     */
    public static void writeToFile(File file, String content) {
        writeToFile(file, content, false, "utf-8");
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     * @param append
     */
    public static void writeToFile(File file, String content, boolean append) {
        writeToFile(file, content, append, "utf-8");
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     * @param encoding
     */
    public static void writeToFile(File file, String content, String encoding) {
        writeToFile(file, content, false, encoding);
    }

    /**
     * Write the specified content to an specified file.
     *
     * @param file
     * @param content
     * @param append
     * @param encoding
     */
    public static void writeToFile(File file, String content, boolean append, String encoding) {
        if (file == null || TextUtils.isEmpty(content)) {
            return;
        }
        ensureParent(file);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file, append), encoding);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(writer);
        }
    }

    /**
     * Write the specified data to an specified file.
     *
     * @param file The file to write into.
     * @param data The data to write. May be null.
     */
    public static final void writeToFile(File file, byte[] data) {
        if (file == null || data == null) {
            return;
        }
        ensureParent(file);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fos);
        }
    }

    /**
     * Write the specified input stream to an specified file. Use NIO
     *
     * @param is
     * @param target
     */
    public static void writeToFileNio(InputStream is, File target) {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;
        try {
            int len = is.available();
            src = Channels.newChannel(is);
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fo);
            IoUtils.closeQuietly(src);
            IoUtils.closeQuietly(out);
        }
    }

    /**
     * Write the specified data to an specified file.
     *
     * @param target
     * @param data
     */
    public static void writeToFileNio(File target, byte[] data) {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;
        try {
            src = Channels.newChannel(new ByteArrayInputStream(data));
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fo);
            IoUtils.closeQuietly(src);
            IoUtils.closeQuietly(out);
        }
    }

    /**
     * Read text file
     *
     * @param path
     * @return
     */
    public static String readFileText(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return readFileText(new File(path));
    }

    /**
     * Read text file
     *
     * @param file
     * @return
     */
    public static String readFileText(File file) {
        if (existsFile(file)) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                return IoUtils.loadContent(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(fis);
            }
        }
        return null;
    }

    /**
     * Read text file
     *
     * @param path
     * @param charsetName
     * @return
     */
    public static String readFileText(String path, String charsetName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            return IoUtils.loadContent(fis, charsetName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * Read file
     *
     * @param file
     * @return
     */
    public static byte[] readFileBytes(File file) {
        if (existsFile(file)) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                return IoUtils.loadBytes(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(fis);
            }
        }
        return null;
    }

    /**
     * Read config file
     *
     * @param file
     * @return
     */
    public static Map<String, String> readConfig(File file) {
        Map<String, String> map = new HashMap<String, String>();
        String text = readFileText(file);
        if (TextUtils.isEmpty(text)) {
            return map;
        }
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (TextUtils.isEmpty(line)) {
                continue;
            } else if (line.startsWith("#")) {
                continue;
            }
            String[] array = line.split("=", 2);
            map.put(array[0].trim(), array[1].trim());
        }
        return map;
    }

    /**
     * Open new file out put
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static FileOutputStream openNewFileOutput(File file) throws IOException {
        deleteFileIfExist(file);
        ensureParent(file);
        file.createNewFile();
        return new FileOutputStream(file);
    }

    /**
     * Get user directory
     *
     * @return
     */
    public static File getUserDir() {
        String path = System.getProperty("user.dir");
        return new File(path);
    }

    /**
     * Get user home directory
     *
     * @return
     */
    public static File getUserHome() {
        String path = System.getProperty("user.home");
        return new File(path);
    }

}
