package com.autonavi.indoor.render3D;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Throwable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Logcat输出包装
 * 用户通过在手机上创建文件来控制是否输出log
 * 在sdcard/autonavi下创建文件logcat，讲会在屏幕上输出log，创建logcat文件夹则输出log到屏幕+文件到此目录下
 */
public class L{
	private static boolean mIsLogging = false;
	static boolean mSave2File = false;
	private static File mLogFile = null;

	private static FileOutputStream mFileOutputStream = null;
	public static final String logcatPath = "/sdcard/autonavi/indoor/logcat";
	public static final String logalgoPath = "/sdcard/autonavi/indoor/logalgo";
	static{
		onCreate();
	}
	private static void onCreate(){
		try	{
			mIsLogging = new File(logcatPath).exists();
			mSave2File = new File(logcatPath).isDirectory();
			Log.d("Locating", "Log instrument onCreate success.mIsLogging:"+mIsLogging+", mSave2File:"+mSave2File);
		}
		catch (Throwable e)	{
			Log.d("Locating", "Log instrument onCreate failed");
		}
	}

	public static boolean enableLog(boolean debug)	{
		mIsLogging = debug;
        Log.d("Locating", "enableLog:" + mIsLogging);
		return mIsLogging;
	}
	public static void destroy(){
		try {
			mFileOutputStream.close();
			mFileOutputStream = null;
			mLogFile = null;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public static boolean isSilent(){
		return !mIsLogging;
	}

	private static void d(String tag, String msg){
		if (!tag.contains("Locating")){
			tag = "Locating " + tag;
		}
		Log.d(tag, msg);
		if (mSave2File)	{
			if (mLogFile == null){
				createFile();
			}
		}else{
			return ;
		}
		try {
			msg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(new Date()) + "	" + Thread.currentThread().getId() + "(" + Thread.currentThread().getName() + ")	" + msg + "\n";
			mFileOutputStream.write(msg.getBytes());
			mFileOutputStream.flush();
//			mFileOutputStream.close();
		} catch (Throwable e) {
			L.d(e);
		}
	}
	private static void createFile(){
		File file = new File(logcatPath);
		if (!file.exists()){
			boolean ret = file.mkdirs();
			if (!ret) {
				return;
			}
		}
		mLogFile = new File(file, new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()) + ".txt");
		try{
			if (!mLogFile.exists()) {
				boolean ret = mLogFile.createNewFile();
				if (!ret) {
					L.d("failed to create logfile");
					return;
				}
			}

			mFileOutputStream = new FileOutputStream(mLogFile, true);
		}
		catch (Throwable e){
			L.d(e);
			mLogFile = null;
			mSave2File = false;
		}
	}
    public static void d(String msg){
        d(msg, 2);
    }

    public static void d(Object obj){
        d(obj+"", 2);
    }

	private static void d(String msg, int n){
		if (isSilent()){
			return;
		}
		Error err = new Error();
		StackTraceElement[] st = err.getStackTrace();
		if (st.length == 0) {
			Log.d("Locating", "getStackTrace() is empty!");
			return;
		}
		if (n >= st.length){
			Log.d("Locating", "getStackTrace() is not enough. n:"+n+", length:"+st.length);
			return;
		}
		String stack = st[n].toString();
		try{
//		d("stack", stack);
			String tag = stack.substring(0, stack.lastIndexOf("("));
			int from = tag.lastIndexOf("$");
			if (from < 0){
				from = tag.lastIndexOf("");
//				String method = tag.substring(from);
				tag = tag.substring(0, from);
				from = tag.lastIndexOf("");
				stack = stack.replace(tag.substring(0, from), "");
				//tag = tag.substring(from) + method;
			}
			else{
				//tag = tag.substring(from);
				stack = stack.substring(from);
			}
		}
		catch (Throwable e)	{
			e.printStackTrace();
		}
		d("", msg + "	[" + stack + "]");
	}

	@SuppressWarnings("boxing")
	public static void d(byte[] buf){
		if (isSilent())	{
			return;
		}
		if (buf == null || buf.length == 0)	{
			d("buf is null", 2);
			return;
		}
		StringBuilder m = new StringBuilder();
		for (int i = 0; i < buf.length && i < 1024; i++){
			m.append(String.format("%02X ", buf[i] & 0xFF));
		}
		m.setLength(m.length() - 1);
		d("[" + buf.length + "]:" + m, 2);
	}

	@SuppressWarnings("boxing")
	public static void d(String title, byte[] buf){
		if (isSilent())	{
			return;
		}
		if (buf == null || buf.length == 0)	{
			d("buf is null", 2);
			return;
		}
		StringBuilder m = new StringBuilder();
		for (int i = 0; i < buf.length && i < 1024; i++){
			m.append(String.format("%02X ", buf[i] & 0xFF));
		}
		m.setLength(m.length() - 1);
		d(title + "[" + buf.length + "]:" + m, 2);
	}

	public static void logStackTrace(String msg){
		if (isSilent())	{
			return;
		}
		d(msg, 2);
		d("Caller " + msg, 3);
	}

	public static void logStackTrace(){
		if (isSilent())	{
			return;
		}
		Error err = new Error();
		err.printStackTrace();
	}

	public static void d(Throwable e){
		if (isSilent())	{
			return;
		}
		d(e.getMessage(), 2);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		d(sw.toString(), 2);
//		e.printStackTrace();
	}
}
