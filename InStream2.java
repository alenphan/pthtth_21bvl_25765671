package bai_2;

import java.io.InputStream;

public class InStream2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InputStream is = System.in;
		try {
			while (true) {
				if (is.available() > 0) {
					byte[] buffer = new byte[is.available()];
					int bytesRead = is.read(buffer);
					if (bytesRead == -1) {
						break;
					}
					String str = new String(buffer, 0, bytesRead);
					System.out.println(str);
				} else {
					System.out.println('.');
					Thread.sleep(100);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
