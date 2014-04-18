package mySourceFiles;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;


public class Binary
{
	//void binary(MyFrame f, String ss, BMPDecoder bmp)
	void binary(String ss, BMPDecoder bmp)
	{
		//this.f = f;
		BMPDecoder binarybmp = bmp;
		autoThreshold(binarybmp); //获取直方图->获取阈值->二值处理
		MemoryImageSource mis = binarybmp.makeImageSource();
		//MyFrame.BMPDecoder =binarybmp;
		//MySourceFiles.canvas.myImage=Toolkit.getDefaultToolkit().createImage(mis);
		//MyFrame.canvas.repaint();
	}
	
	
	//自动获取阈值
	public void autoThreshold(BMPDecoder bmp)
	{
		threshold(getAutoThreshold(bmp), bmp);
	}


	//获取直方图（直方图是得到每个像素的RGB值）
	public int[] getHistogram(BMPDecoder bmp)
	{
		BMPDecoder newbmp = bmp;
		int[] m_binary = new int[bmp.width * bmp.height];
		int[] histogram = new int[256];
		int v;
		
		for(int i = 0; i < bmp.height; i ++)
		{
			for(int j = 0; j < bmp.width;j ++)
			{
				m_binary[i * bmp.width + j] = newbmp.intData[i * bmp.width + j] & 0x000000FF; //取低八位
				v = m_binary[i * bmp.width + j];
				histogram[v] ++;
			}
		}
		
		return histogram;//返回灰度分布在00 到FF间像素的数量分布数组
	}


	//获取阈值
	public int getAutoThreshold(BMPDecoder bmp)
	{
		BMPDecoder newbmp = bmp;
		int level;
		int[] histogram = getHistogram(newbmp); //获得灰度直方图
		double result, tempSum1, tempSum2, tempSum3, tempSum4;
		histogram[0] = 0;
		histogram[255] = 0;

		int min = 0;
		while((histogram[min] == 0) && (min < 255)) //找出像素灰度最小是多少
			min ++;
		int max = 255;
		while((histogram[max] == 0) && (max > 0)) //找出像素灰度最大是多少
			max --;
		
		if(min >= max)             //？？？？？？单一颜色的图片？？？为什么灰度一定为128？
		{
			level = 128;
			return level;
		}
		
		int movingIndex = min;
		do
		{
			tempSum1=tempSum2=tempSum3=tempSum4=0.0;
			for(int i = min; i <= movingIndex; i ++)
			{
				tempSum1 += i * histogram[i];
				tempSum2 += histogram[i];
			}
			for(int i=(movingIndex+1);i<=max;i++)
			{
				tempSum3 += i * histogram[i];
				tempSum4 += histogram[i];
			}
			result = (tempSum1 / tempSum2 / 2.0) + (tempSum3 / tempSum4 / 2.0);
			movingIndex ++;
		} while ((movingIndex + 1) <= result && movingIndex <= (max - 1));
	
		level = (int)Math.round(result); //返回最接近result的int型数值
		return level;
	}

	
	//二值处理
	public void threshold (int level,BMPDecoder bmp)
	{
		BMPDecoder newbmp = bmp;
		int m_binary;
		for(int i = 0; i < bmp.height; i ++)
		{
			for(int j = 0; j < bmp.width; j ++)
			{
				m_binary = newbmp.intData[i * bmp.width + j] & 0x000000FF; //取低八位（取灰度值）
				if (m_binary <= level) //像素值小于LEVEL 取背景色（黑色）
					m_binary = 0xFF000000;
				else
					m_binary = 0xFFFFFFFF; //像素值大于LEVEL 取前景色（白色）
				
				newbmp.intData[i*bmp.width+j]=m_binary;
			}
		}
	}
	
	
	
}
