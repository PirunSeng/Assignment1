import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPFileTransferClient {
	public static void main(String[] args){
		if(!checkArgs(args)){
			return;
		}
		
    String reqMethod, filename, request, savedfilename;
		String ip               = args[0];
		final int PORT          = 9999;
    String prefixFileName   = "from-server-";
		Socket socket           = null;
    Scanner scanner         = new Scanner(System.in);
		PrintWriter writeOut    = null;
		BufferedReader response = null;	
		
		try{
			socket = new Socket(ip, PORT);
			socket.setSoTimeout(5*1000); //Wait for 1s for reading timeout
      while(true) {
        writeOut  = new PrintWriter(socket.getOutputStream(), true);
			  response  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        request   = scanner.nextLine();
        reqMethod = request.split(" ")[0];
        if (reqMethod.equals("quit")) {
          break;
        }else {
          filename      = request.split(" ")[1];
          savedfilename = prefixFileName + filename;
          if (reqMethod.equals("get")) {
            // send request to server
            writeOut.println(request);
            // Wait for server replies whether file exists or returns file size
            long filesize = Long.parseLong(response.readLine());
            if(filesize < 0){
              System.out.println("Oop! File does not exist on server!\r\n");
              continue;
            }else{
              System.out.println("File size = " + filesize + " bytes");
            }
            // inform server to start receiving file
            writeOut.println("OK");
            System.out.println("Start receiving " + filename + " from server and renamed it to " + prefixFileName + filename);
            FileOutputStream incomingFile = new FileOutputStream(savedfilename);
            try{
              InputStream inSocket = socket.getInputStream();
              int b; long l=0;
              byte[] buf = new byte[1024];
              while((b = inSocket.read(buf, 0, 1024)) != -1){
                l += b;
                incomingFile.write(buf, 0, b);
                if(l == filesize) break;
              }
              System.out.println("Receiving completed!");
              System.out.println("-------------------------");

            }catch(SocketTimeoutException ste){
              System.out.println("[Error] Receiving timeout!!!!");
            }finally{
              incomingFile.close();
            }
          } else if (reqMethod.equals("put")) {
            File file = new File(filename);
            long filesize = file.length();
            if(!file.exists()){
              System.out.println("File does not exists on client");
				      System.out.println("-------------------------");
              // writeOut.println("-1"); //File not exist
              continue;
            }else{
              // send request to server only if file exists
              writeOut.println(request);
              System.out.println("File size on client = " + filesize + " bytes");
              writeOut.println(""+filesize);
            }
            // if server say OK
            if(response.readLine().equals("OK")){
              System.out.println("Sending " + filename + " ...");
              OutputStream outSocket = socket.getOutputStream();
              FileInputStream sendingFile = new FileInputStream(filename);
              byte[] buf = new byte[1024];
              int b;
              // long l = 0;
              while((b=sendingFile.read(buf, 0, 1024)) != -1){
                // l += b;
                outSocket.write(buf, 0, b);
              }
              sendingFile.close();
              System.out.println("Sending completed!");
				      System.out.println("-------------------------");
            }
          }
        }
      }
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}
	
	private static boolean checkArgs(String[] args){
    if(args.length<1){
			System.out.println("Usage: java TCPClient ip");
			return false;
		}

		return true;
	}
}
