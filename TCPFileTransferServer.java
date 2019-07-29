import java.io.*;
import java.net.*;

public class TCPFileTransferServer {
	public static void main(String[] args){
		ServerSocket server     = null;
		Socket socket           = null;
		BufferedReader response = null;
		PrintWriter writeOut    = null;
    String data             = null;
    String prefixFileName   = "from-client-";
    String reqMethod, reqFile, savedfilename;

		try{
			server = new ServerSocket(9999);
			
			while(true){
				System.out.println("-------------------------");
				System.out.println("Wait for client to connect....");
				socket = server.accept();
				System.out.println("Got connection from " + socket.getInetAddress());
				response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while ( (data = response.readLine()) != null ) {
          reqMethod = data.split(" ")[0];
          reqFile   = data.split(" ")[1];
          savedfilename = prefixFileName + reqFile;
          if(reqMethod.equals("get")) {
            writeOut = new PrintWriter(socket.getOutputStream(), true);
            File file = new File(reqFile);
            long filesize = file.length();
            if(!file.exists()){
              System.out.println("File does not exists on server");
				      System.out.println("-------------------------");
              writeOut.println("-1"); //File not exist
              continue;
            }else{
              System.out.println("File size = " + filesize + " bytes");
              writeOut.println(""+filesize);
            }
            // send file to client
            if(response.readLine().equals("OK")){
              System.out.println("Sending " + reqFile + " ...");
              OutputStream outSocket = socket.getOutputStream();
              FileInputStream inFile = new FileInputStream(reqFile);
              byte[] buf = new byte[1024];
              int b;
              // long l = 0;
              while((b=inFile.read(buf, 0, 1024)) != -1){
                // l += b;
                outSocket.write(buf, 0, b);
              }
              inFile.close();
              System.out.println("Sending completed!");
				      System.out.println("-------------------------");
            }
          } else if (reqMethod.equals("put")) {
            System.out.println("Request is put");
            long filesize = Long.parseLong(response.readLine());
            System.out.println("File size = " + filesize + " bytes");
            writeOut = new PrintWriter(socket.getOutputStream(), true);
            writeOut.println("OK");

            System.out.println("Start receiving " + reqFile + " from client and renamed it to " + prefixFileName + reqFile);
            FileOutputStream outFile = new FileOutputStream(savedfilename);
            try{
              InputStream inSocket = socket.getInputStream();
              int b; long l=0;
              byte[] buf = new byte[1024];
              while((b = inSocket.read(buf, 0, 1024)) != -1){
                l += b;
                outFile.write(buf, 0, b);
                if(l == filesize) break;
              }
              System.out.println("Receiving completed!");
              System.out.println("-------------------------");
            }catch(SocketTimeoutException ste){
              System.out.println("[Error] Receiving timeout!!!!");
            }finally{
              outFile.close();
            }
          }
        }
			}
			
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}
}
