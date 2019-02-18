import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class http_server extends Thread
{
	private int port;
	private FileInputStream requestedfile = null;
	private oss parent;
	private ServerSocket serversocket = null;
  public http_server(int listen_port, oss parent1)
  {
  	parent = parent1;
    port = listen_port;
    this.start();
  }

  public void run()
  {
  	try
  	{
    	serversocket = new ServerSocket(port);
    	JOptionPane.showMessageDialog(null,"the server is starting");
    }
    catch(Exception e)
    {
    	JOptionPane.showMessageDialog(null,"cant create server socket\n"+e.toString(),"server error",JOptionPane.ERROR);
    }
    
    while (true)
    {
      try
      {
        Socket connectionsocket = serversocket.accept();
        InetAddress client = connectionsocket.getInetAddress();
        BufferedReader input = new BufferedReader(new InputStreamReader(connectionsocket.getInputStream()));
        DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());
        http_handler(input, output);
      }
      catch (Exception e)
      {
      	JOptionPane.showMessageDialog(null,e.toString(),"Error connection",JOptionPane.ERROR);
      }

    }
  }

  private void http_handler(BufferedReader input, DataOutputStream output)
  {
    int method = 0;             //1 get, 2 head, 0 not supported
    String http = new String(); //a bunch of strings to hold
    String path = new String(); //the various things, what http v, what path,
    String file = new String(); //what file
    String tmp,tmp2 = "";
      //This is the two types of request we can handle
      //GET /index.html HTTP/1.0
      //HEAD /index.html HTTP/1.0
      try
      {
      	tmp = input.readLine(); //read from the stream
      	tmp2 = new String(tmp);
      	tmp.toUpperCase();
      	if (tmp.startsWith("GET"))  //compare it is it GET
       	 method = 1;
     	 if (tmp.startsWith("HEAD")) //same here is it HEAD
        	method = 2;
      }
      catch(Exception e)
      {
      	JOptionPane.showMessageDialog(null,"cant read http constructer","Reading error",JOptionPane.ERROR);
      }
      if (method == 0)			  // not supported
      {
      	try
      	{
        	output.writeBytes(construct_http_header(501, 0));
        	output.close();
        }
        catch(Exception e){}
      }

      //tmp contains "GET /index.html HTTP/1.0 ......."
      //find first space
      //find next space
      //copy whats between minus slash, then you get "index.html"
      //it's a bit of dirty code, but bear with me...
      int start = 0;
      int end = 0;
      for (int a = 0; a < tmp2.length(); a++)
      {
        if (tmp2.charAt(a) == ' ' && start != 0) {
          end = a;
          break;
        }
        if (tmp2.charAt(a) == ' ' && start == 0) {
          start = a;
        }
      }


    try
    {
      requestedfile = new FileInputStream(tmp2.substring(start + 2, end));
    }
    catch (Exception e)
    {
    	try
    	{
        	//if you could not open the file send a 404
        	output.writeBytes(construct_http_header(404, 0));
        	output.close();
        }
        catch(Exception e2){}
    }

    try
    {
      int type_is = 0;
      //find out what the filename ends with,
      //so you can construct a the right content type
      if (path.endsWith(".zip") || path.endsWith(".exe")|| path.endsWith(".tar"))
      	type_is = 3;
      if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
        type_is = 1;
      if (path.endsWith(".mp3"))
        type_is = 4;
      if (path.endsWith(".wmv"))
        type_is = 5;
      if (path.endsWith(".gif"))
        type_is = 2;
        
      //write out the header, 200 ->everything is ok.
      output.writeBytes(construct_http_header(200, type_is));

      //if it was a HEAD request, we don't print any BODY
      if (method == 1)
      { //1 is GET 2 is head and skips the body
        while (true)
        {
          //read the file from filestream, and print out through the
          //client-outputstream on a byte per byte base.
          int b = requestedfile.read();
          if (b == -1)
          {
            break; //end of file
          }
          output.write(b);
        }
        
      }
	  //clean up the files, close open handles
      output.close();
      requestedfile.close();
      finish_alert();
    }

    catch (Exception e)
    {
    }

  }

  //this method makes the HTTP header for the response
  //the headers job is to tell the browser the result of the request
  //among if it was successful or not.
  private String construct_http_header(int code, int file_type)
  {
    String s = "HTTP/1.0 ";
    
    switch (code)
    {
      case 200:
        s = s + "200 OK";
        break;
      case 400:
        s = s + "400 Bad Request";
        break;
      case 403:
        s = s + "403 Forbidden";
        break;
      case 404:
        s = s + "404 Not Found";
        break;
      case 500:
        s = s + "500 Internal Server Error";
        break;
      case 501:
        s = s + "501 Not Implemented";
        break;
    }

    s = s + "\r\n"; //other header fields,
    s = s + "Connection: close\r\n"; //we can't handle persistent connections
    s = s + "Server: SimpleHTTPtutorial v0\r\n"; //server name

    switch (file_type)
    {
      case 0: break;
      case 1: s = s + "Content-Type: image/jpeg\r\n"; break;
      case 2: s = s + "Content-Type: image/gif\r\n";  break;
      case 3: s = s + "Content-Type: application/x-zip-compressed\r\n"; break;
      case 4: s = s + "Content-Type: audio/mp3\r\n"; break;
      case 5: s = s + "Content-Type: video/wmv\r\n"; break;
      default:s = s + "Content-Type: text/html\r\n"; break;
    }
    
    s = s + "\r\n";
    return s;
  }
  
  public void finish_alert()
  {
  	parent.get_alert();
  }

}

  /*private class Analysis
  {
  	String extention ;
  	String images = "png,gif,jpg,jpeg,pmb,pic,wmf";
  	String audios = "au,aifc,aiff,aif,smd,rmf.kar,mid,midi,smf,mp3,mpeg,ra,ram,rm,wav,wma"; 
  	String videos = "wmv";
  	private Analysis()
  	{
  	}
  	private void read(File file)
  	{
  		try
  		{
  			String[] ext = file.getName().split("\\.");
  			extention = ext[1];
  		}
  		catch(Exception e)
  		{
  			JOptionPane.showMessageDialog(null,"Error analysis this file "+file.getName(),"Analysis erorr",JOptionPane.ERROR_MESSAGE);
  		}

  	}
  	private boolean isimage()
  	{
  		if(images.indexOf(extention.toLowerCase())!=-1 )
  			return true;
  		else
  			return false;
  	}
  	private boolean isaudio()
  	{
  		if(audios.indexOf(extention.toLowerCase())!=-1 )
  			return true;
  		else
  			return false;
  	}
  	private boolean isvideo()
  	{
  		if(videos.indexOf(extention.toLowerCase())!=-1 )
  			return true;
  		else
  			return false;
  	}
  }
  */