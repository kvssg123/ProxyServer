

import java.lang.Math; 
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;



class searcher extends JFrame implements ActionListener{
    JLabel heading;
    Socket s;
    
    BufferedWriter pw;
    BufferedReader br;
    
    JPanel panel;
    JTextField text;
    JButton search;

    boolean f1;
    searcher()
	    {
	    		this.setLayout(null);
	    		this.setTitle("site launcher");
	    		this.setBounds(700,300,600,200);
	    		this.setResizable(false);
	    		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			
			       heading=new JLabel("ENTER A SITE");
			       heading.setBounds(0,0,600,30);
			       this.add(heading);
			       heading.setFont(new Font(null,Font.BOLD,25));
			       heading.setHorizontalAlignment(JLabel.CENTER);
			       heading.setBackground(new Color((int) ((Math.random())%256),(int) ((Math.random())%256),(int) ((Math.random())%256)));
			       heading.setOpaque(true);
			
			       panel=new JPanel();
			       panel.setLayout(null);
			       panel.setBackground(new Color((int) ((Math.random())%256),(int) ((Math.random())%256),(int) ((Math.random())%256)));
			       this.add(panel);
			       panel.setBounds(0,30,600,420);
			
			       text=new JTextField();
			       panel.add(text);
			       text.setBounds(150,30,300,30);
			       text.setFont(new Font(null,Font.PLAIN,20));
			
			       search=new JButton("search");
			       panel.add(search);
			       search.setBounds(235,75,150,30);
			       search.setFont(new Font(null,Font.BOLD,20));
			       search.setBackground(new Color((int) ((Math.random())%256),(int) ((Math.random())%256),(int) ((Math.random())%256)));
			       search.setFocusable(false);
			       search.addActionListener(this);
			       
			       
			       try {
			    	   s = new Socket("localhost",9989);
			    	   br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			    	   pw=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			       
			       this.setVisible(true);
	    }
    
   void sendrequest(String s1)
   {
		try {
//			pw.write("GET https://www.codeforces.com \n");
			pw.write("GET "+s1+"\n");
		
//			pw.write(	"CONNECT https://www.apple.com/in/ \n");
			


			pw.flush();
//			System.out.println("hi ");
			String conf=br.readLine();
			System.out.println(conf);
			if(conf.equals("HTTP/1.0 200 OK"))
			{
				
				String doc=br.readLine();
				File f=new File("C:\\Users\\itsmy\\Desktop\\index.html");
				BufferedWriter writer = new BufferedWriter ( new FileWriter ("C:\\Users\\itsmy\\Desktop\\index.html" ) );
				writer.write(doc);
				
				
				Runtime rTime = Runtime.getRuntime();
				String url = "C:\\Users\\itsmy\\Desktop\\index.html";
				String browser = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe ";
				@SuppressWarnings("deprecation")
				Process pc = rTime.exec(browser + url);
				pc.waitFor();
				System.out.println("hiend");
				//pw.write("CONNECT https://www.youtube.com \n");
			//	pw.write("CONNECT https://www.netflix.com:7778  HTTP/1.1 \n");

			}
			else
			{
				
			}
				
			
			
				
//				t--;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==search)
		{
			String s = text.getText();
			sendrequest(s);
			text.setText("");
		}
	}
}

public class proxyclient {
	public static void main(String [] args) throws Exception
	{

		
		new searcher();
		

	}

}
