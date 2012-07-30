package com.nominanuda.dataobject;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import com.nominanuda.lang.Maths;

class ParserUtils {
	public Number parseNumber(String token) {
		Double d = Double.parseDouble(token);
		if(Maths.isInteger(d)) {
			return d.longValue();
		} else {
			return d;
		}
	}
	public Number parseNumber(Token token) {
		return parseNumber(token.getText());
	}
	public Number parseNumber(CommonTree token) {
		return parseNumber(token.getText());
	}

	public String parseKey(CommonTree token) {
		return parseStringContent(token);
	}

	public String parseKey(Token token) {
		return parseStringContent(token);
	}

	public String parseKey(String token) {
		return parseStringContent(token);
	}

	public String parseString(Token token) {
		return parseString(token.getText());
	}
	public String parseString(CommonTree token) {
		return parseString(token.getText());
	}

	public String parseString(String token) {
		return parseStringContent(token.substring(1, token.length() - 1));
	}
	public String parseStringContent(Token token) {
		return parseStringContent(token.getText());
	}
	public String parseStringContent(CommonTree token) {
		return parseStringContent(token.getText());
	}
	public String parseStringContent(String token) {
		StringBuilder b = new StringBuilder();
		char[] ch = token.toCharArray();
		int len = ch.length;
		for(int i = 0; i < len; i++) {
			char c = ch[i];
			if('\\' == c) {
				if(i > len - 2) {
					throw new IllegalArgumentException("spare \\ at the end of stream");
				}
				i++;
				char c1 = ch[i];
				switch (c1) {
				case 'b':
					b.append("\b");
					break;
				case 't':
					b.append("\t");
					break;
				case 'n':
					b.append("\n");
					break;
				case 'f':
					b.append("\f");
					break;
				case 'r':
					b.append("\r");
					break;
				case '\'':
					b.append("\'");
					break;
				case '\"':
					b.append("\"");
					break;
				case '\\':
					b.append("\\");
					break;
				case '/':
					b.append("/");
					break;
				case 'u':
					String unicode = parseUnicode(ch[++i],ch[++i],ch[++i],ch[++i]);
					b.append(unicode);
					break;
				default:
					throw new IllegalArgumentException("unrecognized escape sequence start "+c1);
				}
			} else {
				b.append(c);
			}
		}
		return b.toString();
	}

	private String parseUnicode(char c0, char c1, char c2, char c3) {
		String s = new String(new char[] {c0,c1,c2,c3});
		String s1 = Character.toString((char)Integer.parseInt(s, 16));
		return s1;
	}
}
