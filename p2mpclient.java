import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class p2mpclient implements Runnable{

	public static String path;
	public static String checksum;
	public static String request;
	public static int pointer;
	public static int servers;
	public static int mss;
	public static int serverport;
	public static long size;
	public static long rtt=400;
	public static byte data[];
	public static boolean[] flag;
	public static String [] ip;
	public static Thread[] threads;
	public static long starttime;
	public static long endtime;
	public static String[] split;
	
	public static DatagramSocket serversocket;
	
	public static void decode() throws Exception
	{
		
		//decoding MSS
		mss=Integer.parseInt(split[split.length-1]);
		
		//decoding file path and name
		path=split[split.length-2];
		
		//decoding server port #
		serverport=Integer.parseInt(split[split.length-3]);
		
		//decoding number of servers
		servers = split.length-4;
		
		ip = new String[servers];
		
		//decoding ip addresses
		for(int i=0; i<servers; i++)
		{
			ip[i] = split[i+1];
		}
		
	}
	
	public static void checksum(byte[] segment)
	{
		
		if((segment.length%2)==0)
		{
			String s3="0000000000000000";
			
			for(int i=0; i<segment.length; i=i+2)
			{
				String x0 = Integer.toBinaryString(segment[i]);
				String x1 = Integer.toBinaryString(segment[i+1]);
				
				if(x0.length()<8)	//appending zeros to make it 8 bit
				{
					while(x0.length()<8)
					{
						x0=("0"+x0);
					}
				}
				if(x0.length()>8)
				{
					x0 = x0.substring(x0.length()-8, x0.length());
				}
				
				if(x1.length()<8)	//appending zeros to make it 8 bit
				{
					while(x1.length()<8)
					{
						x1=("0"+x1);
					}
				}
				if(x1.length()>8)
				{
					x1 = x1.substring(x1.length()-8, x1.length());
				}
				
				String word = x1+x0;
				
			
				int number0 = Integer.parseInt(word, 2);
				int number1 = Integer.parseInt(s3, 2);
				
				int sum = number0 + number1;
				s3 = Integer.toBinaryString(sum);
				if(s3.length()==17)
				{
					int number2 = Integer.parseInt(s3, 2);
					sum=number2-0b10000000000000000;
					sum=sum+0b0000000000000001;
					s3 = Integer.toBinaryString(sum);
				}	
				
			}
			int s4=~(Integer.parseInt(s3, 2));
			String s5 = Integer.toBinaryString(s4);
			checksum = s5.substring(16,32);
			
		}
	}
	
	public static byte[] header(byte[] segment)
	{
		byte[] data = new byte [8+mss+6];
		
		//Adding sequence number
		String p = Integer.toBinaryString(pointer);
		while(p.length()<32)
		{
			p=("0"+p);
		}
		data[0] = (byte) Integer.parseInt(p.substring(0, 8),2);
		data[1] = (byte) Integer.parseInt(p.substring(8, 16),2);
		data[2] = (byte) Integer.parseInt(p.substring(16, 24),2);
		data[3] = (byte) Integer.parseInt(p.substring(24, 32),2);
		
		//Adding checksum
		p2mpclient.checksum(segment);
		String s = checksum;
		String s1 = s.substring(0, 8);
		String s2 = s.substring(8, 16);
		
		int c1 = Integer.parseInt(s1, 2);
		int c2 = Integer.parseInt(s2, 2);
		data[4] = (byte) c1;
		data[5] = (byte) c2;
		
		//adding 01010101010101
		data[6] = (byte) 0b01010101;
		data[7] = (byte) 0b01010101;
		
		for(int i=8; i<data.length-6; i++)
		{
			data[i] = segment[i-8];
		}
		data[data.length-6]=(byte)-128;
		data[data.length-5]=(byte)127;
		data[data.length-4]=(byte)126;
		data[data.length-3]=(byte)0;
		data[data.length-2]=(byte)126;
		data[data.length-1]=(byte)127;
		
		return data;		
	}
	
	public void run() {
		
		try 
		{
			while(true)
			{
				long starttime = System.currentTimeMillis();
				String name = Thread.currentThread().getName();
				int position = 50000;
				
				for(int i=0; i<ip.length; i++)
				{
					if(name.equals(ip[i]))
					{
						position = i;
						break;
					}
				}
				long currenttime = System.currentTimeMillis();;
				while(currenttime<(starttime+rtt))
				{	
					Thread.currentThread().sleep(rtt);
					currenttime = System.currentTimeMillis();
				}
				
				String seq1, seq2, seq3, seq4;
				seq1=Integer.toBinaryString(data[0]);
				if(seq1.length()>8)
				{
					seq1 = seq1.substring(seq1.length()-8, seq1.length());
				}
				if(seq1.length()<8)	//appending zeros to make it 8 bit
				{
					while(seq1.length()<8)
					{
						seq1=("0"+seq1);
					}
				}
				seq2=Integer.toBinaryString(data[1]);
				if(seq2.length()>8)
				{
					seq2 = seq2.substring(seq2.length()-8, seq2.length());
				}
				if(seq2.length()<8)	//appending zeros to make it 8 bit
				{
					while(seq2.length()<8)
					{
						seq2=("0"+seq2);
					}
				}
				seq3=Integer.toBinaryString(data[2]);
				if(seq3.length()>8)
				{
					seq3 = seq3.substring(seq3.length()-8, seq3.length());
				}
				if(seq3.length()<8)	//appending zeros to make it 8 bit
				{
					while(seq3.length()<8)
					{
						seq3=("0"+seq3);
					}
				}
				seq4=Integer.toBinaryString(data[3]);
				if(seq4.length()>8)
				{
					seq4 = seq4.substring(seq4.length()-8, seq4.length());
				}
				if(seq4.length()<8)	//appending zeros to make it 8 bit
				{
					while(seq4.length()<8)
					{
						seq4=("0"+seq4);
					}
				}
				String seq5=seq1+seq2+seq3+seq4;
				System.out.println ("Timeout, sequence number = "+Integer.parseInt(seq5,2));
				
				DatagramPacket udpsend=new DatagramPacket(data,data.length,InetAddress.getByName(ip[position]),7735);
                serversocket.send(udpsend);
				
			}
		} 
		catch (InterruptedException | IOException e) 
		{
		}
	}
	
	public static void main(String[] args) {
		
		try{
			starttime = System.currentTimeMillis();
			serversocket =new DatagramSocket();
			BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
			

			split = new String[args.length+1];
			split [0] = "p2mpclient";
			for(int i=0; i<args.length; i++)
			{
				split[i+1]= args[i];
			}
			p2mpclient.decode();
				
			
			FileInputStream fr = new FileInputStream (path);
			
			size = fr.available();
			System.out.println("Length of the entire file is : "+size+" bytes");
			flag = new boolean [servers];

			int num = (int) Math.ceil(size/mss);
			
			byte file[]=new byte[(num+1)*mss];
			fr.read(file, 0, (int) size);
			
			
			pointer = 0;
			System.out.println("total number of packets : "+num);
			
			for(int loop=0; loop<(num+1); loop++)
			{
				byte segment[]=new byte[mss];
				//preparing payload
				for(int i=pointer; i<pointer+mss; i++)
				{
					segment[i-pointer]=file[i];
				}
				
				data = new byte [8+mss+6];
				data = p2mpclient.header(segment);
				
				
				for(int i=0; i<servers; i++)
				{
					DatagramPacket udpsend=new DatagramPacket(data,data.length,InetAddress.getByName(ip[i]),serverport);
	                serversocket.send(udpsend);
	                p2mpclient c = new p2mpclient();
	                Thread t = new Thread(c);
	                t.setName(ip[i]);
	                t.start();
	                flag[i] = false;
				}

				ThreadGroup rootGroup = Thread.currentThread( ).getThreadGroup( );
				ThreadGroup parentGroup;
				
				//Listing Threads
				while ( ( parentGroup = rootGroup.getParent() ) != null ) 
				{
				    rootGroup = parentGroup;
				}
				threads = new Thread[ rootGroup.activeCount() ];
				while ( rootGroup.enumerate( threads, true ) == threads.length ) 
				{
				    threads = new Thread[ threads.length * 2 ];
				}
				
				reception:
				while (true)
				{
					byte[] rdata = new byte [8];
					DatagramPacket clientsocket=new DatagramPacket(rdata,rdata.length);
	                serversocket.receive(clientsocket);
	                
	                
	                int ack0 = rdata[0];
	                int ack1 = rdata[1];
	                int ack2 = rdata[2];
	                int ack3 = rdata[3];
	                
	                //If negative acknowledgement then ignore
	                if((ack0==data[0])&&(ack1==data[1])&&(ack2==data[2])&&(ack3==data[3]))
	                {
	                	continue;
	                }
	                else
	                {
	                	InetAddress add = clientsocket.getAddress();
	                	String ipadd = add.toString();
	                	
	                	for(int i=0;i<threads.length; i++)
	                	{
	                		String s = "/"+threads[i].getName();
	                		if(s.equals(ipadd))
	                		{
	                			threads[i].interrupt();	//killing the thread
	                			threads[i].join(5000);;
	                			break;
	                		}
	                	}
	                	
	                	for(int i=0; i<ip.length; i++)
	                	{
	                		String s = "/"+ip[i];
	                		if(s.equals(ipadd))
	                		{
	                			flag[i]=true;
	                			break;
	                		}
	                	}
	                }
	                for(int i=0; i<flag.length; i++)
	                {
	                	if(flag[i]==false)
	                	{
	                		continue  reception;
	                	}            	
	                }
	                break reception;
				}
				
                pointer = pointer + mss;
                
			}
			
			for(int i=0; i<servers; i++)
			{
				byte segment[]=new byte[mss];
				segment[0]=(byte)-128;
				segment[1]=(byte)127;
				segment[2]=(byte)126;
				segment[3]=(byte)0;
				segment[4]=(byte)126;
				segment[5]=(byte)127;
				data = p2mpclient.header(segment);
				DatagramPacket udpsend=new DatagramPacket(data,data.length,InetAddress.getByName(ip[i]),serverport);
                serversocket.send(udpsend);
                flag[i] = false;
			}	
			
			//Check for reception of finish packet
			reception:
				while (true)
				{
					byte[] rdata = new byte [8];
					DatagramPacket clientsocket=new DatagramPacket(rdata,rdata.length);
	                serversocket.receive(clientsocket);
	                
	                System.out.println("Finish packet received");
	                
	                int ack0 = rdata[0];
	                int ack1 = rdata[1];
	                int ack2 = rdata[2];
	                int ack3 = rdata[3];
	                
	                //If negative acknowledgement then ignore
	                if((ack0==data[0])&&(ack1==data[1])&&(ack2==data[2])&&(ack3==data[3]))
	                {
	                	continue;
	                }
	                else
	                {
	                	InetAddress add = clientsocket.getAddress();
	                	String ipadd = add.toString();
	                	
	                	
	                	for(int i=0; i<ip.length; i++)
	                	{
	                		String s = "/"+ip[i];
	                		if(s.equals(ipadd))
	                		{
	                			flag[i]=true;
	                			break;
	                		}
	                	}
	                }
	                for(int i=0; i<flag.length; i++)
	                {
	                	if(flag[i]==false)
	                	{
	                		continue  reception;
	                	}
	                }
	                
                	break reception;
				}
			
			System.out.println ("Number of packets :"+num+1);
			endtime = System.currentTimeMillis();
			long delay = endtime - starttime;
			
			System.out.println("The total delay is : "+delay);
			
		}
		catch(Exception e)
		{
			System.out.println ("ERROR !!!");
			System.out.println (e.getMessage());
			e.printStackTrace();
		}
	}
}
//hi 10.139.59.48 7735 C:/sol1.pdf 1000