package mySourceFiles;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageSaveTest
{

	public static void main(String[] args) throws Exception
	{
		InputStream in = new FileInputStream("Demo.bmp");
		System.out.println("1");
		BMPDecoder bmp = new BMPDecoder();
		System.out.println("2");
		bmp.read(in);
		System.out.println("3");
		in.close();
		
		OutputStream out = new FileOutputStream("Demo2.bmp");
		System.out.println("4");
		bmp.write(out);
		System.out.println("5");
		out.close();
		
	}

}
