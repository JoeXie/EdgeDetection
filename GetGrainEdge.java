import java.awt.Polygon;


public class GetGrainEdge
{
		static	final int UP=0,DOWN=1,UP_OR_DOWN=2,LEFT=3,RIGHT=4,LEFT_OR_RIGHT=5,NA=6;
	public int npoints;  //�����߽�����
	private int maxPoints=1000; //��������߽�����
	private int maxPoints0=400;	//�����������
	public int[] xpoints=new int[maxPoints];//�洢�����߽�����
	public int[] ypoints=new int[maxPoints];
	public int width,height,Xstart,Ystart;
	private int[] cpixels;  //����ֵ����
	private int lowerThreshold,upperThreshold;
	public boolean yes;
	
	public int[][] xxpoints=new int[maxPoints0][maxPoints];//��maxPoints0�����ı߽������
	public int[][] yypoints=new int[maxPoints0][maxPoints];
	//ÿһ�� xxpoints�洢�����ص����
	public int[] nnpoints=new int[maxPoints];  //ĳ�����߽���������
	//��������
	public int n=0;
	Polygon[] polygon=new Polygon[maxPoints0];//����Σ������߽磩����
	int[] area=new int[maxPoints0];      //����ռ�����ص����
	public int[] X=new int[maxPoints0];  //������������
	public int[] Y=new int[maxPoints0];				   

	public GetGrainEdge(MyFrame f, String ss, BMPDecoder bmp)
	{
		BMPDecoder newbmp=bmp;
		int m_b;
		width=newbmp.width;
		height=newbmp.height;
		cpixels=new int[width*height];
		          //(ԭ����������ʼλ�ã�Ŀ������������ʼλ�ã�������С)
		System.arraycopy(newbmp.intData, 0, cpixels, 0, width*height);//������ͼ������ֵ���Ƶ�cpixels������
		
		outer1: for(int y=newbmp.height*(100-ChangeMyFrame.processSize)/200;
					y<newbmp.height*(100+ChangeMyFrame.processSize)/200;y++)
				{
					for(int x=newbmp.width*(100-ChangeMyFrame.processSize)/200;
						x<newbmp.width*(100+ChangeMyFrame.processSize)/200; x++)
					{
						m_b=newbmp.intData[y*bmp.width+x];  //&0x00FFFFFF;
						if(m_b==0xFF000000)  //ȷ����ɫΪ��ʼ��
						{
							Xstart=x;
							Ystart=y;
							break outer1;
						}
					}
				}
		
		autoOutline(Xstart,Ystart);
		getRestEdge();
		centerOfGrain();
	}
	
	/*************************************************************************************
	private int getBytePixel(int x,int y) ��  ��øõ������ֵ
	**************************************************************************************/
	private int getBytePixel(int x,int y)
	{
		if(x>=0&&x<width&&y>=0&&y<height)
		{
			return cpixels[y*width+x];
		}
		else
			return 0xFFFFFFFF;    //ͼ���С֮���Ϊ��ɫ
	}
	/****************************************************************************************
	private boolean inside(int x,int y) ��   �ж��Ƿ����ʼ��ֵ��ͬ
	****************************************************************************************/
	private boolean inside(int x,int y)
	{
		int value;
		value=getBytePixel(x,y);
		return value==lowerThreshold&&value==upperThreshold;//�Ƿ����ʼ����ͬ
	}
	/*************************************************************************
	boolean isLine(int xs,int ys) ��       �ж��Ƿ��ڿ�����
	*************************************************************************/
	boolean isLine(int xs,int ys)//�ھ���(x,y-5),(x+10,y-5),(x+10,y+5),(x,y+5)��
	{
		int r=5;
		int xmin=xs;
		int xmax=xs+2*r;
		
		if(xmax>width)
			xmax=width-1;
		int ymin=ys-r;
		
		if(ymin<0)
			ymin=0;
		int ymax=ys+r;
		
		if(ymax>=height)
			ymax=height-1;
		int area=0;
		int insideCount=0;
		for(int x=xmin;(x<=xmax);x++)
			for(int y=ymin;y<=ymax;y++)
			{
				area++;
				if(inside(x,y))
					insideCount++;
			}
		return((double)insideCount)/area>=0.75; //�ķ�֮�����϶��ڿ����ڣ���ͬһ��
	}
	/*startx,starty���ڿ����ϵĵ㣬xpoints,ypoints����
	�洢�߽�㣬����table[]���������߽�ɨ�跽��*/
	public void autoOutline(int startX,int startY)
	{
		int x=startX;
		int y=startY;
		int direction;
		lowerThreshold=upperThreshold=getBytePixel(startX,startY);
		do
		{
			x++;
		}
		while(inside(x,y));
		
		if(isLine(x,y))
		{
			lowerThreshold=upperThreshold=getBytePixel(x,y);
			direction=UP;
		}
		else
		{
			if(!inside(x-1,y-1))
				direction=RIGHT;
			else if(inside(x,y-1))
				direction=LEFT;
			else
				direction=DOWN;
		}
			
		traceEdge(x,y,direction);
	}
	/**************************************************************************************
	oid traceEdge(int xstart,int ystart,int startingDirection)��  ���ұ�Ե
	***************************************************************************************/
	void traceEdge(int xstart,int ystart,int startingDirection)
	{
		int[] table=
		{
			NA,				//0000
			RIGHT,			//000X
			DOWN,			//00X0
			RIGHT,			//00XX
			UP,				//0X00
			UP,				//0X0X
			UP_OR_DOWN,		//0XX0
			UP,				//0XXX
			LEFT,			//X000
       		 LEFT_OR_RIGHT,	//X00X
			DOWN,			//X0X0
			RIGHT,			//X0XX
			LEFT,			//XX00
			LEFT,			//XX0X
			DOWN,			//XXX0
			NA,				//XXXX
		};
		int index;
		int newDirection;
		int x=xstart;
		int y=ystart;
		int direction=startingDirection;
		int count=0;
		boolean UL=inside(x-1,y-1);
		boolean UR=inside(x,y-1);
		boolean LL=inside(x-1,y);
		boolean LR=inside(x,y);
		do
		{
			index=0;
			if(LR) index|=1;
			if(LL) index|=2;   
			if(UR) index|=4;    
			if(UL) index|=8;    
			newDirection=table[index];
			if(newDirection==UP_OR_DOWN)
			{
				if(direction==RIGHT)
					newDirection=UP;
				else
					newDirection=DOWN;
			}
			if(newDirection==LEFT_OR_RIGHT)
			{
		   		if(direction==UP)
					newDirection=LEFT;
				else
					newDirection=RIGHT;
			}
			if(newDirection!=direction)
			{
			xpoints[count]=x;
			ypoints[count++]=y;
			}
			
			if(count==xpoints.length)//���߽������������ʼֵ������һ��
			{
				int[] xtemp=new int[maxPoints*2];
				int[] ytemp=new int[maxPoints*2];
				System.arraycopy(xpoints,0,xtemp,0,maxPoints);
				System.arraycopy(ypoints,0,ytemp,0,maxPoints);
				//(ԭ����������ʼλ�ã�Ŀ������������ʼλ�ã�������С)
				xpoints=xtemp;
				ypoints=ytemp;
				maxPoints*=2;
			}
			
			switch(newDirection)
			{
				case UP:
					y=y-1;
					LL=UL;
					LR=UR;
					UL=inside(x-1,y-1);
					UR=inside(x,y-1);
					break;
				case DOWN:
					y=y+1;
					UL=LL;
					UR=LR;
					LL=inside(x-1,y);
					LR=inside(x,y);
					break;
				case LEFT:
					x=x-1;
					UR=UL;
					LR=LL;
					UL=inside(x-1,y-1);
					LL=inside(x-1,y);
					break;
				case RIGHT:
					x=x+1;
					UL=UR;
					LL=LR;
					UR=inside(x,y-1);
					LR=inside(x,y);
					break;
			}
			direction=newDirection;
		}
		while((x!=xstart||y!=ystart));//||direction!=startingDirection));
		nnpoints[n]=count;  //�������n�ı߽����
		npoints=count; 
		polygon[n]=new Polygon(xpoints, ypoints, npoints); //����n�����ı߽�����
		System.arraycopy(xpoints,0,xxpoints[n],0,npoints);
		System.arraycopy(ypoints,0,yypoints[n],0,npoints);
		//(ԭ����������ʼλ�ã�Ŀ������������ʼλ�ã�������С)
		if(area(n)>=ChangeMyFrame.minGrainSize)//�Ƿ�Ϊ�������ж��������������minGrainSize(10)
		{
			n++;
			if(n==polygon.length)  //�����������������ֵ400�������Ϊ800
			{
				int[][] xtemp=new int[maxPoints0*2][maxPoints];
				int[][] ytemp=new int[maxPoints0*2][maxPoints];
				Polygon[] ttemp=new Polygon[maxPoints0*2];
				int[] areatemp=new int[maxPoints0*2];
				System.arraycopy(xxpoints,0,xtemp,0,maxPoints0);
				System.arraycopy(yypoints,0,ytemp,0,maxPoints0);
				System.arraycopy(polygon,0,ttemp,0,maxPoints0);
				System.arraycopy(area,0,areatemp,0,maxPoints0);
				xxpoints=xtemp;
				yypoints=ytemp;
				polygon=ttemp;
				area=areatemp;
				maxPoints0*=2;
			}
		}
	}
	/***************************************************************************
	public int area(int n) ��             ����ÿ��������
	****************************************************************************/
	public int area(int n)
	{
		area[n]=0;
		for(int i=0;i<width-1;i++)
			for(int j=0;j<height;j++)
			{
				if(polygon[n].contains(i,j))
					area[n]++;
			}
			return area[n];
	}
	/*********************************************************************
	public boolean onEdge(int x, int y) �����жϸõ��Ƿ����ڱ߽��ϻ������
	**********************************************************************/
	public boolean onEdge(int x, int y)
	{
		for(int nn=0;nn<n;nn++)
		{
			for(int i=0;i<polygon[nn].npoints;i++)
			{
				if(x==xxpoints[nn][i] && y==yypoints[nn][i])
					return true;
			}
			
			if(polygon[nn].contains(x,y))
				return true;
		}
		return false;
	}
	/****************************************************************************
	void getRestEdge()  ��             �������߽�
	*****************************************************************************/
	void getRestEdge()
	{
		int m_b;
		outer2: for(int y=height*(100-ChangeMyFrame.processSize)/200;
					y<height*(100+ChangeMyFrame.processSiz e)/200;y++)
				{
					for(int x=width*(100-ChangeMyFrame.processSize)/200;
						x<width*(100+ChangeMyFrame.processSize)/200; x++)
					{
						yes=onEdge(x,y);
						m_b=cpixels[y*width+x];
						if(m_b==0xFF000000) //Ϊ��ɫ
						{
							Xstart=x; 
							Ystart=y;
							if(!yes)  //�Ҳ������ҵ��Ŀ����ϻ���
							{
                    			autoOutline(Xstart,Ystart);
							}
						}
					}
					
					ChangeMyFrame.myProgressBar.setValue(30+y*70/height);
					ChangeMyFrame.myProgressBar.setString("���ڴ����У����Ժ"
					+String.valueOf(30+y*70/height)+" %");
				}
	}
		/************************************************************************
		private void  centerOfGrain() ��      ��ÿ��������ĵ�
		*************************************************************************/
	private void  centerOfGrain()
	{
		int xmax,ymax,xmin,ymin;
		for(int j=0;j<n;j++)
		{
			xmax=polygon[j].xpoints[0];
			ymax=polygon[j].ypoints[0];
			xmin=polygon[j].xpoints[0];
			ymin=polygon[j].ypoints[0];
			
			for(int i=0;i<polygon[j].npoints;i++)
			{
				if(polygon[j].xpoints[i]>xmax)
					xmax=polygon[j].xpoints[i];
				if(polygon[j].ypoints[i]>ymax)
					ymax=polygon[j].ypoints[i];
				if(polygon[j].xpoints[i]<xmin)
					xmin=polygon[j].xpoints[i];
				if(polygon[j].ypoints[i]<ymin)
					ymin=polygon[j].ypoints[i];
			}
			X[j]=(xmax+xmin)/2;
			Y[j]=(ymax+ymin)/2;
		}
	}
}