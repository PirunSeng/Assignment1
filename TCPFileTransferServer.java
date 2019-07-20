import java.io.*;
import java.net.*;

public class TCPFileTransferServer {
	public static void main(String[] args){
		ServerSocket server = null;
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
    String data = null;
    String reqMethod;
    String reqFile;
    // String fileFromServer;
		try{
			server = new ServerSocket(9999);
			
			while(true){
				System.out.println("-------------------------");
				System.out.println("Wait for client to connect....");
				socket = server.accept();
				System.out.println("Got connection from " + socket.getInetAddress());
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while ( (data = in.readLine()) != null ) {
          reqMethod = data.split(" ")[0];
          reqFile   = data.split(" ")[1];
          if(reqMethod.equals("get")) {
            System.out.println("\r\nRequest method: " + reqMethod);
            System.out.println("Request filename: " + reqFile);

            out = new PrintWriter(socket.getOutputStream(), true);
            // String reqFile = in.readLine();
            File file = new File(reqFile);
            long filesize = file.length();
            if(!file.exists()){
              System.out.println("File does not exists on server");
              out.println("-1"); //File not exist
              continue;
            }else{
              System.out.println("File size = " + filesize + " bytes");
              out.println(""+filesize);
            }
            // send file to client
            if(in.readLine().equals("OK")){
              System.out.println("Sending " + reqFile + " ...");
              OutputStream outSocket = socket.getOutputStream();
              FileInputStream inFile = new FileInputStream(reqFile);
              byte[] buf = new byte[1024];
              int b; long l = 0;
              while((b=inFile.read(buf, 0, 1024)) != -1){
                l += b;
                outSocket.write(buf, 0, b);
              }
              inFile.close();
              System.out.println("Sending completed!");
            }
          }
          // else if (reqMethod.equals("put")) {
            
          // }
        }
				// out = new PrintWriter(socket.getOutputStream(), true);
				
				// String filename = in.readLine();
				// File file = new File(filename);
				// long filesize = file.length();
				// if(!file.exists()){
				// 	System.out.println("File does not exists on server");
				// 	out.println("-1"); //File not exist
				// 	continue;
				// }else{
				// 	System.out.println("File size = " + filesize + " bytes");
				// 	out.println(""+filesize);
				// }

				// if(in.readLine().equals("OK")){
				// 	System.out.println("Sending " + filename + " ...");
				// 	OutputStream outSocket = socket.getOutputStream();
				// 	FileInputStream inFile = new FileInputStream(filename);
				// 	byte[] buf = new byte[1024];
				// 	int b; long l = 0;
				// 	while((b=inFile.read(buf, 0, 1024)) != -1){
				// 		l += b;
				// 		outSocket.write(buf, 0, b);
				// 	}
				// 	inFile.close();
				// 	System.out.println("Sending completed!");
				// }
			}
			
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}
}