package test;

import java.io.File;
import com.kreative.openxion.util.XIONUtil;

public class AuxiliaryFile {
	public static void main(String[] args) {
		if (args.length >= 2) {
			test(args[0], args[1]);
		} else {
			test("afile.xn", ".eXt");
			test("afile.xn", "eXt");
			test("afile.Xn", ".eXt");
			test("afile.Xn", "eXt");
			test("afile.XN", ".eXt");
			test("afile.XN", "eXt");
			test("afile.xN", ".eXt");
			test("afile.xN", "eXt");
			test("afile.txt", ".eXt");
			test("afile.txt", "eXt");
			test("afile", ".eXt");
			test("afile", "eXt");
			test("afile.xn", ".eXt.10.iOn");
			test("afile.xn", "eXt.10.iOn");
			test("afile.Xn", ".eXt.10.iOn");
			test("afile.Xn", "eXt.10.iOn");
			test("afile.XN", ".eXt.10.iOn");
			test("afile.XN", "eXt.10.iOn");
			test("afile.xN", ".eXt.10.iOn");
			test("afile.xN", "eXt.10.iOn");
			test("afile.txt", ".eXt.10.iOn");
			test("afile.txt", "eXt.10.iOn");
			test("afile", ".eXt.10.iOn");
			test("afile", "eXt.10.iOn");
		}
	}
	
	private static void test(String arg0, String arg1) {
		File file = new File(arg0);
		File aux = XIONUtil.getAuxiliaryFile(file, arg1);
		System.out.println(aux.getAbsolutePath());
	}
}
