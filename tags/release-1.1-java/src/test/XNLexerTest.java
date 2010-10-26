package test;

import java.io.*;
import com.kreative.openxion.*;
import com.kreative.openxion.util.XIONUtil;

public class XNLexerTest {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			System.out.println("Lexing: "+new File(arg).getName());
			XNLexer lex = new XNLexer(new InputStreamReader(new FileInputStream(arg), "UTF-8"));
			while (true) {
				XNToken tok = lex.getToken();
				if (tok.isEOF()) break;
				System.out.println(
						"\t" + getTokenType(tok.kind) +
						"\t" + tok.beginLine + ":" + tok.beginColumn +
						"\t" + tok.endLine + ":" + tok.endColumn +
						"\t\t" + XIONUtil.quote(tok.image));
			}
		}
	}

    private static String getTokenType(int tokenType) {
        switch(tokenType) {
        case XNToken.LINE_TERM: return "LT";
        case XNToken.QUOTED: return "QL";
        case XNToken.NUMBER: return "NUM";
        case XNToken.ID: return "ID";
        case XNToken.SYMBOL: return "SYM";
        case XNToken.COMMENT: return "CMT";
        case XNToken.CONTINUATOR: return "CONT";
        case XNToken.WHITESPACE: return "WS";
        default: return "OTHER";
        }
    }
}
