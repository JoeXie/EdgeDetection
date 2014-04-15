import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;


public class BeBinary
{
	BMPDecoder newbmp= bmp;
	int m_b, m_g, m_r, m_gray, m_graydata;
	
	for(int i= 0; i< bmp.height; i++)
	{
		for(int j= 0; j< bmp.width; j++)
		{
			m_r= (newbmp.intData[i*bmp.width+j] & 0x00FF0000)>> 16;//右移16位
			m_b= newbmp.intData[i*bmp.width+j] & 0x000000FF;
			m_g= (newbmp.intData[i*bmp.width+j] & 0x0000FF00)>> 8;//右移8位
			m_gray= (30*m_r+59*m_g+11*m_b)/100;
			m_graydata= m_gray|(m_gray<< 8)|(m_gray<< 16)|0xFF000000;
			newbmp.intData[i*bmp.width+j]= m_graydata;
		}
	}
	
	MemoryImageSource mis= newbmp.makeImageSource(); //图像生成类
	MyFrame.bmpDecoder= newbmp;
	Image image= Toolkit.getDefaultToolkit().createImage(mis);
	MyFrame.canvas.myImage= image;
	MyFrame.canvas.repaint();
	

	void binary(MyFrame f,String ss,BMPDecoder bmp)
	{
		this.f= f;
		BMPDecoder binarybmp= bmp;
		autoThreshold(binarybmp);//获取直方图->获取阈值->二值处理
		MemoryImageSource mis=binarybmp.makeImageSource();
		MyFrame.bmpDecoder=binarybmp;
		MyFrame.canvas.myImage=Toolkit.getDefaultToolkit().createImage(mis);
		MyFrame.canvas.repaint();
	}

//自动获取阈值
	public void autoThreshold(BMPDecoder bmp)
	{
		threshold(getAutoThreshold(bmp),bmp);
	}


//获取直方图（直方图是得到每个像素的RGB 值）
	public int[] getHistogram(BMPDecoder bmp)
	{
		BMPDecoder newbmp=bmp;
		int[] m_binary=new int[bmp.width*bmp.height];
		int[] histogram=new int[256];
		int v;
		for(int i=0;i<bmp.height;i++)
		{
			for(int j=0;j<bmp.width;j++)
			{
				m_binary[i*bmp.width+j]=newbmp.intData[i*bmp.width+j]&0x000000FF;
				//保留低八位
				v=m_binary[i*bmp.width+j];
				histogram[v]++;
			}
		}
		return histogram;//返回00 到FF 间每个点的数量数组
	}


//获取阈值
	public int getAutoThreshold(BMPDecoder bmp)
	{
		
	}

	BMPDecoder newbmp=bmp;
	int level;
	int[] histogram=getHistogram(newbmp);
	double result,tempSum1,tempSum2,tempSum3,tempSum4;
	histogram[0]=0;
	histogram[255]=0;

	int min=0;
	while((histogram[min]==0)&&(min<255))
		min++;
	int max=255;
	while((histogram[max]==0)&&(max>0))
		max--;
	if(min>=max)
	{
		level=128;
		return level;
	}
	int movingIndex=min;
	do
	{
		tempSum1=tempSum2=tempSum3=tempSum4=0.0;
		for(int i=min;i<=movingIndex;i++)
		{
			tempSum1+=i*histogram[i];
			tempSum2+=histogram[i];
		}
		for(int i=(movingIndex+1);i<=max;i++)
		{
			tempSum3+=i*histogram[i];
			tempSum4+=histogram[i];
		}
		result=(tempSum1/tempSum2/2.0)+(tempSum3/tempSum4/2.0);
		movingIndex++;
	}
	while ((movingIndex+1)<=result&&movingIndex<=(max-1));
	
	level=(int)Math.round(result);
	return level;

//二值处理
	public void threshold (int level,BMPDecoder bmp)
	{
		BMPDecoder newbmp=bmp;
		int m_binary;
		for(int i=0;i<bmp.height;i++)
		{
			for(int j=0;j<bmp.width;j++)
			{
				m_binary=newbmp.intData[i*bmp.width+j]&0x000000FF;//保留低八位
				if (m_binary<=level) //像素值小于LEVEL 取背景色（黑色）
					m_binary=0xFF000000;
				else
					m_binary=0xFFFFFFFF; //像素值大于LEVEL 取前景色（白色）
			
				newbmp.intData[i*bmp.width+j]=m_binary;

			}
		}
	}

}
