package mySourceFiles;
import java.awt.Polygon;


public class GetGrainEdge
{
    static final int UP=0,DOWN=1,UP_OR_DOWN=2,LEFT=3,RIGHT=4,LEFT_OR_RIGHT=5,NA=6;
    
    private int maxPoints=1000; //初设颗粒边界点个数
    private int maxPoints0=400; //初设颗粒个数
    
    public int npoints;  //颗粒边界点个数
    public int[] nnpoints= new int[maxPoints];  //存储某个颗粒边界点个数数组
    
    public int[] xpoints= new int[maxPoints];//存储颗粒边界数组
    public int[] ypoints= new int[maxPoints];
    
    public int width, height, Xstart, Ystart;
    private int[] cpixels;  //像素值数组
    private int lowerThreshold, upperThreshold;
    public boolean yes; //在判断是否在已获得的颗粒时用到
    
    public int[][] xxpoints= new int[maxPoints0][maxPoints];//第maxPoints0个颗粒的边界点数组
    public int[][] yypoints= new int[maxPoints0][maxPoints];
    //每一行 xxpoints存储的像素点个数
    
    public int n=0; //n表示颗粒个数
    
    Polygon[] polygon= new Polygon[maxPoints0];//多边形（颗粒边界）数组
    
    int[] area= new int[maxPoints0];      //颗粒占的像素点个数
    
    public int[] X= new int[maxPoints0];  //数组X[j], Y[j]存放第j个颗粒中心坐标
    public int[] Y= new int[maxPoints0];
    
    
    
    
    public GetGrainEdge(MyFrame f, String ss, BMPDecoder bmp)//构造函数
    {
        BMPDecoder newbmp=bmp;
        int m_b;
        width=newbmp.width;
        height=newbmp.height;
        cpixels=new int[width*height];
                  //(原数组名，起始位置，目标数组名，起始位置，拷贝大小)
        System.arraycopy(newbmp.intData, 0, cpixels, 0, width*height);//将整副图像像素值复制到cpixels数组里
        
        //循环遍历图像的每一个点，ChangeMyFrame.processSize可忽略，它是用来选择图像中的某一块区域。
        //寻找初始点
        outer1: for(int y=newbmp.height*(100-ChangeMyFrame.processSize)/200; 
                    y<newbmp.height*(100+ChangeMyFrame.processSize)/200; y++)
                {
                    for(int x= newbmp.width*(100-ChangeMyFrame.processSize)/200;
                        x< newbmp.width*(100+ChangeMyFrame.processSize)/200; x++)
                    {
                        m_b= newbmp.intData[y*bmp.width+x];  //&0x00FFFFFF
                        /*0xFF000000，第一个FF是缺省字节，后面三个"00"分别是R,G,B，所以总共四个字节，是int类型。
                        二值图像中，黑色是R=G=B=00000000(8位，一个字节）；白色是R=G=B=FFFFFFFF。*/
                        if(m_b== 0xFF000000)  //确定黑色为初始点
                        {
                            Xstart= x;
                            Ystart= y;
                            break outer1;
                        }
                    }
                }
        
        autoOutline(Xstart,Ystart);
        getRestEdge();
        centerOfGrain();
    }
    
    /*************************************************************************************
    private int getBytePixel(int x,int y) ：  获得坐标点(x, y)的像素值
    **************************************************************************************/
    private int getBytePixel(int x,int y)
    {
        if(x>= 0 && x< width && y>= 0 && y< height)
        {
            return cpixels[y*width+x];
        }
        else
            return 0xFFFFFFFF;    //超出图像大小，定义为白色
    }
    /****************************************************************************************
    private boolean inside(int x, int y) ：   判断点（x, y）的像素值是否与初始点相同
    ****************************************************************************************/
    private boolean inside(int x, int y)
    {
        int value;
        value=getBytePixel(x,y);
        return value==lowerThreshold && value==upperThreshold; //是否与初始点相同
        //其中，lowerThreshold=upperThreshold=getBytePixel(startX,startY)
    }
    
    
    /*************************************************************************
    boolean isLine(int xs,int ys) ：       判断是否在颗粒内
    *************************************************************************/
    boolean isLine(int xs, int ys)//在矩形(x,y-5),(x+10,y-5),(x+10,y+5),(x,y+5)内进行运算
    {
        int r=5;
        int xmin=xs;
        int xmax=xs+2*r;
        int ymin=ys-r;
        int ymax=ys+r;
        
        if(xmax> width)
            xmax= width-1;    
        if(ymin< 0)
            ymin= 0;
        if(ymax>= height)
            ymax= height-1;
        
        int area=0;
        int insideCount=0;
        
        for(int x= xmin; x<= xmax; x++)
            for(int y= ymin; y<= ymax; y++)
            {
                area++;
                if(inside(x, y))
                    insideCount++;
            }
        
        return((double)insideCount)/area >= 0.75; //四分之三以上都在颗粒内，则同一行
    }
    
    
    /*startX,startY是在颗粒上的点，xpoints,ypoints用来
    存储边界点，数组table[]用来决定边界扫描方向*/
    public void autoOutline(int startX,int startY)
    {
        int x= startX;
        int y= startY;
        int direction;
        lowerThreshold= upperThreshold= getBytePixel(startX, startY); //赋初始点的值
        
        do
        {
            x++;
        }
        while(inside(x, y));
        
        if(isLine(x, y))
        {
            lowerThreshold= upperThreshold= getBytePixel(x, y);
            direction= UP;
        }
        else
        {
            if(!inside(x-1, y-1))
                direction= RIGHT;
            else
                if(inside(x, y-1))
                    direction= LEFT;
                else
                    direction=DOWN;
        }
        
        traceEdge(x,y,direction);
    }
    /**************************************************************************************
    void traceEdge(int xstart, int ystart, int startingDirection)：  查找边缘
    ***************************************************************************************/
    void traceEdge(int xstart, int ystart, int startingDirection)
    {
        int[] table=
        {
            NA,                //0000  0
            RIGHT,            //000X  1
            DOWN,            //00X0  2
            RIGHT,            //00XX  3
            UP,                //0X00  4
            UP,                //0X0X  5
            UP_OR_DOWN,        //0XX0  6
            UP,                //0XXX  7
            LEFT,            //X000  8
            LEFT_OR_RIGHT,    //X00X  9
            DOWN,            //X0X0  10
            RIGHT,            //X0XX  11
            LEFT,            //XX00  12
            LEFT,            //XX0X  13
            DOWN,            //XXX0  14
            NA,                //XXXX  15
        };
        int index;
        int newDirection;
        int x= xstart;
        int y= ystart;
        int direction= startingDirection;
        int count= 0;
        
        boolean UL= inside(x-1, y-1); //UpperLeft
        boolean UR= inside(x, y-1); //UpperRight
        boolean LL= inside(x-1, y); //LowerLeft
        boolean LR= inside(x, y); //LowerRight
        
        do
        {
            index= 0;
            if(LR) index|= 1; //index= 00000000 | 00000001
            if(LL) index|= 2; //index= 00000000 | 00000010
            if(UR) index|= 4; //index= 00000000 | 00000100
            if(UL) index|= 8; //index= 00000000 | 00001000
            newDirection= table[index]; //根据4邻域像素确定方向
            
            //判断对角分布的情况下的方向
            if(newDirection == UP_OR_DOWN)
            {
                if(direction == RIGHT)
                    newDirection= UP;
                else
                    newDirection= DOWN;
            }
            if(newDirection == LEFT_OR_RIGHT)
            {
                   if(direction == UP)
                    newDirection= LEFT;
                else
                    newDirection= RIGHT;
            }
            if(newDirection != direction)
            {
            xpoints[count]= x;
            ypoints[count++]= y;
            }
            
            if(count == xpoints.length)//当边界颗粒数超出初始值，扩大一倍
            {
                int[] xtemp= new int[maxPoints*2];
                int[] ytemp= new int[maxPoints*2];
                System.arraycopy(xpoints,0,xtemp,0,maxPoints);
                System.arraycopy(ypoints,0,ytemp,0,maxPoints);
                //(原数组名，起始位置，目标数组名，起始位置，拷贝大小)
                xpoints= xtemp;
                ypoints= ytemp;
                maxPoints*= 2;
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
        while((x!= xstart || y!= ystart)); // || direction!= startingDirection));
        
        nnpoints[n]= count; //保存颗粒n的边界点数
        npoints= count;
        polygon[n]= new Polygon(xpoints, ypoints, npoints); //保存第n颗粒的边界多边形
        System.arraycopy(xpoints, 0, xxpoints[n], 0, npoints);
        System.arraycopy(ypoints, 0, yypoints[n], 0, npoints);
        //(原数组名，起始位置，目标数组名，起始位置，拷贝大小)
        
        if(area(n) >= ChangeMyFrame.minGrainSize)//是否为颗粒的判断条件：面积大于minGrainSize(10)
        {
            n++;
            if(n == polygon.length)  //如果颗粒数超过初设值400，则该设为800
            {
                int[][] xtemp=new int[maxPoints0*2][maxPoints];
                int[][] ytemp=new int[maxPoints0*2][maxPoints];
                Polygon[] ttemp=new Polygon[maxPoints0*2];
                int[] areatemp=new int[maxPoints0*2];
                System.arraycopy(xxpoints, 0, xtemp, 0, maxPoints0);
                System.arraycopy(yypoints, 0, ytemp, 0, maxPoints0);
                System.arraycopy(polygon, 0, ttemp, 0, maxPoints0);
                System.arraycopy(area, 0, areatemp, 0, maxPoints0);
                xxpoints= xtemp;
                yypoints= ytemp;
                polygon= ttemp;
                area= areatemp;
                maxPoints0*= 2;
            }
        }
    }
    
    
    /***************************************************************************
    public int area(int n) ：             计算该颗粒的面积（统计包含多少个像素）
    ****************************************************************************/
    public int area(int n) //参数n表示第n个点
    {
        area[n]= 0;
        for(int i= 0; i< width-1; i++)
            for(int j= 0; j< height; j++)
            {
                if(polygon[n].contains(i, j))
                    area[n]++;
            }
            return area[n];
    }
    
    
    /*********************************************************************
    public boolean onEdge(int x, int y) ：判断该点（x，y）是否已在边界上或颗粒内
    **********************************************************************/
    public boolean onEdge(int x, int y)
    {
        for(int nn= 0; nn< n; nn++) //判断是否在边界上
        {
            for(int i=0; i< polygon[nn].npoints; i++)
            {
                if(x==xxpoints[nn][i] && y==yypoints[nn][i])
                    return true;
            }
            
            if(polygon[nn].contains(x,y)) //判断是否在顶点上？？？？？？？此处是否存在重复操作？？？？？？？？
                return true;
        }
        return false;
    }
    
    
    /****************************************************************************
    void getRestEdge()  ：             获得其余边界
    *****************************************************************************/
    void getRestEdge()
    {
        int m_b;
        outer2: for(int y= height*(100-ChangeMyFrame.processSize)/200;
                    y< height*(100+ChangeMyFrame.processSiz e)/200; y++)
                {
                    for(int x= width*(100-ChangeMyFrame.processSize)/200;
                        x< width*(100+ChangeMyFrame.processSize)/200; x++)
                    {
                        yes= onEdge(x, y);
                        m_b= cpixels[y*width+x];
                        if(m_b == 0xFF000000) //为黑色
                        {
                            Xstart= x; 
                            Ystart= y;
                            if(!yes)  //且不在已找到的颗粒上或内
                            {
                                autoOutline(Xstart, Ystart);
                            }
                        }
                    }
                    
                    ChangeMyFrame.myProgressBar.setValue(30+y*70/height);
                    ChangeMyFrame.myProgressBar.setString("正在处理中，请稍侯！"+
                                                            String.valueOf(30+y*70/height)+" %");
                }
    }
    
    
    /************************************************************************
    private void  centerOfGrain() ：      获得颗粒的中心点
    *************************************************************************/
    private void  centerOfGrain()
    {
        int xmax, ymax, xmin, ymin;
        for(int j= 0; j< n; j++)
        {
            xmax= polygon[j].xpoints[0];
            ymax= polygon[j].ypoints[0];
            xmin= polygon[j].xpoints[0];
            ymin= polygon[j].ypoints[0];
            
            for(int i= 0; i< polygon[j].npoints; i++)
            {
                if(polygon[j].xpoints[i]> xmax)
                    xmax= polygon[j].xpoints[i];
                if(polygon[j].ypoints[i]> ymax)
                    ymax= polygon[j].ypoints[i];
                if(polygon[j].xpoints[i]< xmin)
                    xmin= polygon[j].xpoints[i];
                if(polygon[j].ypoints[i]< ymin)
                    ymin= polygon[j].ypoints[i];
            }
            
            X[j]=(xmax+xmin)/2; //X[j], Y[j]存放第j个点的中心坐标
            Y[j]=(ymax+ymin)/2;
        }
    }
}