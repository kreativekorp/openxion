package test;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import com.kreative.openxion.*;
import com.kreative.openxion.ast.XNStatement;

public class XNParserTree {
	public static void main(String[] args) throws IOException {
		DefaultMutableTreeNode toptop = new DefaultMutableTreeNode("XION Scripts");
		for (String arg : args) {
			try {
				System.out.println("Parsing "+arg+"...");
				FileInputStream str = new FileInputStream(arg);
				Reader r = new InputStreamReader(str, "UTF-8");
				XNContext ctx = new XNContext(new XNStdInOutUI());
				ctx.loadModule(XNStandardModule.instance());
				ctx.loadModule(XNExtendedModule.instance());
				XNLexer lex = new XNLexer(r);
				XNParser par = new XNParser(ctx, lex);
				List<XNStatement> scr = par.parse();
				DefaultMutableTreeNode top = new DefaultMutableTreeNode(arg);
				top.add(objectToTreeNode("", scr));
				toptop.add(top);
			} catch (XNScriptError err) {
				System.out.println(err.getMessage() + " on line " + err.getLine() + " at character " + err.getCol());
				err.printStackTrace();
				return;
			}
		}
		JTree t = new JTree(toptop);
		JScrollPane s = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JFrame f = new JFrame("XION Syntax Tree");
		f.setContentPane(s);
		f.setSize(600,400);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	private static DefaultMutableTreeNode objectToTreeNode(String pfx, Object o) {
		DefaultMutableTreeNode top;
		if (o == null) {
			top = new DefaultMutableTreeNode(pfx+"(null)");
		} else {
			top = new DefaultMutableTreeNode(pfx+toString(o));
			if (o instanceof Object[]) {
				int i = 0;
				for (Object oo : (Object[])o) {
					top.add(objectToTreeNode(Integer.toString(i++)+": ", oo));
				}
			}
			else if (o instanceof Collection) {
				int i = 0;
				for (Object oo : (Collection<?>)o) {
					top.add(objectToTreeNode(Integer.toString(i++)+": ", oo));
				}
			}
			else if (o instanceof Map) {
				for (Object oo : ((Map<?,?>)o).entrySet()) {
					Map.Entry<?,?> e = (Map.Entry<?,?>)oo;
					top.add(objectToTreeNode(toString(e.getKey())+": ", e.getValue()));
				}
			}
			else {
				Class<?> c = o.getClass();
				while (c.getSimpleName().startsWith("XN")) {
					Field[] flds = c.getDeclaredFields();
					for (Field fld : flds) {
						if (Modifier.isStatic(fld.getModifiers())) continue;
						else if (!Modifier.isPublic(fld.getModifiers())) continue;
						else if (o instanceof XNToken && fld.getName().equals("next")) continue;
						else if (o instanceof XNToken && fld.getName().equals("specialToken")) continue;
						else try {
							fld.setAccessible(true);
							top.add(objectToTreeNode(fld.getName()+": ", fld.get(o)));
						} catch (IllegalAccessException iae) {}
					}
					c = c.getSuperclass();
				}
			}
		}
		return top;
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
