package com.tochange.yang.lib.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.tochange.yang.lib.Utils;
import com.tochange.yang.lib.log;

import android.content.Context;
import android.util.Log;

/**
 * 文件操作类 (针对目录和文件的拷贝，移动，删除)
 */
public class FileOperateHelper {
	
	private final static String TAG = "FileOperateHelper";
	
    public static void getFileStat(String path)
    {
        try
        {
            Process p = Runtime.getRuntime().exec("stat " + path);
            p.waitFor();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line = bf.readLine();
            while (line != null)
            {
                line = bf.readLine();
                log.e("stat:" + line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    public static boolean copyAssetsToFiles(Context context, String filaName)
    {
        if (!(new File(context.getFilesDir() + File.separator + filaName))
                .exists())
        {
            try
            {
                InputStream inputStream = context.getAssets().open(filaName);
                int length = inputStream.available();
                byte[] buffer = new byte[length];
                inputStream.read(buffer);
                inputStream.close();
                FileOutputStream fileOutputStream = context.openFileOutput(
                        filaName, Context.MODE_PRIVATE);
                fileOutputStream.write(buffer);
                fileOutputStream.close();
                return true;
            }
            catch (FileNotFoundException e)
            {
                log.e(e.toString());
                return false;
            }
            catch (IOException e)
            {
                log.e(e.toString());
                return false;
            }

        }
        else
            return true;
    }
    
    
    public static String getCurTimeToString(int i, int n)
    {
        Calendar c = Calendar.getInstance();
        if (c == null)
            return null;
        String time;
        String s1 = "-", s2 = ":", s3 = " ";
        c.add(Calendar.DATE, n);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond = c.get(Calendar.SECOND);

        time = "" + mYear;
        if (1 == i)
            time += s1;
        int mon = mMonth + 1;
        if (mon < 10)
            time = time + 0 + mon;
        else
            time += mon;
        if (1 == i)
            time += s1;
        if (mDay < 10)
            time = time + 0 + mDay;
        else
            time += mDay;
        if (1 == i)
            time += s3;
        if (mHour < 10)
            time = time + 0 + mHour;
        else
            time += mHour;
        if (1 == i)
            time += s2;
        if (mMinute < 10)
            time = time + 0 + mMinute;
        else
            time += mMinute;
        if (1 == i)
            time += s2;
        if (mSecond < 10)
            time = time + 0 + mSecond;
        else
            time += mSecond;

        time = time.replace(" ", "_").replace(":", ".");
        time = "_" + time;
        return time;
    }
    
	/**
	 * 供APP层调用，删除文件
	 * 
	 * @param file 预删除的目标文件句柄
	 * @return	操作是否成功的标识
	 */
	public static boolean delFile(File file) {
		if (file == null){
			Log.e(TAG, "delFile, file = null!");
			return false;
		}			
		if (file.isDirectory()){
			Log.e(TAG, "delFile, file is a Directory!");
			return false;
		}			
		return file.delete();
	}

	/**
	 * 删除一个目录(包括其子目录和文件)（可以是非空目录）
	 * 
	 * @param dir	删除目录的句柄
	 * @return	操作是否成功的标识
	 */
	public static boolean delDir(File dir) {
		if (dir == null){
			Log.e(TAG, "delDir, dir = null!");
			return false;
		}	
		if (!dir.exists()) {
			Log.e(TAG, "delDir, dir not exists!");
			return false;
		}
		if (!dir.isDirectory()) {
			Log.e(TAG, "delDir, dir is not a Directory!");
			return false;
		}
		
		boolean flag = true;		
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				flag = file.delete();
			} else if (file.isDirectory()) {
				flag = delDir(file);
			}
			if (!flag) {
				return flag;
			}
		}
		flag = dir.delete();
		return flag;
	}
	
	/**
	 * 删除一个目录(包括其子目录和文件)（可以是非空目录）
	 * 
	 * @param dir
	 *            删除目录的句柄
	 * @return 操作是否成功的标识
	 */
	public static boolean delDir(String dir) {
		if(dir == null || dir.trim().equals("")){
			Log.e(TAG, "delDir, dir is null!");
			return false;
		}
		dir = dir.trim();
		if(!dir.endsWith(File.separator)){
			dir += File.separator;
		}
		
		boolean flag = true;
		File dirFile = new File(dir);
		if (dirFile == null){
			Log.e(TAG, "delDir, dirFile = null!");
			return false;
		}	
		if (!dirFile.exists()) {
			Log.e(TAG, "delDir, dirFile not exists!");
			return false;
		}
		if (!dirFile.isDirectory()) {
			Log.e(TAG, "delDir, dirFile is not a Directory!");
			return false;
		}		
		
		for (File file : dirFile.listFiles()) {	
			if(file == null){
				Log.e(TAG, "delDir, file = null! continue~");
				continue;
			}
			if (file.isFile()) {
				flag = file.delete();					
				
			} else if (file.isDirectory()) {
				flag = delDir(file.getAbsolutePath());
			}
			Log.d("delDir", "delete file :" + file.getAbsolutePath() + " , " + flag);
			if (!flag) {
				return flag;
			}			
		}
		flag = dirFile.delete();
		return flag;
	}
	
	/**
	 * 拷贝一个文件,srcFile源文件，destFile目标文件
	 * 
	 * @param srcFile
	 *            源文件句柄
	 * @param destFile
	 *            目标文件句柄
	 * @return 操作是否成功的标识
	 * @throws IOException
	 *             向调用方抛出IO异常
	 */
	public static boolean copyFileTo(File srcFile, File destFile)
			throws FileNotFoundException, IOException {
		if(srcFile == null){
			Log.e(TAG, "copyFileTo, srcFile = null!");
			return false;
		}
		if(destFile == null){
			Log.e(TAG, "copyFileTo, destFile = null!");
			return false;
		}
		if (srcFile.isDirectory()){
			Log.e(TAG, "copyFileTo, srcFile is a Directory!");
			return false;
		}
		if (destFile.isDirectory()){
			Log.e(TAG, "copyFileTo, destFile is a Directory!");
			return false;
		}
			
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try{
			fis = new FileInputStream(srcFile);			
			fos = new FileOutputStream(destFile);
			int readLen = 0;
			byte[] buf = new byte[1024];
			while ((readLen = fis.read(buf)) != -1) {
				fos.write(buf, 0, readLen);
			}
			fos.flush();
		}catch(FileNotFoundException e){
			Log.e(TAG, "copyFileTo, new file stream failed, FileNotFoundException : " + e);	
			throw e;			
		}catch(IOException ioe){
			Log.e(TAG, "copyFileTo, read or write file stream failed, IOException : " + ioe);
			throw ioe;
		}finally{
			if(fos != null){
				fos.close();
			}
			if(fis != null){
				fis.close();
			}
		}
		return true;
	}

	/**
	 * 拷贝目录下的所有文件到指定目录
	 * 
	 * @param srcDir	源目录句柄
	 * @param destDir	目标目录句柄
	 * @param callback	拷贝过程中的回调接口，将拷贝文件的相关信息传给函数调用者
	 * @param copyFlag	copyFlag=true 进行实际的拷贝动作 copyFlag=false 不进行实际的拷贝动作，仅是传递文件相关信息
	 * @return	操作是否成功的标识
	 * @throws FileNotFoundException
	 * @throws IOException	向调用方抛出IO异常
	 */
	public static boolean copyFilesTo(File srcDir, File destDir,
			IFileOperateCallback callback, boolean copyFlag) throws FileNotFoundException, IOException{
		// 判断源目录的File实例是否为null或者目录不存在
		if(srcDir == null || !srcDir.exists()){
			Log.e(TAG, "copyFilesTo, src file is not exist or equals null! srcDir = " + srcDir);
			return false;
		}
		// 判断目标目录的File实例是否为null
		if (destDir == null) {
			Log.e(TAG, "copyFilesTo, dest file equals null!");
			return false;
		}
		//判断源目录是否是一个文件夹
		if(!srcDir.isDirectory()){
			Log.e(TAG, "copyFilesTo, srcDir is not a  Directory!");
			return false;
		}
		// 判断目录是否存在，不存在的话就创建目录
		if (!destDir.exists()) {
			boolean mkdirs = destDir.mkdirs();
			if(!mkdirs){
				Log.e(TAG, "copyFilesTo, destDir mkdirs failed!");
				return false;
			}
		}
		// 判断目标目录是否是文件夹
		if (!destDir.isDirectory()){
			Log.e(TAG, "copyFilesTo, dest dir is not a directory !");
			return false;
		}
		
		//开始复制文件	
		Log.d(TAG, "copyFilesTo, copy dir to " + destDir.getPath() + "/" + srcDir.getName());
		File[] srcFiles = srcDir.listFiles();
		for (int i = 0; i < srcFiles.length; i++) {
			if (srcFiles[i].isFile()) {
				// 获得目标文件
				if (copyFlag == true) {
					String destPath = destDir.getPath() + "/" + srcFiles[i].getName();
					File destFile = new File(destPath);
					if(destFile == null){
						Log.e(TAG, "copyFilesTo, continue, new dest File failed！");
						continue;
					}	
					Log.d(TAG, "copyFilesTo, copy file to " + destPath);
					copyFileTo(srcFiles[i], destFile);
				}

				if (callback != null) {
					Map<String, String> operateParamMap = new HashMap<String, String>();
					operateParamMap.put("srcfilename", srcFiles[i].getName());

					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(srcFiles[i].lastModified());
					String lastModifyed = getCurTimeToString(1,
							0);
					operateParamMap.put("lastmodifyed", lastModifyed);

					callback.operate(operateParamMap);
				}
			} else if (srcFiles[i].isDirectory()) {
				if (copyFlag == true) {					
					File theDestDir = new File(destDir.getPath() + "/" + srcFiles[i].getName());
					copyFilesTo(srcFiles[i], theDestDir, callback, true);					
				}
			}
		}
		return true;
	}

	/**
	 * 移动一个文件
	 * 
	 * @param srcFile	源文件句柄
	 * @param destFile	移动到的目标文件句柄
	 * @return	操作是否成功的标识
	 * @throws FileNotFoundException
	 * @throws IOException	向调用方抛出IO异常
	 */
	public static boolean moveFileTo(File srcFile, File destFile)
			throws FileNotFoundException, IOException {
		boolean iscopy = copyFileTo(srcFile, destFile);
		if (!iscopy)
			return false;
		delFile(srcFile);
		return true;
	}

	/**
	 * 移动目录下的所有文件到指定目录
	 * 
	 * @param srcDir	源目录句柄
	 * @param destDir	移动到的目标目录句柄
	 * @return	操作是否成功的标识
	 * @throws FileNotFoundException
	 * @throws IOException	向调用方抛出IO异常
	 */
	public static boolean moveFilesTo(File srcDir, File destDir)
			throws FileNotFoundException, IOException {
		if (srcDir.isDirectory() && !destDir.exists()) {
			destDir.mkdirs();// 判断目标目录是否存在
		}

		if (!srcDir.isDirectory() || !destDir.isDirectory()) {
			return false;
		}
		File[] srcDirFiles = srcDir.listFiles();
		for (int i = 0; i < srcDirFiles.length; i++) {
			if (srcDirFiles[i].isFile()) {
				File oneDestFile = new File(destDir.getPath() + "\\"
						+ srcDirFiles[i].getName());
				moveFileTo(srcDirFiles[i], oneDestFile);
				delFile(srcDirFiles[i]);
			} else if (srcDirFiles[i].isDirectory()) {
				File oneDestFile = new File(destDir.getPath() + "\\"
						+ srcDirFiles[i].getName());
				moveFilesTo(srcDirFiles[i], oneDestFile);
				delDir(srcDirFiles[i]);
			}
		}
		return true;
	}

	/**
	 * 复制文件夹下的所有文件
	 * 
	 * @param src	源路径
	 * @param des	目标路径
	 * @return	true：拷贝成功；false：拷贝失败
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean copyFilesTo(final String src, final String des) throws FileNotFoundException, IOException{
    	File oldFile = new File(src);
		File newFile = new File(des);
		return copyFilesTo(oldFile, newFile, null, true);
	}
	
	/**
	 * 供APP层使用linux命令删除文件(夹)
	 * 
	 * @param path	预删除的文件(夹)的路径
	 * @return
	 */
	public static boolean delFileByShellCmd(String path){
		Log.d(TAG, "delFileByShellCmd, del path by shell command!");
		try{
			Process pro = Runtime.getRuntime().exec("rm -r " + path);
			int i = pro.waitFor();
			Log.d(TAG, "i=" + i);
		}catch(IOException ioe){
			return false;
		}catch(InterruptedException ie){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取文件的大小
	 * 
	 * @param file 目标文件
	 * @return	该文件的大小
	 */
	public static long getFileSize(File file){
		if(file == null){
			return 0;
		}
		long size = 0;
		File[] flist = file.listFiles();
		if(flist != null){
			for(int i=0;i<flist.length;i++){
				if(flist[i].isDirectory()){
					size = size + getFileSize(flist[i]);
				}else{
					size = size + flist[i].length();
				}
			}
		}
		return size/1024;
	}
	
}