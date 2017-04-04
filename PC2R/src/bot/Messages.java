package bot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import server.staticvalue.StaticRequete;

public class Messages {
	private static String	message[]	= { "Je suis trop fort" };
	private static Random	r			= new Random();

	public static void sendMessage(String user, BufferedWriter bf) throws IOException {
		bf.write(StaticRequete.message + "/" + user + "/" + message[r.nextInt(message.length)] + "/");
		try {
			Thread.sleep(r.nextInt(50)*1000);
		} catch (InterruptedException e) {}
	}

}
