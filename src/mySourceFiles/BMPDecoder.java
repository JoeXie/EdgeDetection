package mySourceFiles;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** A decoder for Windows bitmap (.BMP) files. */
public class BMPDecoder {
    InputStream is;
    int curPos = 0;       // current position
            
    int bitmapOffset;               // starting position of image data

    int width;                              // image width in pixels
    int height;                             // image height in pixels
    short bitsPerPixel;             // 1, 4, 8, or 24 (no color map)
    int compression;                // 0 (none), 1 (8-bit RLE), or 2 (4-bit RLE)
    int actualSizeOfBitmap;
    int scanLineSize;
    int actualColorsUsed;

    // Actual contents (40 bytes):
    int size;                               // size of this header in bytes
    short planes;                   // no. of color planes: always 1
    int sizeOfBitmap;               // size of bitmap in bytes (may be 0: if so, calculate)
    int horzResolution;             // horizontal resolution, pixels/meter (may be 0)
    int vertResolution;             // vertical resolution, pixels/meter (may be 0)
    int colorsUsed;                 // no. of colors in palette (if 0, calculate)
    int colorsImportant;  // no. of important colors (appear first in palette) (0 means all are important)
    boolean topDown;
    int noOfPixels;        //像素数量

    
    byte r[], g[], b[];             // color palette
    int noOfEntries;

    byte[] byteData;                // Unpacked data
    int[] intData;                  // Unpacked data


    //返回int型b4 b3 b2 b1
    public int readInt() throws IOException {
            int b1 = is.read();
            int b2 = is.read();
            int b3 = is.read();
            int b4 = is.read();
            curPos += 4;
            return ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
    }


    //返回short型b2 b1
    public short readShort() throws IOException {
            int b1 = is.read();
            int b2 = is.read();
            curPos += 2;
            return (short)((b2 << 8) + b1);
    }


    //获取文件头信息，判断是不是BMP图片
    void getFileHeader()  throws IOException, Exception {
            // Actual contents (14 bytes):
            short fileType = 0x004d42;// always "BM"
            int fileSize;                   // size of file in bytes
            short reserved1 = 0;    // always 0
            short reserved2 = 0;    // always 0

            fileType = readShort();
            if (fileType != 0x004d42)
                    throw new Exception("Not a BMP file");  // wrong file type
            fileSize = readInt();
            reserved1 = readShort();
            reserved2 = readShort();
            bitmapOffset = readInt();
    }

    
    //分析文件头，获取图片信息
    void getBitmapHeader() throws IOException {
    
           
            //根据文件头信息分布依次读取
            size = readInt();
            width = readInt();
            height = readInt();
            planes = readShort();
            bitsPerPixel = readShort();
            compression = readInt();
            sizeOfBitmap = readInt();
            horzResolution = readInt();
            vertResolution = readInt();
            colorsUsed = readInt();
            colorsImportant = readInt();

            topDown = (height < 0);
            noOfPixels = width * height;

            // Scan line is padded with zeroes to be a multiple of four bytes
            scanLineSize = ((width * bitsPerPixel + 31) / 32) * 4;

            //获得图片的大小
            if (sizeOfBitmap != 0)
                    actualSizeOfBitmap = sizeOfBitmap;
            else
                    // a value of 0 doesn't mean zero - it means we have to calculate it
                    actualSizeOfBitmap = scanLineSize * height;

            //判断是多少位的图片
            if (colorsUsed != 0)
                    actualColorsUsed = colorsUsed;
            else
                    // a value of 0 means we determine this based on the bits per pixel
                    if (bitsPerPixel < 16)
                            actualColorsUsed = 1 << bitsPerPixel;
                    else
                            actualColorsUsed = 0;   // no palette
    }


    //读取调色板数据存放到r[] b[] g[]中
    void getPalette() throws IOException {
            noOfEntries = actualColorsUsed;
            //IJ.write("noOfEntries: " + noOfEntries);
            if (noOfEntries > 0) {
                    r = new byte[noOfEntries];
                    g = new byte[noOfEntries];
                    b = new byte[noOfEntries];

                    int reserved;
                    for (int i = 0; i < noOfEntries; i++) {
                            b[i] = (byte)is.read();
                            g[i] = (byte)is.read();
                            r[i] = (byte)is.read();
                            reserved = is.read();
                            curPos += 4;
                    }
            }
    }

    //按一定格式复制rawData到intData
    void unpack(byte[] rawData, int rawOffset, int[] intData, int intOffset, int w) {
            int j = intOffset;
            int k = rawOffset;
            int mask = 0x00ff;
            for (int i = 0; i < w; i++) {
                    int b0 = (((int)(rawData[k++])) & mask);
                    int b1 = (((int)(rawData[k++])) & mask) << 8;
                    int b2 = (((int)(rawData[k++])) & mask) << 16;
                    intData[j] = 0x00ff000000 | b0 | b1 | b2;   //输出int型ff b2 b1 b0
                    j++;
            }
    }


    void unpack(byte[] rawData, int rawOffset, int bpp, byte[] byteData, int byteOffset, int w) throws Exception 
    {
            int j = byteOffset;
            int k = rawOffset;
            byte mask;
            int pixPerByte;

            switch (bpp) {
            case 1: mask = (byte)0x0001; pixPerByte = 8; break;
            case 4: mask = (byte)0x000f; pixPerByte = 2; break;
            case 8: mask = (byte)0x00ff; pixPerByte = 1; break;
            default:
                    throw new Exception("Unsupported bits-per-pixel value");
            }

            for (int i = 0; ; ) {
                    int shift = 8 - bpp;
                    for (int ii = 0; ii < pixPerByte; ii++) {
                            byte br = rawData[k];
                            br >>= shift;
                            byteData[j] = (byte)(br & mask);
                            //System.out.println("Setting byteData[" + j + "]=" + Test.byteToHex(byteData[j]));
                            j++;
                            i++;
                            if (i == w) return;
                            shift -= bpp;
                    }
                    k++;
            }
    }


    //读取像素值到intData[]或byteData[]
    void getPixelData() throws IOException, Exception {
            byte[] rawData;                 // the raw unpacked data

            // Skip to the start of the bitmap data (if we are not already there)
            long skip = bitmapOffset - curPos;
            if (skip > 0) {
                    is.skip(skip);  //读取位置跳过skip个字节
                    curPos += skip;
            }

            int len = scanLineSize;
            if (bitsPerPixel > 8)
                    intData = new int[width * height];
            else
                    byteData = new byte[width * height];
            rawData = new byte[actualSizeOfBitmap];
            int rawOffset = 0;
            int offset = (height - 1) * width;
            for (int i = height - 1; i >= 0; i--) {
                    int n = is.read(rawData, rawOffset, len);
                    if (n < len) throw new Exception("Scan line ended prematurely after "
                            + n + " bytes");
                    if (bitsPerPixel > 8) {
                            // Unpack and create one int per pixel
                            unpack(rawData, rawOffset, intData, offset, width);
                    }
                    else {
                            // Unpack and create one byte per pixel
                            unpack(rawData, rawOffset, bitsPerPixel, 
                                    byteData, offset, width);
                    }
                    rawOffset += len;
                    offset -= width;
            }
    }


    public void read(InputStream is) throws IOException, Exception {
            this.is = is;
            getFileHeader();
            getBitmapHeader();
            if (compression!=0)
                    throw new Exception("Compression not supported");
            getPalette();
            getPixelData();
    }


    public MemoryImageSource makeImageSource() {
            ColorModel cm;
            MemoryImageSource mis;

            if (noOfEntries > 0) {
                    // There is a color palette; create an IndexColorModel
                    cm = new IndexColorModel(bitsPerPixel, noOfEntries, r, g, b);
            } 
            else 
            {
                    // There is no palette; use the default RGB color model
                    cm = ColorModel.getRGBdefault();
            }

            // Create MemoryImageSource

            if (bitsPerPixel > 8) {
                    // use one int per pixel
                    mis = new MemoryImageSource(width, height, cm, intData, 0, width);
            } 
            else 
            {
                    // use one byte per pixel
                    mis = new MemoryImageSource(width, height, cm, byteData, 0, width);
            }

            return mis;      // this can be used by Component.createImage()
    }
    
    
    
    
    //保存图片到文件
    public void write(OutputStream out) throws IOException, Exception
    {
    	//int width = image.getWidth();
		//int height = image.getHeight();
		//int rowSize = (width * 3 + 3) / 4 * 4;  // 3 bytes per pixel in RGB888, round up to multiple of 4
		int imageSize = width * height;
		
		// BITMAPFILEHEADER
		writeBytes(new byte[]{'B', 'M'}, out);  // FileType
		writeInt32(14 + 40 + imageSize, out);   // FileSize
		writeInt16(0, out);                     // Reserved1
		writeInt16(0, out);                     // Reserved2
		writeInt32(14 + 40, out);               // BitmapOffset
		
		// BITMAPINFOHEADER
		writeInt32(40, out);                        // Size
		writeInt32(width, out);                     // Width
		writeInt32(height, out);                    // Height
		writeInt16(1, out);                         // Planes
		writeInt16(24, out);                        // BitsPerPixel
		writeInt32(0, out);                         // Compression
		writeInt32(imageSize, out);                 // SizeOfBitmap
		writeInt32(horzResolution, out);  // HorzResolution
		writeInt32(vertResolution, out);    // VertResolution
		writeInt32(0, out);                         // ColorsUsed
		writeInt32(0, out);                         // ColorsImportant
    
		for(int i = 0; i < imageSize; i++)
		{
			if(bitsPerPixel < 8)
			{
				out.write((int)byteData[i]);
			} else
			{
				writeInt32(intData[i], out);
			}
		}
    
}


	private void writeInt32(int x, OutputStream out) throws IOException
	{
		out.write( (x & 0xFF000000) >>> 24);
		out.write( (x & 0x00FF0000) >>> 16);
		out.write( (x & 0x0000FF00) >>> 8);
		out.write( x & 0x000000FF);
	}


	private void writeInt16(int x, OutputStream out) throws IOException
	{
		out.write( (x & 0x0000FF00) >>> 8);
		out.write( x & 0x000000FF);
	}


	private void writeBytes(byte[] x, OutputStream out) throws IOException
	{
		out.write(x);
	}

	
	
}
