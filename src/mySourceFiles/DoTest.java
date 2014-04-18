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
	 * 1������ͼƬ��
	 * 2������BeBinary����ж�ֵ������
	 * 3������GetGrainEdge����б�Ե���٣�
	 * 4������ͼ��
	 * ********************************************/
	public static void main(String[] args) throws IOException
	{
		/**********************************************
		 * ����ͼƬDemo.bmp��������bmp�С�
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
		 * ���лҶȻ�����
		 *********************************************/
		BMPDecoder newbmp = bmp;
		int m_b, m_g, m_r, m_gray, m_graydata;
		
		for(int i = 0; i < newbmp.height ; i ++)
		{
			for(int j = 0; j < newbmp.width; j ++)
			{
				m_r = (newbmp.intData[i * newbmp.width + j] & 0x00FF0000) >> 16; //����16λ
				m_b = newbmp.intData[i * newbmp.width + j] & 0x000000FF;
				m_g = (newbmp.intData[i * newbmp.width + j] & 0x0000FF00) >> 8; //����8λ
				m_gray = (30 * m_r + 59 * m_g + 11 * m_b) / 100;
				m_graydata = m_gray | (m_gray << 8) | (m_gray << 16) | 0xFF000000;
				newbmp.intData[i * newbmp.width + j] = m_graydata;
			}
		}
		
		
		MemoryImageSource mis= newbmp.makeImageSource(); //ͼ�������ࣿ�������������������������ͷ����д����
		//MyFrame.BMPDecoder= newbmp;
		Image image= Toolkit.getDefaultToolkit().createImage(mis); //����ͼ��
		//MyFrame.canvas.myImage= image;
		//MyFrame.canvas.repaint();
		
		/**************************************************
		 * ��ֵ������
		 **************************************************/
		Binary graybmp = new Binary();
		newbmp = graybmp.beBinary(newbmp);
		
		
		/**************************************************
		 * ����GetGrainEdge����б�Ե���١�
		 **************************************************/
		
		
		/*************************************************
		 * ��bmp�е�ͼƬ����ͼ��Demo2.bmp��
		 *************************************************/
		
		OutputStream out = new FileOutputStream("Demo2.bmp");
		
		try 
		{
			//Image img = Component.createImage(bmp.makeImageSource());
			//makeImageSource()��֪����ôʹ��
		} 
		finally 
		{
			out.close();
		}
		
		
		
		
		
	}
	
}
