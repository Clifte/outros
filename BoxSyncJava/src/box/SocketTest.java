package box;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;


public class SocketTest {

	public static void main(String[] args) throws IOException {
		ServerSocket s = new ServerSocket(4000);
		Socket sc = s.accept();
		
		InputStream in =  sc.getInputStream();
		
		
		BufferedReader bis = new BufferedReader (new InputStreamReader (in) );
		
		System.out.println(bis.readLine());
		System.out.println(bis.readLine());
		System.out.println(bis.readLine());

		
	}
}
