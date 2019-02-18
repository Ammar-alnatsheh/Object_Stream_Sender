import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class oss extends JFrame
{
  public static void main(String arg[])
  {
    oss application = new oss();
    application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }


  private ObjectInputStream input;    //objetc will recive from distenation
  private ObjectOutputStream output;  //object will send to distenation
  private file_server serve;		  //server to send the file in streaming way
  private server_class s;             //server class to make communication
  private client_class c;             //client class to make communication
  private Display display;			  //display class to recognize the downloaded file and display it
  private String path;                //referance to the project directory
  private JTextArea show;			  //display the transactions
  private JTextArea text;			  //field to send chatting messages
  private JTextField statusbar;       //bar show what is going on right now
  private JTextField ip;			  //field to enter distenation ip
  private JRadioButton light;		  //light to show connection situation
  private JProgressBar progressbar;   //bar show how much time do we need to finish the sending operation
  private JButton cd;				  //button to make connect or disconnect
  private JButton ss;				  //button to send chatting text
  private boolean con_dis = false;	  //value show if we are connected or not right now
  private boolean s_or_c = true;	  //value show if we are in server mode or client one
  private String files[];			  //files which will be send.
  private int sended_files = 0;		  //number of files which will be send.
  
  public oss()
  {
    super("Object Streaming Sender");
    Container container = getContentPane();
    container.setLayout(new BorderLayout(10,5));
    //.....................................
    JMenu file = new JMenu("File");
    file.setMnemonic('f');
    file.setMnemonic('F');
    JMenu edit = new JMenu("Edit");
    edit.setMnemonic('e');
    edit.setMnemonic('E');
    JMenu connection = new JMenu("Connection");
    connection.setMnemonic('c');
    connection.setMnemonic('C');
    JMenu help = new JMenu("Help");
    help.setMnemonic('h');
    help.setMnemonic('H');

    JMenuItem load = new JMenuItem("Load file");
    load.setMnemonic('l');
    load.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
               load();
            }});
    
    JMenuItem download = new JMenuItem("Download files");
    download.setMnemonic('d');
    download.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
               download();
            }});
    JMenuItem send = new JMenuItem("Send loaded files");
    send.setMnemonic('s');
    send.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	transfer();
            }});
    JMenuItem brows = new JMenuItem("Brows downloaded files");
    brows.setMnemonic('b');
    brows.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	brows();
            }});
    JMenuItem exit = new JMenuItem("Exit");
    exit.setMnemonic('e');
    exit.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
               if(0==JOptionPane.showConfirmDialog(null,"Do you want to exit?","Exit the system",0))
               {
               	dispose();
               	System.exit(0);
               }
            }});
    JMenuItem cut = new JMenuItem("Cut");
    cut.setMnemonic('u');
    cut.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	if(text.getSelectedText()!=null)
            		text.cut();
            	if(show.getSelectedText()!=null)
            		show.cut();
            }});
    JMenuItem copy = new JMenuItem("Copy   ");
    copy.setMnemonic('c');
    copy.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	if(text.getSelectedText()!=null)
            		text.copy();
            	if(show.getSelectedText()!=null)
            		show.copy();
            }});
    JMenuItem past = new JMenuItem("Past");
    past.setMnemonic('p');
    past.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	text.paste();
            }});
    JMenuItem connect = new JMenuItem("Connect");
    connect.setMnemonic('c');
    connect.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	if(con_dis)
            		JOptionPane.showMessageDialog(null,"The program is allready connected","connectino information",JOptionPane.INFORMATION_MESSAGE);
            	else
            		connect();
            }});
    JMenuItem disconnect = new JMenuItem("Disconnect");
    disconnect.setMnemonic('d');
    disconnect.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	if(!con_dis)
            		JOptionPane.showMessageDialog(null,"The program is allready not connected","connectino information",JOptionPane.INFORMATION_MESSAGE);
            	else
            		disconnect();
            }});
    JMenuItem details = new JMenuItem("Connection details");
    details.setMnemonic('i');
    details.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
            	if(s_or_c)
            		JOptionPane.showMessageDialog(null,s.getDetails(),"connection details",JOptionPane.INFORMATION_MESSAGE);
            	else
            		JOptionPane.showMessageDialog(null,c.getDetails(),"connection details",JOptionPane.INFORMATION_MESSAGE);
            }});
            
    JMenuItem about = new JMenuItem("Help About");
    about.setMnemonic('a');
    about.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
               JOptionPane.showMessageDialog(null,"This program written by :\n    - ST.Ammar 'Mohamed Basem' Jawdet Al-natsheh\n    - ID# : 2050854\n as Graduation project\nSupervisor :\n    - Dr.Bassam hammo\nEmail: ammar_2050854@yahoo.com\n                                thank you."," Help _ About",JOptionPane.PLAIN_MESSAGE);
            }});
    JMenuItem topic = new JMenuItem("Help Topic");
    topic.setMnemonic('t');
    topic.addActionListener(new ActionListener(){
            public void actionPerformed( ActionEvent event )
            {
               JOptionPane.showMessageDialog(null,"This program use to send object from known one like\n files, pictures, audio and movies to unknown ones,\nby collect them in a temporary folder then send them\nover the internet to spacific IP adrress using stream\ntechniqe, at the same time you can save what you recive\nfrom another compuetr. the idea is to let the user\nbrowsing some part of that object before recive all of it.\n\n this program also provide simple communication like send\nand recive text without need to save it or use Email services\n beside you can save your property by using security transfering\ndata all what you have to do is to connect using IP address and\nlets the fun begin\n\n                                                  thank you."," Help _ Topic",JOptionPane.PLAIN_MESSAGE);
            }});
    file.add(load);
    file.add(download);
    file.addSeparator();
    file.add(send);
    file.addSeparator();
    file.add(brows);
    file.addSeparator();
    file.add(exit);
    
    edit.add(cut);
    edit.add(copy);
    edit.add(past);
    
    connection.add(connect);
    connection.add(disconnect);
    connection.addSeparator();
    connection.add(details);

    help.add(topic);
    help.addSeparator();
    help.add(about);

    JMenuBar bar = new JMenuBar();
    bar.add(file);
    bar.add(edit);
    bar.add(connection);
    bar.add(help);
    bar.setBorder(BorderFactory.createRaisedBevelBorder());
    setJMenuBar(bar);
    //.....................................
    
    
    show = new JTextArea();
    show.setBorder(BorderFactory.createTitledBorder("conversation Text"));
    show.setEditable(false);
    show.setToolTipText("This field use to show the conversation");
   
    //  box number 1 ..........................................
    
    JPanel box1 = new JPanel();
    box1.setLayout(new BorderLayout());
    ip = new JTextField("enter value",10);
    ip.addActionListener(
    	new ActionListener(){
    		public void actionPerformed(ActionEvent e)
    		{
    			connect();
    		}
    	});
    light = new JRadioButton();
    light.setSelected(true);
    light.setForeground(Color.red);
    light.addItemListener( new ItemListener()
    {
    	public void itemStateChanged(ItemEvent e)
    	{
    		light.setSelected(true);
    	}
    });
    cd = new JButton("Connect");
    cd.setBorder(BorderFactory.createRaisedBevelBorder());
    cd.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent event)
    	{
    		if(!con_dis)
    			connect();
    		else
    			disconnect();
    	}});
    JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(3,1,2,2));
        panel2.add(new JLabel("Destination IP:"));
        panel2.add(ip);
        panel2.add(cd); 
    JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(panel2,BorderLayout.CENTER);      
        panel1.add(light,BorderLayout.EAST);
    box1.add(panel1,BorderLayout.NORTH);
   
    
    
    //  box number 2 ..........................................
    JPanel box2 = new JPanel();
    box2.setLayout(new BorderLayout());
    progressbar = new JProgressBar();
    progressbar.getAccessibleContext().setAccessibleName("progressbar");
    progressbar.setForeground(Color.green);
    progressbar.setBackground(Color.white);
    progressbar.setBorder(BorderFactory.createEmptyBorder());
    
    ss = new JButton("Send");
    ss.setBorder(BorderFactory.createRaisedBevelBorder());
    ss.addActionListener(new ActionListener(){
    	public void actionPerformed(ActionEvent event)
    	{
    		send();
    	}});
    JPanel banel22 = new JPanel(new GridLayout(1,2)); 
    banel22.add(ss);
    banel22.add(new JLabel(""));
    JPanel banel2 = new JPanel(new BorderLayout(1,5));
    banel2.add(banel22,BorderLayout.CENTER);
    banel2.add(progressbar,BorderLayout.SOUTH);
    banel2.add(new JLabel(" "),BorderLayout.EAST);
    banel2.add(new JLabel(" "),BorderLayout.WEST);
    
    Box banel1 = Box.createVerticalBox();
    statusbar = new JTextField("");
    statusbar.setEnabled(false);
    statusbar.setBackground(super.getBackground());
    statusbar.setBorder(BorderFactory.createEmptyBorder());
    text = new JTextArea("",3,10);
    banel1.add(new JScrollPane(text));
    banel1.add(statusbar);
    
    box2.add(banel1,BorderLayout.CENTER);
    box2.add(banel2,BorderLayout.EAST);
    
    s = new server_class();
    s.start();
    
    con_dis = false;
    display = new Display();
    
    path=(getClass().getResource("").toString());
  	path.trim();
  	path=path.substring(0,path.lastIndexOf('/'));
  	path=path.substring(6,path.lastIndexOf('/'));
  	String w[]=path.split("%20");
  	path="";
  	for(int y=0;y<w.length;y++)
  		path=path+w[y]+" ";
  	path=path.trim();

    container.add(new JLabel(" "),BorderLayout.NORTH);
    container.add(new JScrollPane(show),BorderLayout.CENTER);
    container.add(box1,BorderLayout.EAST);
    container.add(box2,BorderLayout.SOUTH);
    setSize(700,500);
    setVisible(true);
    setLocation(100,100);
  }

public static void copyfile(String fromFileName, String toFileName) throws IOException {
    File fromFile = new File(fromFileName);
    File toFile = new File(toFileName);

    if (!fromFile.exists())
      throw new IOException("FileCopy: " + "no such source file: "
          + fromFileName);
    if (!fromFile.isFile())
      throw new IOException("FileCopy: " + "can't copy directory: "
          + fromFileName);
    if (!fromFile.canRead())
      throw new IOException("FileCopy: " + "source file is unreadable: "
          + fromFileName);

    if (toFile.isDirectory())
      toFile = new File(toFile, fromFile.getName());

    if (toFile.exists()) {
      if (!toFile.canWrite())
        throw new IOException("FileCopy: "
            + "destination file is unwriteable: " + toFileName);
      System.out.print("Overwrite existing file " + toFile.getName()
          + "? (Y/N): Y");
      System.out.flush();
      //BufferedReader in = new BufferedReader(new InputStreamReader(
      //   System.in));
      String response = "N" ;//= in.readLine();
      if(0==JOptionPane.showConfirmDialog(null,"This file is allready exist\nDo you want to replace it?","Transfer file",0))
      	response = "Y";
      if (!response.equals("Y") && !response.equals("y"))
        throw new IOException("FileCopy: "
            + "existing file was not overwritten.");
    } else {
      String parent = toFile.getParent();
      if (parent == null)
        parent = System.getProperty("user.dir");
      File dir = new File(parent);
      if (!dir.exists())
        throw new IOException("FileCopy: "
            + "destination directory doesn't exist: " + parent);
      if (dir.isFile())
        throw new IOException("FileCopy: "
            + "destination is not a directory: " + parent);
      if (!dir.canWrite())
        throw new IOException("FileCopy: "
            + "destination directory is unwriteable: " + parent);
    }

    FileInputStream from = null;
    FileOutputStream to = null;
    try {
      from = new FileInputStream(fromFile);
      to = new FileOutputStream(toFile);
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = from.read(buffer)) != -1)
        to.write(buffer, 0, bytesRead); // write
    } finally {
      if (from != null)
        try {
          from.close();
        } catch (IOException e) {
          ;
        }
      if (to != null)
        try {
          to.close();
        } catch (IOException e) {
          ;
        }
    }
  }
  
  private void load()
  {
    JFileChooser filechooser=new JFileChooser();
    filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int r = filechooser.showOpenDialog(this);
    if(r==JFileChooser.CANCEL_OPTION)
      return;
    File file = filechooser.getSelectedFile();
    if(file==null||file.getName().equals(""))
      JOptionPane.showMessageDialog(null,"Invalid file name","Invalid file name",JOptionPane.ERROR_MESSAGE);
    else
    {
      try
      {
      	copyfile(file.toString(),path+"\\oss\\load");
      	JOptionPane.showMessageDialog(null,file.getName()+" is loaded","loading information",JOptionPane.INFORMATION_MESSAGE);
      }
      catch(Exception e)
      {
      	String g1 = "FileCopy: existing file was not overwritten.";
      	String g2 = e.getMessage();
      	if(!g1.equalsIgnoreCase(g2))
      	{
      	  JOptionPane.showMessageDialog(null,"Error loading file.","Error loading file",JOptionPane.ERROR_MESSAGE);
      	}
      }
    }
  }

  private void download()
  {
    JFileChooser filechooser=new JFileChooser();
    filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int r = filechooser.showSaveDialog(null);
    if(r==JFileChooser.CANCEL_OPTION)
      return;
    File file = filechooser.getSelectedFile();
    if(file==null||file.getName().equals(""))
      JOptionPane.showMessageDialog(null,"Invalid file name","Invalid file name",JOptionPane.ERROR_MESSAGE);
    else
    {
     try
      {     	
      	Process p = Runtime.getRuntime().exec("cmd /c md "+file.getPath());
      	File dir = new File(path+"\\oss\\download");
      	String files[]=dir.list();
      	for(int i=0;i<files.length;i++)
      	{
      		JOptionPane.showMessageDialog(null,files[i]+" is downloaded","download information",JOptionPane.INFORMATION_MESSAGE);
      		copyfile(path+"\\oss\\download\\"+files[i],file.getPath()+"\\"+files[i]);
      	}
      }
      catch(Exception e)
      {
      	String g1 = "FileCopy: existing file was not overwritten.";
      	String g2 = e.getMessage();
      	if(!g1.equalsIgnoreCase(g2))
      	{
      	  JOptionPane.showMessageDialog(null,"Error downloading fils.","Error downloading file",JOptionPane.ERROR_MESSAGE);
      	}
      }
    }
  }
  
  private void send()
  {
  	if(!con_dis)
  	{
  		JOptionPane.showMessageDialog(null,"You should connect to a computer first","failed process",JOptionPane.ERROR_MESSAGE);
  		return ;
  	}
  	if(s_or_c)
  		s.sendData(text.getText());
  	else
  		c.sendData(text.getText());
  	text.setText("");
  }

  private void transfer()
  {
  	if(!con_dis)
  	{
  		JOptionPane.showMessageDialog(null,"You should connect to a computer first","failed process",JOptionPane.ERROR_MESSAGE);
  		return ;
  	}
  	try
  	{
  		// prepair an server to send the files in streaming way
  		// the code will be seperated in a file called http_server in the same oss folder.
  		serve = new file_server(2323,this);
  		
  		// get the files names to the other side to handle it.
  		files= new File(path+"\\oss\\load").list();
  		sended_files = 0;
  			
  		progressbar.setVisible(true);
  		progressbar.setMaximum(files.length);
  		progressbar.setValue(0);
  		
  		if(files.length != sended_files)
  		{
  			if(s_or_c)
  			{
  				s.sendData("SERVER will send some files%"+files.length);
  				show.append("\nSERVER will send "+files.length+" files.");
  				s.sendData("SERVER%SEND%FILES%"+files[sended_files]);
  			}
  			else
  			{
  				c.sendData("CLIENT will send some files%"+files.length);
  				show.append("\nCLIENT will send "+files.length+" files.");
  				c.sendData("CLIENT%SEND%FILES%"+files[sended_files]);
  			}
  		}
  		sended_files++;
  	
  	}
  	catch(Exception e)
  	{
  		JOptionPane.showMessageDialog(null,"error preparing for sending objects","sending error",JOptionPane.ERROR_MESSAGE);
  	}
  }
  
  private void brows()
  {
  	//set file chooser on the downloded file directory to select one
  	JFileChooser filechooser=new JFileChooser();
    filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    filechooser.setCurrentDirectory(new File(path+"/oss/download"));
    filechooser.setMultiSelectionEnabled(false);
    int r = filechooser.showOpenDialog(this);
    if(r==JFileChooser.CANCEL_OPTION)
      return;
    File file = filechooser.getSelectedFile();
    if(file==null||file.getName().equals(""))
      JOptionPane.showMessageDialog(null,"Invalid file name","Invalid file name",JOptionPane.ERROR_MESSAGE);
    else
    {
    	try
    	{
    		//after select a file display it using the internet explorer
    		Process c1 = Runtime.getRuntime().exec("cmd /c "+path);
  			Process c2 = Runtime.getRuntime().exec("cmd /c iexplore "+file.getAbsolutePath());
  		}
  		catch(Exception e)
  		{
  			JOptionPane.showMessageDialog(null,"Cant browse this file.","browsing error",JOptionPane.ERROR_MESSAGE);
  		}
    }
  }
  
  private void connect()
  {
  	if(!con_dis)
  	{
  		try
  		{
  		String[] str = ip.getText().split("\\.");
  		if(str.length!=4 || Integer.parseInt(str[0])>255 ||Integer.parseInt(str[1])>255||Integer.parseInt(str[2])>255||Integer.parseInt(str[3])>255 || Integer.parseInt(str[0])<0 ||Integer.parseInt(str[1])<0||Integer.parseInt(str[2])<0 || Integer.parseInt(str[3])<0)
  		{
  			JOptionPane.showMessageDialog(null,"IP address expression is wrong","Error reading ip",JOptionPane.ERROR_MESSAGE);
  		}
  		else
  		{
  			ip.setEditable(false);
  			light.setForeground(Color.green);
  			cd.setLabel("Disconnect");
    		con_dis = true;
    		s.closeConnection();
    		c = new client_class();
  			c.start();
  			s_or_c = false;
  		}
  		}
  		catch(Exception e)
  		{
  			JOptionPane.showMessageDialog(null,"connection error","connection",JOptionPane.ERROR_MESSAGE);
  		}
  	}
  	else
  		JOptionPane.showMessageDialog(null,"you are allready connecting to a computer","connection info",JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void disconnect()
  {
  	if(con_dis)
  	{
  		if(!s_or_c)
  		{
  			c.sendData("TERMINATE%NOW");
  			c.closeConnection();
    		s = new server_class();
  			s.start();
  		}
  		else
  		{
  			s.sendData("TERMINATE%NOW");
  		}
    	light.setForeground(Color.red);
    	cd.setLabel("Connect");
    	con_dis = false;
    	ip.setText("Ente value");
    	ip.setEditable(true);
    	s_or_c = true;
    }
    else
  		JOptionPane.showMessageDialog(null,"you are not connecting to any computer","connection info",JOptionPane.INFORMATION_MESSAGE);
  }
  
  public void get_alert()
  {	
  	progressbar.setValue(progressbar.getValue()+1);
  	if(files.length != sended_files)
  	{
  		if(s_or_c)
  		{
  			s.sendData("CLIENT%DELEVIERD%FILE%");
  			s.sendData("SERVER%SEND%FILES%"+files[sended_files]);
  		}
  		else
  		{
  			c.sendData("SERVER%DELEVIERD%FILE%");
  			c.sendData("CLIENT%SEND%FILES%"+files[sended_files]);
  		}
  	}
  	else
  	{
  		if(s_or_c)
  		{
  			s.sendData("CLIENT%DELEVIERD%FILES%");
  			JOptionPane.showMessageDialog(null,"Client delevierd all the files","Sending info",JOptionPane.INFORMATION_MESSAGE);
  		}
  		else
  		{
  			c.sendData("SERVER%DELEVIERD%FILES%");
  			JOptionPane.showMessageDialog(null,"Server delevierd all the files","Sending info",JOptionPane.INFORMATION_MESSAGE);
  		}
  		
  		progressbar.setValue(0);
  	}
  	
  	sended_files++;
  }
  
  private boolean is_protocol(String t)
  {
  	if(
  	t.startsWith("CLIENT%DELEVIERD%FILES%")||
  	t.startsWith("CLIENT%DELEVIERD%FILE%")||
  	t.startsWith("CLIENT%SEND%FILES%")||
  	t.startsWith("CLIENT will send some files%")||
  	t.startsWith("SERVER%DELEVIERD%FILES%")||
  	t.startsWith("SERVER%DELEVIERD%FILE%")||
  	t.startsWith("SERVER%SEND%FILES%")||
  	t.startsWith("SERVER will send some files%"))
  		return true;
  	else
  		return false;
  }
  
  private class server_class extends Thread
  {
  	private ServerSocket server; //make a server soket for connection
  	private Socket connection;   //make connection
  	private int counter;		 //value to count the recived connections
  	
  	private server_class()
  	{
  		counter = 1;
  		con_dis = true;
        s_or_c = true;
  	 }
  	public void run()
   {
      // set up server to receive connections; process connections
      try {

         // Step 1: Create a ServerSocket.
         server = new ServerSocket( 12345, 100 );

         while ( true ) {

            try {
               	waitForConnection(); // Step 2: Wait for a connection.
              	getStreams();        // Step 3: Get input & output streams.
              	serverprocessConnection(); // Step 4: Process connection.
              	closeConnection();
            }

            // process EOFException when client closes connection 
            catch ( EOFException eofException ) {
               System.err.println( "Server terminated connection" );
            }

            finally {
               ++counter;
            }

         } // end while

      } // end try

      // process problems with I/O
      catch ( IOException ioException ) {
         ioException.printStackTrace();
      }

   } // end method runServer

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {
      show.append( "\nWaiting for connection\n" );
      connection = server.accept(); // allow server to accept connection            
      show.append( "Connection " + counter + " received from: " + connection.getInetAddress().getHostName() );
      
      if(JOptionPane.showConfirmDialog(null,"you got connection from "+connection.getInetAddress().getHostName()+"\ndo you accept?","accept connection",0)!=0)
      {
      	show.append("\nclose the connection with "+connection.getInetAddress().getHostName());
      	closeConnection();
      }
      
      	ip.setText(connection.getInetAddress().toString());
      	ip.setEditable(false);
  		light.setForeground(Color.green);
  		cd.setLabel("Disconnect");
    	con_dis = true;
      
   }

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      show.append( "\nGot I/O streams\n" );
   }

   // process connection with client
   private void serverprocessConnection() throws IOException
   {
      // send connection successful message to client
      String message = "Connection successful";
      sendData( message );

      do { // process messages sent from client

         // read message and display it
         try
         {
         	message = (String)input.readObject();
         	if(message.startsWith("CLIENT%DELEVIERD%FILES%"))
         	{
         		serve.stop();
         		serve = null;
         		JOptionPane.showMessageDialog(null,"The client recive all the files","recive information",JOptionPane.INFORMATION_MESSAGE);
         		progressbar.setValue(0);
         		
         	}
         	else if(message.startsWith("CLIENT%DELEVIERD%FILE%"))
         	{
         		progressbar.setValue(progressbar.getValue()+1);
  
         	}
         	else if(message.startsWith("CLIENT%SEND%FILES%"))
         	{
         		display.run(message.substring(18));
     
         	}
            else if(message.startsWith("CLIENT will send some files%"))
            {
            	int s = Integer.parseInt(message.substring(28));
            	progressbar.setMaximum(s);
            	progressbar.setValue(0);
            	show.append("\nCLIENT send "+s+" files");
            }
            else
            	show.append( "\nCLIENT>>>" + message );
         }

         // catch problems reading from client
         catch ( Exception e ) {
            //show.append( "\nUnknown object type received" );
            break;
         }

      } while ( !message.equals( "TERMINATE%NOW" ) );

   } // end method server processConnection
   
   // close streams and socket
	   private void closeConnection() 
   {
      show.append( "\nTerminating connection\n" );
      show.setText("");

      try {
         output.close();
         input.close();
         connection.close();
         light.setForeground(Color.red);
    	 cd.setLabel("Connect");
    	 con_dis = false;
     	 ip.setText("Ente value");
    	 ip.setEditable(true);
    	 s_or_c = true;
      }
      catch( Exception e ) {
      }
   }

   // send message to client
   private void sendData( String message )
   {
      // send object to client
      try {
         output.writeObject((Object) message);
         output.flush();
         if(!is_protocol(message))
         	show.append( "\nSERVER>>> " + message );
      }

      // process problems sending object
      catch ( Exception e ) {
         show.append( "\nError writing object" );
      }
   }
   
   
   private String getDetails()
   {
   	try
   		{
   			return "You are in Server mode\nYour local address "+connection.getLocalAddress()+"\nYour opening port "+connection.getLocalPort()+"\n The client address is "+connection.getInetAddress().getHostAddress()+"\n The client name is "+connection.getInetAddress().getHostName();
   		}
   		catch(Exception e)
   		{
   			return "There is no connection";
   		}
   }
  }
  
  private class client_class extends Thread
  {
  	private Socket connection;  //open connection to deal with server
  	
  	private client_class()
  	{	}
   
   public void run() 
   {
      // connect to server, get streams, process connection
      try {
         connectToServer(); // Step 1: Create a Socket to make connection
         getStreams();      // Step 2: Get the input and output streams
         clientprocessConnection(); // Step 3: Process connection
      }

      // server closed connection
      catch ( EOFException eofException ) {
         System.err.println( "Client terminated connection" );
      }

      // process problems communicating with server
      catch ( IOException ioException ) {
         ioException.printStackTrace();
      }

   } // end method runClient

   // connect to server
   private void connectToServer() throws IOException
   {      
      show.append( "Attempting connection\n" );

      // create Socket to make connection to server
      connection = new Socket( InetAddress.getByName( ip.getText() ), 12345 );

      // display connection information
      show.append( "Connected to: " + 
         connection.getInetAddress().getHostName() );
   }

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      show.append( "\nGot I/O streams\n" );
   }   
   private void clientprocessConnection() throws IOException
   {
   	String message = "";
   	do
   	{    // read message and display it
        try
        {	
         	message  =(String)input.readObject();
         	if(message.startsWith("CLIENT%DELEVIERD%FILES"))
         	{
         		serve.stop();
         		serve = null;
         		JOptionPane.showMessageDialog(null,"The server recive all the files","recive information",JOptionPane.INFORMATION_MESSAGE);
         		progressbar.setValue(0);
         		//return;
         	}
         	else if(message.startsWith("CLIENT%DELEVIERD%FILE"))
         	{
         		progressbar.setValue(progressbar.getValue()+1);
         		//return;
         	}
         	else if(message.startsWith("SERVER%SEND%FILES%"))
         	{
         		display.run(message.substring(18));
         		//return;
         	}
         	else if(message.startsWith("SERVER will send some files%"))
            {
            	int s = Integer.parseInt(message.substring(28));
            	progressbar.setMaximum(s);
            	show.append("\nSERVER send "+s+" files");
            }
            else 
            {
            	if(message.equals( "TERMINATE%NOW" ))
            		disconnect();
            	else
            		show.append( "\nSERVER>>>" + message );
            }
         }

         // catch problems reading from server
         catch ( Exception e ) {
            //show.append( "\nUnknown object type received" );
            break;
         }
   	}while(!message.equals( "TERMINATE%NOW" ));

   } // end method client processConnection

   // close streams and socket
   private void closeConnection() 
   {
      show.append( "\nTerminating connection\n" );
      show.setText("");

      try {
         output.close();
         input.close();
         connection.close();
      }
      catch( IOException ioException ) {
         ioException.printStackTrace();
      }
   }

   // send message to server
   private void sendData( String message )
   {
      // send object to server
      try {
         output.writeObject((Object)message);
         output.flush();
         if(!is_protocol(message))
         	show.append( "\nCLIENT>>> " + message );
      }

      // process problems sending object
      catch ( IOException ioException ) {
         show.append( "\nError writing object" );
      }
   }
   

   private String getDetails()
   {
   		try
   		{
   			return "You are in client mode\nYour local address "+connection.getLocalAddress()+"\nYour opening port "+connection.getLocalPort()+"\n The host address is "+connection.getInetAddress().getHostAddress()+"\n The host name is "+connection.getInetAddress().getHostName();
   		}
   		catch(Exception e)
   		{
   			return "There is no connection";
   		}
   }
  }
  

  private class Display extends Thread
  {
  	Socket file_client;
  	BufferedInputStream in_buffer;
  	BufferedOutputStream out_buffer;
  	private Display()
  	{
  	}
  	private void run(String file)
  	{	
  		try
  		{
  			file_client = new Socket( InetAddress.getByName( ip.getText() ), 2323 );

				byte[] receivedData = new byte[1100];
				int number;
				ObjectOutputStream req = new ObjectOutputStream(file_client.getOutputStream());
				req.writeObject((Object)file);
				req.flush();
				in_buffer = new BufferedInputStream(file_client.getInputStream());
				out_buffer = new BufferedOutputStream(new FileOutputStream(path+"\\oss\\download\\"+file));
				while ((number = in_buffer.read(receivedData)) != -1)
				{
					out_buffer.write(receivedData,0,number);
				}
				out_buffer.close();
		}
  		catch(Exception e)
  		{
  			JOptionPane.showMessageDialog(null,"the program can not recive the sending files.","reciving error",JOptionPane.ERROR_MESSAGE);
  		}
  		
  	}
  }

}

