package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket Ssock;

    public Server(int port) throws IOException {
	Ssock = new ServerSocket(port);
    }

    public static void handle(Socket sock) throws IOException {

	Byte[] buffer = new Byte[1024];
	BufferedReader inBR = new BufferedReader(new InputStreamReader(
		sock.getInputStream()));
	BufferedWriter outBW= new BufferedWriter(new OutputStreamWriter(
		sock.getOutputStream()));
	
	while(true){
	    System.out.println("debut while (handle)");
	    String received=inBR.readLine();
	    String[] tok=received.split("/");
	    System.out.println("recu "+ received);
	    if(tok[0].equals("CONNEXION")){
		outBW.write(" retour : Bienvenue "+tok[1] );
		outBW.flush();
	    }
	    
	    
	}
	
	
	
	
	
    }

    public static void main(String[] args) {

	try {
	    Server server = new Server(Integer.parseInt(args[0]));

	    while (true) {
		System.out.println("debut while (main)");
		Socket sock = server.Ssock.accept();

		Thread t=new Thread(){
		    public void run(){
			try {
			    handle(sock);
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    }
		};
		t.start();
		
	    }

	} catch (NumberFormatException | IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
