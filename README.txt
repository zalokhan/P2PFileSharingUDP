Instructions for compiling and running the Client and Server programs. (Windows Command Prompt)

Install jdk 1.7.0. 
Enter new system variable path with the path of the bin folder of "jdk 1.7.0".

First run the server program so that it is ready to listen. The class name is "p2mpserver".
1. Note down the IP Addresses of all the Server machines.
2. Complile the file p2mpserver.java using the command
	javac p2mpserver.java

3. Now you have to run the program as well as enter the required data as follows
	java   p2mpserver  port#   file-name    probability-of-packet-loss

4. For every packet loss, a message on screen will be displayed along with the sequence number of the lost packet.
    After the file transfer is complete, the "File Received" message will be displayed.

NOTE : The file-name should include the entire path too. Also, make sure that there are no spaces inany part of the file path.

On every packet loss, a message is displayed on the screen along with thesequence number.

For example
	java p2mpserver 7735 C:\file.pdf 0.05

After the desired number of servers have been initiated, run the client program. The class name for the client program is "p2mpclient".
1. Compile the client program using the command
	javac p2mpclient.java

2. Now run the program by entering the command in the specified format,
	java p2mpclient   server-1   server-2   server-3   server-port#   file-name   MSS

server-1, server-2,...... are the IP Adresses of the servers.
NOTE : Like in the server, the file-name should include the entire path of the file too. Make sure that there are no spaces in the file path.

For example,
	java p2mpclient 192.168.1.1 10.135.21.35 152.64.23.85 7735 C:\file.pdf 500

3. On every time-out,  a message will be displayed on screen along with the sequence number.
    On completion of the file transfer, the Total Delay in milliseconds will be printed.
