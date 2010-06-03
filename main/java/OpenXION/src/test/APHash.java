package test;
import com.kreative.openxion.util.AtkinsonHash;

public class APHash {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg + ": " + Long.toString(AtkinsonHash.hash(arg) & 0xFFFFFFFFL));
		}
	}
}
