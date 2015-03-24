package com.mcxiaoke.next.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import com.mcxiaoke.next.Charsets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 日志工具类，支持记录到文件，支持针对单个TAG设定日志级别
 * User: mcxiaoke
 * Date: 13-9-10 14-04-08
 * Time: 下午1:14
 */
public final class LogUtils {

    /**
     * 表示关闭LOG输出 *
     */
    public static final int LEVEL_OFF = Integer.MAX_VALUE;

    public static final String TAG_DEBUG = "LogUtils";
    private static final String FILE_LOG_DIR = "logs";

    private static Map<String, Long> sTraceMap = new HashMap<String, Long>();
    private static FileLogger sFileLogger;
    // 默认情况下log输出ERROR级别，file log不输出
    private static int sLoggingLevel = Log.ERROR;
    private static int sFileLoggingLevel = Log.ASSERT;

    private LogUtils() {
    }

    private static boolean needLog(int level) {
        return level >= sLoggingLevel;
    }

    public static void setLevel(int level) {
        sLoggingLevel = level;
    }

    public static void setFileLoggingLevel(Context appContext, int level) {
        sFileLoggingLevel = level;
        openFileLogger(appContext);
    }

    public static void e(Throwable t) {
        e(TAG_DEBUG, t);
    }

    public static void e(String tag, Throwable e) {
        if (needLog(Log.ERROR)) {
            Log.e(tag, "", e);

        }
    }

    public static void e(String tag, String message) {
        if (needLog(Log.ERROR)) {
            Log.e(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (needLog(Log.WARN)) {
            Log.w(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (needLog(Log.INFO)) {
            Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (needLog(Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (needLog(Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void v(String format, Object... args) {
        if (needLog(Log.VERBOSE)) {
            Log.v(TAG_DEBUG, buildMessage(format, args));
        }
    }

    public static void d(String format, Object... args) {
        if (needLog(Log.DEBUG)) {
            Log.d(TAG_DEBUG, buildMessage(format, args));
        }
    }

    public static void i(String format, Object... args) {
        if (needLog(Log.INFO)) {
            Log.i(TAG_DEBUG, buildMessage(format, args));
        }
    }

    public static void w(String format, Object... args) {
        if (needLog(Log.WARN)) {
            Log.w(TAG_DEBUG, buildMessage(format, args));
        }
    }

    public static void e(String format, Object... args) {
        if (needLog(Log.ERROR)) {
            Log.e(TAG_DEBUG, buildMessage(format, args));
        }
    }

    public static void e(Throwable tr, String format, Object... args) {
        if (needLog(Log.ERROR)) {
            Log.e(TAG_DEBUG, buildMessage(format, args), tr);
        }
    }

    public static void e(Class<?> clz, String message) {
        e(clz.getSimpleName(), message);
    }

    public static void w(Class<?> clz, String message) {
        w(clz.getSimpleName(), message);
    }

    public static void i(Class<?> clz, String message) {
        i(clz.getSimpleName(), message);
    }

    public static void d(Class<?> clz, String message) {
        d(clz.getSimpleName(), message);
    }

    public static void v(Class<?> clz, String message) {
        v(clz.getSimpleName(), message);
    }

    public static void e(Class<?> clz, Throwable t) {
        e(clz.getSimpleName(), t);
    }

    public static void e(String message) {
        if (needLog(Log.ERROR)) {
            Log.e(TAG_DEBUG, getMethodInfo(4));
            Log.e(TAG_DEBUG, "Message:\t" + message);
        }
    }

    public static void w(String message) {
        if (needLog(Log.WARN)) {
            Log.w(TAG_DEBUG, getMethodInfo(4));
            Log.w(TAG_DEBUG, "Message:\t" + message);
        }
    }

    public static void i(String message) {
        if (needLog(Log.INFO)) {
            Log.i(TAG_DEBUG, getMethodInfo(4));
            Log.i(TAG_DEBUG, "Message:\t" + message);
        }
    }

    public static void d(String message) {
        if (needLog(Log.DEBUG)) {
            Log.d(TAG_DEBUG, getMethodInfo(4));
            Log.d(TAG_DEBUG, "Message:\t" + message);
        }
    }

    public static void v(String message) {
        if (needLog(Log.VERBOSE)) {
            Log.v(TAG_DEBUG, getMethodInfo(4));
            Log.v(TAG_DEBUG, "Message:\t" + message);
        }
    }

    private static String getMethodInfo(int index) {
        final Thread current = Thread.currentThread();
        final StackTraceElement[] stack = current.getStackTrace();
        final StackTraceElement element = stack[index];
        if (!element.isNativeMethod()) {
            final String className = element.getClassName();
            final String fileName = element.getFileName();
            final int lineNumber = element.getLineNumber();
            final String methodName = element.getMethodName();
            return "Method:\t" + className + "." + methodName + "() (" + fileName + ":" + lineNumber + ")";
        }
        return "";
    }

    /**
     * 获取StackTrace信息
     */
    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        // Walk up the stack looking for the first caller outside of VolleyLog.
        // It will be at least two frames up, so start there.
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtils.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }

    public static void startTrace(String operation) {
        sTraceMap.put(operation, System.currentTimeMillis());
    }

    public static void stopTrace(String operation) {
        Long start = sTraceMap.remove(operation);
        if (start != null) {
            long end = System.currentTimeMillis();
            long interval = end - start;
            Log.v(TAG_DEBUG, operation + " use time: " + interval + "ms");
        }
    }

    public static void removeTrace(String key) {
        sTraceMap.remove(key);
    }

    public static void clearTrace() {
        sTraceMap.clear();
        Log.v(TAG_DEBUG, "trace is cleared.");
    }


    /**
     * 写log到文件
     */
    public static void fe(String tag, Throwable e) {
        fe(tag, "", e);
    }

    public static void fe(String tag, String message, Throwable e) {
        if (needLog(Log.ERROR)) {
            Log.e(tag, "", e);

        }
        if (isFileLoggable(Log.ERROR)) {
            if (sFileLogger != null) {
                sFileLogger.e(tag, message, e);
            }
        }
    }

    public static void fe(String tag, String message) {
        if (needLog(Log.ERROR)) {
            Log.e(tag, message);
        }
        if (isFileLoggable(Log.ERROR)) {
            if (sFileLogger != null) {
                sFileLogger.e(tag, message);
            }
        }
    }

    public static void fw(String tag, String message) {
        if (needLog(Log.WARN)) {
            Log.w(tag, message);
        }
        if (isFileLoggable(Log.WARN)) {
            if (sFileLogger != null) {
                sFileLogger.w(tag, message);
            }
        }
    }

    public static void fi(String tag, String message) {
        if (needLog(Log.INFO)) {
            Log.i(tag, message);
        }
        if (isFileLoggable(Log.INFO)) {
            if (sFileLogger != null) {
                sFileLogger.i(tag, message);
            }
        }
    }

    public static void fd(String tag, String message) {
        if (needLog(Log.DEBUG)) {
            Log.d(tag, message);
        }
        if (isFileLoggable(Log.DEBUG)) {
            if (sFileLogger != null) {
                sFileLogger.d(tag, message);
            }
        }
    }

    public static void fv(String tag, String message) {
        if (needLog(Log.VERBOSE)) {
            Log.v(tag, message);
        }
        if (isFileLoggable(Log.VERBOSE)) {
            if (sFileLogger != null) {
                sFileLogger.v(tag, message);
            }
        }
    }


    /**
     * *****
     * File Logger相关
     */

    private static boolean isFileLoggable(int level) {
        return level >= sFileLoggingLevel;
    }

    private static void closeFileLogger() {
        if (sFileLogger != null) {
            sFileLogger.close();
            sFileLogger = null;
        }
    }

    private static void openFileLogger(Context context) {
        closeFileLogger();
        if (sFileLoggingLevel < Log.ASSERT) {
            sFileLogger = new FileLogger(TAG_DEBUG, createFileLogDirIfNeeded(context));
        }
    }

    private static File createFileLogDirIfNeeded(Context context) {
        File dir;
        if (AndroidUtils.isMediaMounted()) {
            dir = new File(context.getExternalCacheDir(), FILE_LOG_DIR);
        } else {
            dir = new File(context.getCacheDir(), FILE_LOG_DIR);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    public void clearLogFiles(Context context) {
        File logDir = createFileLogDirIfNeeded(context);
        IOUtils.delete(logDir.getPath());
    }

    public void clearLogFilesAsync(final Context context) {
        new Thread() {
            @Override
            public void run() {
                clearLogFiles(context);
            }
        }.start();
    }

    private static class LogEntry {
        private static SimpleDateFormat dateFormat; // must always be used in the same thread
        private static Date mDate;

        private final long now;
        private final char level;
        private final String tag;
        private final String threadName;
        private final String msg;
        private final Throwable cause;
        private String date;

        LogEntry(char lvl, String tag, String threadName, String msg, Throwable tr) {
            this.now = System.currentTimeMillis();
            this.level = lvl;
            this.tag = tag;
            this.threadName = threadName;
            this.msg = msg;
            this.cause = tr;
        }

        private void addCsvHeader(final StringBuilder csv) {
            if (dateFormat == null)
                dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
            if (date == null) {
                if (null == mDate)
                    mDate = new Date();
                mDate.setTime(now);
                date = dateFormat.format(mDate);
            }

            csv.append(date);
            csv.append(',');
            csv.append(level);
            csv.append(',');
            csv.append(android.os.Process.myPid());
            csv.append(',');
            if (threadName != null)
                csv.append(threadName);
            csv.append(',');
            csv.append(',');
            if (tag != null)
                csv.append(tag);
            csv.append(',');
        }

        private void addException(final StringBuilder csv, Throwable tr) {
            if (tr == null)
                return;
            final StringBuilder sb = new StringBuilder(256);
            sb.append(cause.getClass());
            sb.append(": ");
            sb.append(cause.getMessage());
            sb.append('\n');

            for (StackTraceElement trace : cause.getStackTrace()) {
                //addCsvHeader(csv);
                sb.append(" at ");
                sb.append(trace.getClassName());
                sb.append('.');
                sb.append(trace.getMethodName());
                sb.append('(');
                sb.append(trace.getFileName());
                sb.append(':');
                sb.append(trace.getLineNumber());
                sb.append(')');
                sb.append('\n');
            }

            addException(sb, tr.getCause());
            csv.append(sb.toString().replace(';', '-').replace(',', '-').replace('"', '\''));
        }

        public CharSequence formatCsv() {
            final StringBuilder csv = new StringBuilder(256);
            addCsvHeader(csv);
            csv.append('"');
            if (msg != null) csv.append(msg.replace(';', '-').replace(',', '-').replace('"', '\''));
            csv.append('"');
            csv.append('\n');
            if (cause != null) {
                addCsvHeader(csv);
                csv.append('"');
                addException(csv, cause);
                csv.append('"');
                csv.append('\n');
            }
            return csv.toString();
        }
    }

    private static class FileLogger implements Handler.Callback {
        public static final String TAG = FileLogger.class.getSimpleName();
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HH", Locale.US);
        private static final String UTF_8 = Charsets.ENCODING_UTF_8;
        private static final int MSG_OPEN = 0;
        private static final int MSG_WRITE = 1;
        private static final int MSG_CLEAR = 2;
        public static long MAX_FILE_SIZE = 1024 * 1024 * 10;
        private File mLogDir;
        private File mLogFile;
        private String mTag;
        private HandlerThread mHandlerThread;
        private Handler mAsyncHandler;
        private Writer mWriter;

        public FileLogger(String logTag, File logDir) {
            mTag = logTag;
            mLogDir = logDir;
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            mAsyncHandler = new Handler(mHandlerThread.getLooper(), this);
            sendOpenMessage();
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN:
                    onMessageOpen(msg);
                    break;
                case MSG_WRITE:
                    onMessageWrite(msg);
                    break;
                case MSG_CLEAR:
                    onMessageClear();
                    break;
            }
            return true;
        }

        public void close() {
            Handler handler = mAsyncHandler;
            mAsyncHandler = null;
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            if (mHandlerThread != null) {
                mHandlerThread.quit();
                mHandlerThread = null;
            }
        }

        public void clear() {
            sendClearMessage();
        }

        private void onMessageOpen(Message msg) {
            closeWriter();
            openWriter();
        }

        private void onMessageWrite(Message msg) {
            try {
                LogEntry logMessage = (LogEntry) msg.obj;
                if (mWriter != null) {
                    mWriter.append(logMessage.formatCsv());
                    mWriter.flush();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getClass().getSimpleName() + " : " + e.getMessage());
            }

            verifyFileSize();
        }

        private void onMessageClear() {
            if (mLogFile != null) {
                closeWriter();
                IOUtils.delete(mLogDir);
                openWriter();
            }
        }

        private void sendOpenMessage() {
            if (mAsyncHandler != null) {
                mAsyncHandler.sendEmptyMessage(MSG_OPEN);
            }
        }

        private void sendWriteMessage(LogEntry log) {
            if (mAsyncHandler != null) {
                Message message = mAsyncHandler.obtainMessage(MSG_WRITE, log);
                mAsyncHandler.sendMessage(message);
            }
        }

        private void sendClearMessage() {
            if (mAsyncHandler != null) {
                mAsyncHandler.sendEmptyMessage(MSG_CLEAR);
            }
        }

        private void createLogFile() {
            if (mLogDir == null) {
                return;
            }
            if (!mLogDir.exists()) {
                mLogDir.mkdirs();
            }
            String fileName = String.format("log-%1$s.%2$s", DATE_FORMAT.format(new Date()), "txt");
            File file = new File(mLogDir, fileName);
            try {
                file.createNewFile();
                mLogFile = file;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "createLogFile ex=" + e);
            }
        }

        private void openWriter() {
            createLogFile();
            if (mLogFile == null) {
                return;
            }
            if (mWriter == null)
                try {
                    mWriter = new OutputStreamWriter(new FileOutputStream(mLogFile, true), UTF_8);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "can't get a writer for " + mLogFile + " : " + e.getMessage());
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "can't get a writer for " + mLogFile + " : " + e.getMessage());
                }
        }

        private void closeWriter() {
            if (mWriter != null) {
                try {
                    mWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mWriter = null;
            }
        }

        private void verifyFileSize() {
            if (mLogFile != null) {
                long size = mLogFile.length();
                if (size > MAX_FILE_SIZE) {
                    closeWriter();
                    mLogFile.delete();
                    openWriter();
                }
            }
        }

        public void d(String tag, String msg) {
            write('d', tag, msg);
        }

        public void d(String msg) {
            write('d', msg);
        }

        public void e(String tag, String msg, Throwable tr) {
            write('e', tag, msg, tr);
        }

        public void e(String tag, String msg) {
            write('e', tag, msg);
        }

        public void e(String msg) {
            write('e', msg);
        }

        public void i(String msg, String tag) {
            write('i', tag, msg);
        }

        public void i(String msg) {
            write('i', msg);
        }

        public void v(String msg, String tag) {
            write('v', tag, msg);
        }

        public void v(String msg) {
            write('v', msg);
        }

        public void w(String tag, String msg, Throwable tr) {
            write('w', tag, msg, tr);
        }

        public void w(String tag, String msg) {
            write('w', tag, msg);
        }

        public void w(String msg) {
            write('w', msg);
        }

        private void write(char lvl, String message) {
            String tag;
            if (mTag == null)
                tag = TAG;
            else
                tag = mTag;
            write(lvl, tag, message);
        }

        private void write(char lvl, String tag, String message) {
            write(lvl, tag, message, null);
        }

        private void write(char lvl, String tag, String message, Throwable tr) {
            if (tag == null) {
                write(lvl, message);
                return;
            }
            LogEntry log = new LogEntry(lvl, tag, Thread.currentThread().getName(), message, tr);
            sendWriteMessage(log);
        }
    }
}


