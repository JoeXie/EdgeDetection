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
	 * 1������ͼƬ��
	 * 2������BeBinary����ж�ֵ������
	 * 3������GetGrainEdge����б�Ե���٣�
	 * 4������ͼ��
	 * ********************************************/
	public static void main(String[] args) throws IOException
	{
		//����ͼƬDemo.bmp��
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
		
		
		//����BeBinary����ж�ֵ������
		
		//����GetGrainEdge����б�Ե���١�
		
		//����ͼ��Demo2.bmp��
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
