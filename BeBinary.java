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
			m_r= (newbmp.intData[i*bmp.width+j] & 0x00FF0000)>> 16;//����16λ
			m_b= newbmp.intData[i*bmp.width+j] & 0x000000FF;
			m_g= (newbmp.intData[i*bmp.width+j] & 0x0000FF00)>> 8;//����8λ
			m_gray= (30*m_r+59*m_g+11*m_b)/100;
			m_graydata= m_gray|(m_gray<< 8)|(m_gray<< 16)|0xFF000000;
			newbmp.intData[i*bmp.width+j]= m_graydata;
		}
	}
	
	MemoryImageSource mis= newbmp.makeImageSource(); //ͼ��������
	MyFrame.bmpDecoder= newbmp;
	Image image= Toolkit.getDefaultToolkit().createImage(mis);
	MyFrame.canvas.myImage= image;
	MyFrame.canvas.repaint();
	

	void binary(MyFrame f,String ss,BMPDecoder bmp)
	{
		this.f= f;
		BMPDecoder binarybmp= bmp;
		autoThreshold(binarybmp);//��ȡֱ��ͼ->��ȡ��ֵ->��ֵ����
		MemoryImageSource mis=binarybmp.makeImageSource();
		MyFrame.bmpDecoder=binarybmp;
		MyFrame.canvas.myImage=Toolkit.getDefaultToolkit().createImage(mis);
		MyFrame.canvas.repaint();
	}

//�Զ���ȡ��ֵ
	public void autoThreshold(BMPDecoder bmp)
	{
		threshold(getAutoThreshold(bmp),bmp);
	}


//��ȡֱ��ͼ��ֱ��ͼ�ǵõ�ÿ�����ص�RGB ֵ��
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
				//�����Ͱ�λ
				v=m_binary[i*bmp.width+j];
				histogram[v]++;
			}
		}
		return histogram;//����00 ��FF ��ÿ�������������
	}


//��ȡ��ֵ
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

//��ֵ����
	public void threshold (int level,BMPDecoder bmp)
	{
		BMPDecoder newbmp=bmp;
		int m_binary;
		for(int i=0;i<bmp.height;i++)
		{
			for(int j=0;j<bmp.width;j++)
			{
				m_binary=newbmp.intData[i*bmp.width+j]&0x000000FF;//�����Ͱ�λ
				if (m_binary<=level) //����ֵС��LEVEL ȡ����ɫ����ɫ��
					m_binary=0xFF000000;
				else
					m_binary=0xFFFFFFFF; //����ֵ����LEVEL ȡǰ��ɫ����ɫ��
			
				newbmp.intData[i*bmp.width+j]=m_binary;

			}
		}
	}

}
