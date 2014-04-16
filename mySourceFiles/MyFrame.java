package mySourceFiles;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class MyFrame extends JFrame implements WindowListener, ActionListener,KeyListener,MouseListener
{
	static int imagecounter=0;
	Menus menu;
	//BMPDecoder bmp=new BMPDecoder();
	static ImageShowWindow activeImageWindow;
	
	Label label;
	JProgressBar progressBar;
	MyFrame myFrame;
	
	//ImageShowWindow[] imagewindow;
	
	MyFrame(){
		super("My First Application");
		myFrame=this;
		
		Container c=getContentPane();
		
		menu=new Menus(this);
		setMenuBar(menu.Mymenubar);
		//label=new Label("hello");
		//add(label);
		Image myIconImage;
		myIconImage=Toolkit.getDefaultToolkit().getImage("images/T5.gif");
	    setIconImage(myIconImage);
		//label.addKeyListener(this);
		addWindowListener(this);
		//addKeyListener(this);
		//addMouseListener(this);
		//c.setLayout(new GridLayout(2, 1));
		JToolBar toolBar=new JToolBar();
		addButtons(toolBar);
		
		//Label toolbar=new Label();
		//toolBar.setBackground(Color.lightGray);
		
	
		//c.add(toolBar, BorderLayout.NORTH);
		
		Panel statusBar = new Panel();
		statusBar.setLayout(new BorderLayout());
		statusBar.add(toolBar,BorderLayout.NORTH);
		
		Panel myBar=new Panel();
		myBar.setLayout(new BorderLayout());
		//statusBar.setForeground(Color.black);
		//statusBar.setBackground(Color.lightGray);
		Label statusLine = new Label();
		//statusLine.addKeyListener(this);
		//statusLine.addMouseListener(this);
		myBar.add("Center", statusLine);
		
		progressBar = new JProgressBar();
		progressBar.setBackground(Color.lightGray);
		progressBar.setBorderPainted(false);
		progressBar.setOpaque(true);
		//progressBar.setValue(50);
		//progressBar.setPreferredSize(new Dimension(70,15));
		//progressBar.addKeyListener(this);
		//progressBar.addMouseListener(this);
		myBar.add("East", progressBar);
		//statusBar.setSize(toolbar.getPreferredSize());
		//statusBar.add(myBar,BorderLayout.CENTER);
		//statusBar.setVisible(false);
		setContentPane(statusBar);
		
	}
	
	protected void addButtons(JToolBar toolBar){
		JButton button=null;
		
		
		button=new JButton(new ImageIcon("images/T5.gif"));
		button.setToolTipText("ª“∂»¥¶¿Ì");
		
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MenuItemAction menuItemAction=new MenuItemAction(myFrame,"gray");
			}
			});
		toolBar.add(button);
	}
	
	public void keyPressed(KeyEvent e){}
		public void keyReleased(KeyEvent e){}	public void keyTyped(KeyEvent e){}
	
	
	public void mouseClicked(MouseEvent e){
	}
	public void mouseEntered(MouseEvent e){
	}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){
	}
	public void mouseReleased(MouseEvent e){}
	
	
	public void actionPerformed(ActionEvent e){
		MenuItem m_event=(MenuItem)(e.getSource());
		String m_stre=m_event.getActionCommand();
		
		MenuItemAction menuItemAction=new MenuItemAction(this,m_stre);
	
	}
	
	public void windowActivated( WindowEvent e ){}
	public void windowClosed( WindowEvent e ){}
	public void windowClosing( WindowEvent e ) {System.exit(0);}
	public void windowDeactivated( WindowEvent e ){}
	public void windowDeiconified( WindowEvent e ) {}
	
	public void windowIconified( WindowEvent e ){}
	public void windowOpened( WindowEvent e ){}
	
	public static void main(String[] arg){
		MyFrame myFrame=new MyFrame();
		
		//myFrame.setBackground(Color.gray);
		
		myFrame.pack();
		//myFrame.setSize (300,200);
		myFrame.setVisible(true);
	
	}
	
}

