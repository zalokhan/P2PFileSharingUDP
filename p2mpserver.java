import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Random;
import java.util.zip.Checksum;



public class p2mpserver {
	public static Random generator = new Random();
	public static boolean flag;
	public static String seq1;
	public static String seq2;
	public static String seq3;
	public static String seq4;
	public static byte ack[]=new byte[8];
	public static byte data[]=new byte[2000];
	public static byte file[]=new byte[100000000];
	public static String path;
	public static String input;
	public static int mss;
	public static int pointer;
	public static int filesize;
	public static String ackseq;
	public static String seq6;
	
	
	public static void checksum(int chksum, byte[] segment) 
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
			int s4=(Integer.parseInt(s3, 2));
			int sum1=chksum+s4;
			String s5 = Integer.toBinaryString(sum1);
			if(s5.length()==17)
			{
				int number2 = Integer.parseInt(s5, 2);
				int sum=number2-0b10000000000000000;
				sum=sum+0b0000000000000001;
				s5 = Integer.toBinaryString(sum);
			}
			if(s5.equalsIgnoreCase("1111111111111111"))
			{
				flag=true;
			}
		}
		else
		{
			flag = true;
		}
	}
	public static void getmss()
	{
		for (int i =8; i<data.length; i++)
		{
			if((data[i]==-128)&&(data[i+1]==127)&&(data[i+2]==126)&&(data[i+3]==0)&&(data[i+4]==126)&&(data[i+5]==127)&&(data[i+6]==0)&&(data[i+7]==0)&&(data[i+8]==0))
			{
				mss = i-8;
				break;
			}
		}
	}
	public static void getfilesize()
	{
		for (int i =0; i<file.length; i++)
		{
			if((file[i]==-128)&&(file[i+1]==127)&&(file[i+2]==126)&&(file[i+3]==0)&&(file[i+4]==126)&&(file[i+5]==-128)&&(file[i+6]==0)&&(file[i+7]==0)&&(file[i+8]==0))
			{ i--;
				while(file[i]==0)
				{
					i--;
				}
				filesize= i+1;
				break;
			}
		}
	}
	public static void ACK()
	{
		if(flag==true)
		{
			//Adding sequence number
			while(ackseq.length()<32)
			{
				ackseq=("0"+ackseq);
			}
			ack[0]=(byte) Integer.parseInt(ackseq.substring(0, 8),2);
			ack[1]=(byte) Integer.parseInt(ackseq.substring(8, 16),2);
			ack[2]=(byte) Integer.parseInt(ackseq.substring(16, 24),2);
			ack[3]=(byte) Integer.parseInt(ackseq.substring(24, 32),2);
			ack[4]=(byte) 00000000;
			ack[5]=(byte) 00000000;
			ack[6]=(byte) 10101010;
			ack[7]=(byte) 10101010;
		}
		else
		{
			ack[0]=data[0];
			ack[1]=data[1];
			ack[2]=data[2];
			ack[3]=data[3];
			ack[4]=(byte) 00000000;
			ack[5]=(byte) 00000000;
			ack[6]=(byte) 10101010;
			ack[7]=(byte) 10101010;
		}
		
	}
	
	public static void CreateFile(byte[] file) throws Exception 
	{
		FileOutputStream fo=new FileOutputStream(path);
		fo.write(file, 0, filesize);
	}
	
	public static void main(String[] args) throws Exception
	{
		seq6="x";
		ackseq="00000000000000000000000000000000";
		flag=false;
		pointer=0;
		double rno,p;
		/*System.out.println("Enter the command");
		BufferedReader b=new BufferedReader(new InputStreamReader(System.in));
		input=b.readLine();*/
		String[] split = args;
		//String[] split = input.split(" ");
		int ownport=Integer.parseInt(split[0]);
		path=split[1];
		p=Double.parseDouble(split[2]);
		
		DatagramSocket serversocket=new DatagramSocket(ownport);
		while(true)
		{
			try 
			{
				DatagramPacket receivePacket=new DatagramPacket(data, data.length);
				
				//System.out.println("Datagram awaiting");
				serversocket.receive(receivePacket);
				//System.out.println("Datagram received");
				
				rno= generator.nextDouble();
				//System.out.println(rno);
				


				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				p2mpserver.getmss();	//get value of mss
				if (mss==0)
				{
					ack[0]=(byte) 00000000;
					ack[1]=(byte) 00000000;
					ack[2]=(byte) 00000000;
					ack[3]=(byte) 00000000;
					ack[4]=(byte) 00000000;
					ack[5]=(byte) 00000000;
					ack[6]=(byte) 10101010;
					ack[7]=(byte) 10101010;
					file[pointer]=(byte)-128;
					file[pointer+1]=(byte)127;
					file[pointer+2]=(byte)126;
					file[pointer+3]=(byte)0;
					file[pointer+4]=(byte)126;
					file[pointer+5]=(byte)-128;
					DatagramPacket acksend=new DatagramPacket(ack, ack.length, IPAddress, port);
					serversocket.send(acksend);
					
					break;
				}
				if(rno>=p)
				{
					
					byte segment[]=new byte[mss];
					for (int i=0;i<mss;i++)
					{
						segment[i]=data[i+8];
					}
					
					
					seq1=Integer.toBinaryString(data[0]);
					//System.out.println(seq1+"   "+data[0]);
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
					if(seq6.equals(seq5))
					{
						int seq=Integer.parseInt(seq5,2);
						int ackn=seq+mss;
						ackseq=Integer.toBinaryString(ackn);
						flag=true;
						ACK();
						DatagramPacket acksend=new DatagramPacket(ack, ack.length, IPAddress, port);
						serversocket.send(acksend);
						continue;
					}
					seq6=seq5;
					int seq=Integer.parseInt(seq5,2);
					int ackn=seq+mss;
					ackseq=Integer.toBinaryString(ackn);
					
					String chk1=Integer.toBinaryString(data[4]);
					String chk2=Integer.toBinaryString(data[5]);
					if(chk1.length()>8)
					{
						chk1 = chk1.substring(chk1.length()-8, chk1.length());
					}
					if(chk1.length()<8)	//appending zeros to make it 8 bit
					{
						while(chk1.length()<8)
						{
							chk1=("0"+chk1);
						}
					}
					if(chk2.length()>8)
					{
						chk2 = chk2.substring(chk2.length()-8, chk2.length());
					}
					if(chk2.length()<8)	//appending zeros to make it 8 bit
					{
						while(chk2.length()<8)
						{
							chk2=("0"+chk2);
						}
					}
					String chk3=chk1+chk2;
					int chksum=Integer.parseInt(chk3,2);
					checksum(chksum,segment);
					//System.out.println("mss : "+mss);
					if(flag==false)
					{
						System.out.println("Checksum did not match");
						continue;	//repeat if checksum incorrect
					}
					
					//writing into file array
					for(int i=0;i<segment.length;i++)
					{
						file[pointer]=segment[i];
						pointer++;
					}
					//System.out.println(pointer);
					ACK();
					DatagramPacket acksend=new DatagramPacket(ack, ack.length, IPAddress, port);
					serversocket.send(acksend);
					
					flag = false;	//setting flag back to 0
				}
				
				else
				{
					seq1=Integer.toBinaryString(data[0]);
					//System.out.println(seq1+"   "+data[0]);
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
					System.out.println("Packet loss, sequence number = "+Integer.parseInt(seq5,2));
				}
			}
			catch (Exception e) 
			{
				System.out.println("Error!!");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}	
		}
		getfilesize();
		System.out.println(filesize);
		CreateFile(file);
		System.out.println("File received");
	}
}//hi 7735 P:\hw2.pdf 0.05