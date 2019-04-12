package com.wh.camapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileUtils
{

	private String SDCardROOT;
	private boolean mIsCardExist = false;

	public String getSDPATH()
	{
		return SDCardROOT;
	}

	public FileUtils()
	{
		mIsCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

		SDCardROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}


	public File createFileInSDCard(String fileName, String dir) throws IOException
	{
		File file = new File(SDCardROOT + dir + File.separator + fileName);
		file.createNewFile();
		return file;
	}

	public File creatSDDir(String dir)
	{
		File dirFile = new File(SDCardROOT + dir + File.separator);
		dirFile.mkdirs();
		return dirFile;
	}

	public boolean isSdcardExist()
	{
		return mIsCardExist;
	}


	public boolean isFileExist(String fileName, String path)
	{
		File file = new File(SDCardROOT + path + File.separator + fileName);
		return file.exists();
	}

	public boolean isPathExist(String path)
	{
		File file = new File(SDCardROOT + path + File.separator);
		return file.exists();
	}

	public File write2SDFromInput(String path, String fileName, InputStream input)
	{
		File file = null;
		OutputStream output = null;
		try
		{
			creatSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp;
			while ((temp = input.read(buffer)) != -1)
			{
				output.write(buffer, 0, temp);
			}
			output.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				output.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}


	public static void delFolder(String folderPath)
	{
		try
		{
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public static boolean delAllFile(String path)
	{
		boolean flag = false;
		File file = new File(path);
		if (!file.exists())
		{
			return flag;
		}
		if (!file.isDirectory())
		{
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++)
		{
			if (path.endsWith(File.separator))
			{
				temp = new File(path + tempList[i]);
			}
			else
			{
				temp = new File(path + File.separator + tempList[i]);
			}

			if (temp.isFile())
			{
				temp.delete();
			}

			if (temp.isDirectory())
			{
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	public static String readFile(String filePath)
	{
		String dst = "";
		File file = new File(filePath);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String lineTxt = null;
			// 一次读入一行，直到读入null为文件结束
			while ((lineTxt = reader.readLine()) != null) {
				dst += lineTxt;
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
		return dst;
	}

	public static byte[] readImage(String filePath)
	{
		try {
			File filePic = new File(filePath);
			FileInputStream in = new FileInputStream(filePic);
			int size = in.available(); // 得到文件大小
			byte data[] = new byte[size];
			in.read(data); // 读数据
			in.close();
			return data;
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeBitmap(String path,int screenW,int screenH) {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
		//获取比例大小
		int wRatio = (int) Math.ceil(op.outWidth / screenW);
		int hRatio = (int) Math.ceil(op.outHeight / screenH);
		//如果超出指定大小，则缩小相应的比例
		if (wRatio > 1 && hRatio > 1) {
			if (wRatio > hRatio) {
				op.inSampleSize = wRatio;
			} else {
				op.inSampleSize = hRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, op);
		return bmp;
	}



	//读取文本文件中的内容
	public static String readTxtFile(String strFilePath)
	{
		String content = ""; //文件内容字符串
		//打开文件
		File file = new File(strFilePath);
		try {
			InputStream instream = new FileInputStream(file);
			InputStreamReader inputreader = new InputStreamReader(instream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;
			//分行读取
			while (( line = buffreader.readLine()) != null) {
				content += line + "\n";
			}
			instream.close();
		}
		catch (Exception e)
		{
			Log.d("", e.getMessage());
		}
		return content;
	}

	public static void writeTxtFile(String filePath,String s)
	{
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(s.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}