package com.jsxfedu.sfyjs_android.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by WEI CHAO on 2017/5/23.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    private FileUtil() {
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return isFileExist(file);
    }

    public static boolean isFileExist(File file) {
        return file.exists() && !file.isDirectory();
    }

    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return isFileEmpty(file);
    }

    public static boolean isFileEmpty(File file) {
        return !isFileExist(file) || file.length() <= 0;
    }

    public static boolean isFileDirExist(String filePath) {
        File fileDir = new File(filePath);
        return fileDir.exists() && fileDir.isDirectory();
    }

    public static void doFileDirExist(String fileDirPath) {
        File fileDir = new File(fileDirPath);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            boolean b = fileDir.mkdirs();
            Log.d(TAG, b + "");
        }
    }

    public static void doFileExist(String filePath) {
        File file = new File(filePath);
        doFileExist(file);
    }

    public static void doFileExist(File file) {
        File fileDir = file.getParentFile();
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            boolean b = fileDir.mkdirs();
            Log.d(TAG, b + "");
        }
        if (!file.exists() || file.isDirectory()) {
            try {
                boolean b = file.createNewFile();
                Log.d(TAG, b + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(final String string, final String filePath) {
        FileUtil.doFileExist(filePath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                Writer writer = null;
                try {
                    fos = new FileOutputStream(new File(filePath));
                    writer = new OutputStreamWriter(fos, "UTF-8");
                    writer.write(string);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static void writeFile(final InputStream is, final String filePath) {
        FileUtil.doFileExist(filePath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                try {
                    bis = new BufferedInputStream(is);
                    fos = new FileOutputStream(new File(filePath));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static void writeFileWithUiThread(final String string, final String filePath) {
        FileUtil.doFileExist(filePath);
        FileOutputStream fos = null;
        Writer writer = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            writer = new OutputStreamWriter(fos, "UTF-8");
            writer.write(string);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeFileWithUiThread(final InputStream is, final String filePath) {
        FileUtil.doFileExist(filePath);
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(new File(filePath));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static void readFileByBytes(String fileName) {
        File file = new File(fileName);
        InputStream in = null;
        try {
            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
            // 一次读一个字节
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                System.out.write(tempbyte);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            int byteread = 0;
            in = new FileInputStream(fileName);
            showAvailableBytes(in);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                System.out.write(tempbytes, 0, byteread);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static String readFileByChars(String fileName) {
        if (!isFileExist(fileName)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Reader reader = null;
        try {
            char[] buffer = new char[1024];
            int len = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            while ((len = reader.read(buffer)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((len == buffer.length) && (buffer[buffer.length - 1] != '\r')) {
                    sb.append(buffer);
                } else {
                    for (int i = 0; i < len; i++) {
                        if (buffer[i] != '\r') {
                            sb.append(buffer[i]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 随机读取文件内容
     */
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 显示输入流中还剩的字节数
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return deleteFile(file);
    }

    public static boolean deleteFile(File file) {
        return isFileExist(file) && file.delete();
    }

    public static boolean deleteDir(String filePath) {
        File file = new File(filePath);
        return deleteDir(file);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static void copyFile(String srcFilePath, String destFilePath) {
        FileUtil.doFileExist(destFilePath);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(srcFilePath);
            outputStream = new FileOutputStream(destFilePath);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
