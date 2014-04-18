package notUsedFiles;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import mySourceFiles.BMPDecoder;

public class BeGray 
{
	BMPDecoder newbmp=bmp;
	int m_b, m_g, m_r, m_gray, m_graydata;
	
	for(int i = 0; i < bmp.height ; i ++)
	{
		for(int j= 0; j< bmp.width; j++)
		{
			m_r= (newbmp.intData[i*bmp.width+j] & 0x00FF0000)>> 16; //右移16位
			m_b= newbmp.intData[i*bmp.width+j] & 0x000000FF;
			m_g= (newbmp.intData[i*bmp.width+j] & 0x0000FF00)>> 8; //右移8位
			m_gray= (30*m_r+59*m_g+11*m_b)/100;
			m_graydata= m_gray|(m_gray<< 8)|(m_gray<< 16)|0xFF000000;
			newbmp.intData[i*bmp.width+j]= m_graydata;
		}
	}
	
	MemoryImageSource mis= newbmp.makeImageSource(); //图像生成类
	MyFrame.BMPDecoder= newbmp;
	Image image= Toolkit.getDefaultToolkit().createImage(mis);
	MyFrame.canvas.myImage= image;
	MyFrame.canvas.repaint();
}

