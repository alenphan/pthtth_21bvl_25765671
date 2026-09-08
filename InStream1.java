package bai_2;

import java.io.IOException;
import java.io.InputStream;

public class InStream1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InputStream is = System.in;
		while (true) {
			try {
				int ch = is.read();
				if (ch == -1 || ch == 'q') {
					break;
				}
				System.out.println((char) ch);
				
			} catch (IOException ioe) {
				// TODO: handle exception
				System.err.println(ioe);
			}
		}
	}

}
