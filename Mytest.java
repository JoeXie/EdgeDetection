import java.awt.*;
import java.awt.event.*;

public class Mytest //extends WindowAdapter 
{
	public static void main(String[] arg)
	{	
		MyFrame myFrame=new MyFrame();
		myFrame.setVisible(true);
		myFrame.setBackground(Color.gray);
		myFrame.setSize (300,200);
		
		//myFrame.addWindowListener(new NameL());
	
/*	MyFrame.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
			System.exit(0);
		}
		});*/
	
	}
	
	
}

/*class NameL extends WindowAdapter{
		public void windowClosing( WindowEvent e ) { System.exit(0); }
	
	}*/
