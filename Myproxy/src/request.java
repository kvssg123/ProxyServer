import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class request implements Runnable{
	Socket clientSocket;
	BufferedReader pcbr;
	BufferedWriter pcbw;
	
	request(Socket s)
	{
		this.clientSocket = s;
		try {
			pcbr=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			pcbw=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String clientRequest = new String();
		try {
			clientRequest= pcbr.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		System.out.println("Request Received "+clientRequest);
		String reqtype=clientRequest.substring(0,clientRequest.indexOf(' ')); // GET
		String url=clientRequest.substring(clientRequest.indexOf(' ')+1);
		if(url.contains(" "))
		{
			url=url.substring(0,url.indexOf(' '));
		}
		if(!url.substring(0,4).equals("http")){
			String temp = "https://";
			url = temp + url;
		}
		
		
		if(ProxyServer.isBlocked(url)){
			System.out.println("Blocked site requested : " + url);
			blockedSiteRequested();
			return;
		}
		
		File f;
		if((f=ProxyServer.getCachedPage(url))!=null)
		{
			System.out.println("Received Request cached"+url);
			sendCachedFile(f);
		}
		else {
			System.out.println("Recieved Request non cached"+url);
			sendNonCachedFile(url);
			
		}
		
		
		
		
	}




	private void sendNonCachedFile(String url)  {
		try {
		// TODO Auto-generated method stub
		int fileExtensionIndex = url.lastIndexOf(".");
		// Get the type of file
		String fileExtension = url.substring(fileExtensionIndex, url.length());
		System.out.println(fileExtension);
		// Get the initial file name
		String fileName = url.substring(0,fileExtensionIndex);
		System.out.println(fileName);
		fileName = fileName.substring(fileName.indexOf('.')+1);
		
		fileName=fileName.replace('.','_');
		fileName=fileName.replace('/','_');
		
		fileExtension=fileExtension.replace('.','_');
		fileExtension=fileExtension.replace('/','_');
		
		fileName+=fileExtension+".html";
		System.out.println(fileName);
		
		File fileToCache=null;
		fileToCache = new File("C:\\Users\\itsmy\\eclipse-workspace\\Proxygit\\cached\\" + fileName);
		boolean caching=true;
		BufferedWriter bWriter=null;	
		try {
			if(!fileToCache.exists())
			{
				fileToCache.createNewFile();
			}
			bWriter=new BufferedWriter(new FileWriter(fileToCache));
		} catch (Exception e) {
			caching=false;
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		
		
			URL remoteURL;
			remoteURL = new URL(url);
			HttpURLConnection proxyToServerCon = (HttpURLConnection)remoteURL.openConnection();
			proxyToServerCon.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");
			proxyToServerCon.setRequestProperty("Content-Language", "en-US");  
			proxyToServerCon.setUseCaches(false);
			proxyToServerCon.setDoOutput(true);
			BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));
			String line = "HTTP/1.0 200 OK\n";
			pcbw.write(line);
			System.out.println("hi1");
			while((line=proxyToServerBR.readLine())!=null)
			{
				pcbw.write(line);
				
				if(caching){
					bWriter.write(line);
				}
			}
			pcbw.flush();
			if(proxyToServerBR != null){
				proxyToServerBR.close();
			}
			if(caching){
				// Ensure data written and add to our cached hash maps
				bWriter.flush();
				ProxyServer.addCachedPage(url, fileToCache);
			}

			// Close down resources
			if(bWriter != null){
				bWriter.close();
			}

			if(pcbw != null){
				pcbw.close();
			}


		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
	}




	public void sendCachedFile(File f) {
		// TODO Auto-generated method stub
		try {
			String fileExtension = f.getName().substring(f.getName().lastIndexOf('.'));
			String response;
			BufferedReader cachedFileBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			response = "HTTP/1.0 200 OK\n";
			pcbw.write(response);
			pcbw.flush();
			String line;
			while((line=cachedFileBufferedReader.readLine())!=null)
			{
				pcbw.write(line);
			}
			pcbw.flush();
			if(cachedFileBufferedReader!=null)
				cachedFileBufferedReader.close();
			if(pcbw!=null)
				pcbw.close();
			
		} 
		
		
		
		catch (Exception e) {
			// TODO: handle exception
			
			System.out.println(e);
		}
	}




	private void blockedSiteRequested() {
		// TODO Auto-generated method stub
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			String line="HTTP/1.0 403 Access Forbidden \n";
			bufferedWriter.write(line);
			bufferedWriter.flush();
		} catch (Exception e) {
			System.out.println("Error writing to client when requested a blocked site");
			// TODO: handle exception
		}
		
		
	}
}
