package mySourceFiles;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DoTest 
{
	/**********************************************
	 * 1、输入图片；
	 * 2、调用BeBinary类进行二值化处理；
	 * 3、调用GetGrainEdge类进行边缘跟踪；
	 * 4、保存图像。
	 * ********************************************/
	public static void main(String[] args) throws IOException
	{
		/**********************************************
		 * 导入图片Demo.bmp。保存在bmp中。
		 **********************************************/
		InputStream in = new FileInputStream("Demo.bmp");
		
		BMPDecoder bmp = new BMPDecoder();
		try 
		{
			bmp.read(in);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			in.close();
		}
		
		
		/*********************************************
		 * 进行灰度化处理。
		 *********************************************/
		BMPDecoder newbmp = bmp;
		int m_b, m_g, m_r, m_gray, m_graydata;
		
		for(int i = 0; i < newbmp.height ; i ++)
		{
			for(int j = 0; j < newbmp.width; j ++)
			{
				m_r = (newbmp.intData[i * newbmp.width + j] & 0x00FF0000) >> 16; //右移16位
				m_b = newbmp.intData[i * newbmp.width + j] & 0x000000FF;
				m_g = (newbmp.intData[i * newbmp.width + j] & 0x0000FF00) >> 8; //右移8位
				m_gray = (30 * m_r + 59 * m_g + 11 * m_b) / 100;
				m_graydata = m_gray | (m_gray << 8) | (m_gray << 16) | 0xFF000000;
				newbmp.intData[i * newbmp.width + j] = m_graydata;
			}
		}
		
		
		MemoryImageSource mis= newbmp.makeImageSource(); //图像生成类？？？？？？？？？？？这个类和方法有待理解
		//MyFrame.BMPDecoder= newbmp;
		Image image= Toolkit.getDefaultToolkit().createImage(mis); //创建图像
		//MyFrame.canvas.myImage= image;
		//MyFrame.canvas.repaint();
		
		/**************************************************
		 * 二值化处理。
		 **************************************************/
		Binary graybmp = new Binary();
		newbmp = graybmp.beBinary(newbmp);
		
		
		/**************************************************
		 * 调用GetGrainEdge类进行边缘跟踪。
		 **************************************************/
		
		
		/*************************************************
		 * 将bmp中的图片保存图像到Demo2.bmp。
		 *************************************************/
		
		OutputStream out = new FileOutputStream("Demo2.bmp");
		
		try 
		{
			//Image img = Component.createImage(bmp.makeImageSource());
			//makeImageSource()不知道怎么使用
		} 
		finally 
		{
			out.close();
		}
		
		
		
		
		
	}
	
}
