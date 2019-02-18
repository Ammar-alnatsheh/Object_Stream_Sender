import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class file_server extends Thread
{
	private int port;
	private FileInputStream requestedfile = null;
	private oss parent;
	private ServerSocket serversocket = null;
	private String path;
  public file_server(int listen_port, oss parent1)
  {
  	path=(getClass().getResource("").toString());
  	path.trim();
  	path=path.substring(0,path.lastIndexOf('/'));
  	path=path.substring(6,path.lastIndexOf('/'));
  	String w[]=path.split("%20");
  	path="";
  	for(int y=0;y<w.length;y++)
  		path=path+w[y]+" ";
  	path=path.trim();

  	parent = parent1;
    port = listen_port;
    this.start();
  }

  public void run()
  {
  	try
  	{
    	serversocket = new ServerSocket(port,100);

    	while (true)
    	{

        	Socket connectionsocket = serversocket.accept();
        	InetAddress client = connectionsocket.getInetAddress();
        	ObjectInputStream input = new ObjectInputStream(connectionsocket.getInputStream());
        	DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());
        	file_handler(input, output);
      	}
      }
      catch (Exception e)
      {
      	JOptionPane.showMessageDialog(null,e.toString(),"Error connection",JOptionPane.ERROR_MESSAGE);
      }

  }

  private void file_handler(ObjectInputStream input, DataOutputStream output)
  {
    String file_name = "";

      try
      {
      	file_name = (String)input.readObject();

      }
      catch(Exception e)
      {
      	JOptionPane.showMessageDialog(null,"cant read http constructer","Reading error",JOptionPane.ERROR_MESSAGE);
      }

    try
    {
    	requestedfile = new FileInputStream(path+"\\oss\\load\\"+file_name);
    }
    catch (Exception e)
    {
    	JOptionPane.showMessageDialog(null,"cant read this file "+file_name,"Reading error",JOptionPane.ERROR_MESSAGE);
    }

    try
    {
    	byte[] buffer = new byte[1000];
    	while(requestedfile.available()>0)
    	{
     		//read the file from filestream, and print out through the
     		//client-outputstream on a byte per byte base.
        	output.write(buffer, 0, requestedfile.read(buffer));
        }

	  //clean up the files, close open handles
      output.close();
      requestedfile.close();
      finish_alert();
      Process c1 = Runtime.getRuntime().exec("cmd /c "+path+"\\oss\\load");
      Process c2 = Runtime.getRuntime().exec("cmd /c del "+file_name);
    }
	catch(Exception e)
    {
       JOptionPane.showMessageDialog(null,"cant send this file "+file_name,"writting error",JOptionPane.ERROR_MESSAGE);
    }

  }

  public void finish_alert()
  {
  	parent.get_alert();
  }

}
