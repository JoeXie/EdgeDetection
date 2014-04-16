package mySourceFiles;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bmpIO.BmpImage;
import bmpIO.BmpReader;
import bmpIO.BmpWriter;


public class EdgeDetcetionTest 
{
	/**********************************************
	 * 1、输入图片；
	 * 2、调用BeBinary类进行二值化处理；
	 * 3、调用GetGrainEdge类进行边缘跟踪；
	 * 4、保存图像。
	 * ********************************************/
	public static void main(String[] args) throws IOException
	{
		//导入图片Demo.bmp。
		InputStream in = new FileInputStream("Demo.bmp");
		BmpImage bmp;
		
		try 
		{
			bmp = BmpReader.read(in);
		} 
		finally 
		{
			in.close();
		}
		
		
		//调用BeBinary类进行二值化处理。
		
		//调用GetGrainEdge类进行边缘跟踪。
		
		//保存图像到Demo2.bmp。
		OutputStream out = new FileOutputStream("Demo2.bmp");
		
		try 
		{
			BmpWriter.write(out, bmp);
		} 
		finally 
		{
			out.close();
		}
		
		
		
		
		
	}
	
}
