import java.io.InputStream;

public class InStream2 {

	public static void main(String[] args) {
		InputStream is = System.in;
		try {
			while (true) {
				int value = is.read();
				if (value == -1 || value == 'q') break;
				System.out.println((char) value);
			}
		} catch (java.io.IOException e) {
			System.err.println("Khong the doc du lieu tu stdin: " + e.getMessage());
		}
	}

}
