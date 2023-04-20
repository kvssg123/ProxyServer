import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ProxyServer extends JFrame implements ActionListener{
	ServerSocket ss;
	
	static HashMap<String,File>cached;
	static HashMap<String,String>blockedSites;
	static ArrayList<Thread>runningThreads;
	static boolean running =true;
	
	@SuppressWarnings("unchecked")
	ProxyServer(int port)
	{
		cached = new HashMap<>();
		blockedSites = new HashMap<>();
		runningThreads=new ArrayList<>();
		GUI();
		this.setLayout(null);
		this.setTitle("PROXY CONTROL");
		this.setBounds(0,00,600,700);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		   heading = new JLabel("PROXY CONTROL");
	       heading.setBounds(0,0,600,30);
	       this.add(heading);
	       heading.setFont(new Font(null,Font.BOLD,25));
	       heading.setHorizontalAlignment(JLabel.CENTER);
	       heading.setBackground(new Color(38,234,123));
	       heading.setOpaque(true);
	       
	       panel=new JPanel();
	       panel.setLayout(null);
	       panel.setBackground(new Color(92,120,120));
	       this.add(panel);
	       panel.setBounds(0,30,600,670);
	       
	       blocked=new JLabel("Enter site to block");
	       panel.add(blocked);
	       blocked.setBounds(200,10,400,30);
	       
	       text=new JTextField();
	       panel.add(text);
	       text.setBounds(50,50,300,30);
	       text.setFont(new Font(null,Font.PLAIN,20));
	       
	       block = new JButton("block");
	       panel.add(block);
	       block.addActionListener(this);
	       block.setBounds(400,55,70,20);
	       
	       ViewBlocked = new JButton("ViewBlocked");
	       panel.add(ViewBlocked);
	       ViewBlocked.addActionListener(this);
	       ViewBlocked.setBounds(20,100,150,20);
	       
	       ViewCached = new JButton("ViewCached");
	       panel.add(ViewCached);
	       ViewCached.addActionListener(this);
	       ViewCached.setBounds(230,100,150,20);
	       
	       Close = new JButton("Close");
	       panel.add(Close);
	       Close.addActionListener(this);
	       Close.setBounds(430,100,150,20);
	       
	       Area=new JTextArea();
	       panel.add(Area);
	       Area.setBounds(100,150,300,150);
	       
	       this.setVisible(true);
	       
	       
	       
		
		try {
			File cachedSites = new File("cachedSites.txt");
			if(!cachedSites.exists()) cachedSites.createNewFile();
			else {
				FileInputStream fInputStream= new FileInputStream(cachedSites);
				ObjectInputStream objectInputStream=new ObjectInputStream(fInputStream);
				cached=(HashMap<String,File>) objectInputStream.readObject();
				System.out.println(cached);
				objectInputStream.close();
				fInputStream.close();
				
			}
			File blockedSitesfile = new File("blockedSites.txt");
			if(!blockedSitesfile.exists()) blockedSitesfile.createNewFile();
			else {
				FileInputStream fInputStream= new FileInputStream(blockedSitesfile);
				ObjectInputStream objectInputStream=new ObjectInputStream(fInputStream);
				cached=(HashMap<String,File>) objectInputStream.readObject();
				objectInputStream.close();
				fInputStream.close();
				
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ss=new ServerSocket(port);
			running=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		
	}
	public void listen() {
		// TODO Auto-generated method stub
		while(running)
		{
			try {
				Socket s=ss.accept();
				Thread thread=new Thread(new request(s));
				runningThreads.add(thread);
				thread.start();
				
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Server Crashed");
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	public void closeServer()
	{
		try {
			FileOutputStream fos = new FileOutputStream("cachedSites.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cached);
			oos.close();
			fos.close();
			System.out.println("Cached Sites written");

			FileOutputStream fileOutputStream2 = new FileOutputStream("blockedSites.txt");
			ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(fileOutputStream2);
			objectOutputStream2.writeObject(blockedSites);
			objectOutputStream2.close();
			fileOutputStream2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		for(Thread thread:runningThreads)
		{
			if(thread.isAlive()) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			ss.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
		
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==block)
		{
			String url = text.getText();
			if(url.contains(" "))
			{
				url=url.substring(0,url.indexOf(' '));
			}
			if(!url.substring(0,4).equals("http")){
				String temp = "https://";
				url = temp + url;
			}
			blockedSites.put(url,url);
			Area.setText("Successully blocked "+url);
		}
		if(e.getSource()==ViewBlocked)
		{
			String s1="";
			for(String key:blockedSites.keySet())
			{
				s1+=key;
				s1+="\n";
			}
			Area.setText(s1);
		}
		if(e.getSource()==ViewCached)
		{
			String s1="";
			for(String key:cached.keySet())
			{
				s1+=key;
				s1+="\n";
			}
			Area.setText(s1);
		}
		if(e.getSource()==Close)
		{
			running=false;
			closeServer();
		}
	}
	void GUI()
	{
		
	       
	}
	JLabel heading,blocked;
	JPanel panel;
	JTextField text;
	JButton block,ViewBlocked,ViewCached,Close;
	JTextArea Area;
	
	public static File getCachedPage(String url){
		return cached.get(url);
	}
	
	public static void addCachedPage(String urlString, File fileToCache){
		cached.put(urlString, fileToCache);
	}
	
	public static boolean isBlocked (String url){
		System.out.println(url);
		if(blockedSites.get(url) != null){
			return true;
		} else {
			return false;
		}
	}

	
	
	
	public static void main(String args[])
	{
		ProxyServer myProxy = new ProxyServer(9989);
		myProxy.listen();
	}
	
}
