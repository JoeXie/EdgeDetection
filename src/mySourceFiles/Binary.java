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
		autoThreshold(binarybmp); //��ȡֱ��ͼ->��ȡ��ֵ->��ֵ����
		MemoryImageSource mis = binarybmp.makeImageSource();
		//MyFrame.BMPDecoder =binarybmp;
		//MySourceFiles.canvas.myImage=Toolkit.getDefaultToolkit().createImage(mis);
		//MyFrame.canvas.repaint();
	}
	
	
	//�Զ���ȡ��ֵ
	public void autoThreshold(BMPDecoder bmp)
	{
		threshold(getAutoThreshold(bmp), bmp);
	}


	//��ȡֱ��ͼ��ֱ��ͼ�ǵõ�ÿ�����ص�RGBֵ��
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
				m_binary[i * bmp.width + j] = newbmp.intData[i * bmp.width + j] & 0x000000FF; //ȡ�Ͱ�λ
				v = m_binary[i * bmp.width + j];
				histogram[v] ++;
			}
		}
		
		return histogram;//���ػҶȷֲ���00 ��FF�����ص������ֲ�����
	}


	//��ȡ��ֵ
	public int getAutoThreshold(BMPDecoder bmp)
	{
		BMPDecoder newbmp = bmp;
		int level;
		int[] histogram = getHistogram(newbmp); //��ûҶ�ֱ��ͼ
		double result, tempSum1, tempSum2, tempSum3, tempSum4;
		histogram[0] = 0;
		histogram[255] = 0;

		int min = 0;
		while((histogram[min] == 0) && (min < 255)) //�ҳ����ػҶ���С�Ƕ���
			min ++;
		int max = 255;
		while((histogram[max] == 0) && (max > 0)) //�ҳ����ػҶ�����Ƕ���
			max --;
		
		if(min >= max)             //��������������һ��ɫ��ͼƬ������Ϊʲô�Ҷ�һ��Ϊ128��
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
	
		level = (int)Math.round(result); //������ӽ�result��int����ֵ
		return level;
	}

	
	//��ֵ����
	public void threshold (int level,BMPDecoder bmp)
	{
		BMPDecoder newbmp = bmp;
		int m_binary;
		for(int i = 0; i < bmp.height; i ++)
		{
			for(int j = 0; j < bmp.width; j ++)
			{
				m_binary = newbmp.intData[i * bmp.width + j] & 0x000000FF; //ȡ�Ͱ�λ��ȡ�Ҷ�ֵ��
				if (m_binary <= level) //����ֵС��LEVEL ȡ����ɫ����ɫ��
					m_binary = 0xFF000000;
				else
					m_binary = 0xFFFFFFFF; //����ֵ����LEVEL ȡǰ��ɫ����ɫ��
				
				newbmp.intData[i*bmp.width+j]=m_binary;
			}
		}
	}
	
	
	
}
