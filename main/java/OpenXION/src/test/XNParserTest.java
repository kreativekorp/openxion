package test;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import com.kreative.openxion.*;
import com.kreative.openxion.ast.XNStatement;

public class XNParserTest {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			try {
				System.out.println("Parsing "+new File(arg).getName()+"...");
				FileInputStream str = new FileInputStream(arg);
				Reader r = new InputStreamReader(str, "UTF-8");
				XNContext ctx = new XNContext(new XNStdInOutUI());
				ctx.loadModule(XNStandardModule.instance());
				ctx.loadModule(XNExtendedModule.instance());
				XNLexer lex = new XNLexer(r);
				XNParser par = new XNParser(ctx, lex);
				List<XNStatement> scr = par.parse();
				printObject("", "", scr);
			} catch (XNScriptError err) {
				System.out.println(err.getMessage() + " on line " + err.getLine() + " at character " + err.getCol());
				err.printStackTrace();
				return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void printObject(String ind, String pfx, Object o) {
		if (o == null) {
			System.out.println(ind+pfx+"(null)");
		} else {
			System.out.println(ind+pfx+toString(o));
			if (o instanceof Object[]) {
				int i = 0;
				for (Object oo : (Object[])o) {
					printObject(ind+"\t", Integer.toString(i++)+": ", oo);
				}
			}
			else if (o instanceof Collection) {
				int i = 0;
				for (Object oo : (Collection)o) {
					printObject(ind+"\t", Integer.toString(i++)+": ", oo);
				}
			}
			else if (o instanceof Map) {
				for (Object oo : ((Map)o).entrySet()) {
					Map.Entry e = (Map.Entry)oo;
					printObject(ind+"\t", toString(e.getKey())+": ", e.getValue());
				}
			}
			else {
				Class c = o.getClass();
				while (c.getSimpleName().startsWith("XN")) {
					Field[] flds = c.getDeclaredFields();
					for (Field fld : flds) {
						if (Modifier.isStatic(fld.getModifiers())) continue;
						else if (!Modifier.isPublic(fld.getModifiers())) continue;
						else if (o instanceof XNToken && fld.getName().equals("next")) continue;
						else if (o instanceof XNToken && fld.getName().equals("specialToken")) continue;
						else try {
							fld.setAccessible(true);
							printObject(ind+"\t", fld.getName()+": ", fld.get(o));
						} catch (IllegalAccessException iae) {}
					}
					c = c.getSuperclass();
				}
			}
		}
	}
	
	private static String toString(Object o) {
		String s;
		if (o instanceof Object[]) {
			s = "[";
			for (Object oo : (Object[])o) {
				s += toString(oo)+", ";
			}
			if (s.endsWith(", ")) s = s.substring(0, s.length()-2);
			s += "]";
		} else if (o instanceof Collection) {
			s = "[";
			for (Object oo : (Collection<?>)o) {
				s += toString(oo)+", ";
			}
			if (s.endsWith(", ")) s = s.substring(0, s.length()-2);
			s += "]";
		} else {
			s = o.toString();
		}
		return s;
	}
}
