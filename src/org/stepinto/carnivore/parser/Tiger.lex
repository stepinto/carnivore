/*
* FIXME: Line No. may be wrong when string and comment multiple lined enrolled.
*/

package org.stepinto.carnivore.parser;
import java.io.*;
import java_cup.runtime.*;
import org.stepinto.carnivore.common.*;

%%
%public
%cup
%line
%char
%{
	public Yylex(InputStream s, ErrorManager e) {
		this(s);
		errorMgr = e;
	}

	private void error(String msg) {
		errorMgr.report(yyline, msg);
	}

	private Symbol tok(int kind, Object value) {
		return new Symbol(kind, yyline+1, yychar+1, value);
	}
	
	private Symbol tok(int kind) { 
		return new Symbol(kind, yyline+1, yychar+1);
	}

	// skip anything until a double quote occured or eof
	private void skipToQuote() throws IOException {
		int curr = yy_advance();
		while (curr != YY_EOF && curr != '\"')
			curr = yy_advance();
	}

	// eat anything before a double quote. escape chars are to be translated into
	// the actual ascii value it stands for.
	private String eatString() {
		StringBuffer sb = new StringBuffer();
		try {
			while (true) {
				int curr = yy_advance();
				if (curr == YY_EOF) {
					error("Unexpected end of string literal.");
					break;
				}
				if (curr == '\"')
					break;
				else if (curr == '\\') {
					// translate the escape char
					curr = yy_advance();
					if (curr == 'n')
						sb.append('\n');
					else if (curr == 't')
						sb.append('\t');
					else if (curr == '\\')
						sb.append('\\');
					else if (curr == '\"')
						sb.append('\"');
					else if (curr == '^')
						sb.append((char)(yy_advance() - 'A' + 1));
					else if (Character.isDigit(curr)) {
						int x  = curr, y = yy_advance(), z = yy_advance();
						if (Character.isDigit(y) && Character.isDigit(z)) {
							int ascii = (x-'0')*100 + (y-'0')*10 + z-'0';
							if (0 <= ascii && ascii < 128)
								sb.append((char)ascii);
							else {
								error("Expect ASCII value ranging from 0 to 127.");
								skipToQuote();
								return "";
							}
						}
						else {
							error("Expect three digits after \'\\\'.");
							skipToQuote();
							return "";
						}
					}
					else if (Character.isWhitespace(curr)) {
						while (Character.isWhitespace(curr)) 
							curr = yy_advance();
						if (curr != '\\') {
							error("Expect \'\\\' after a series of whitespaces.");
							skipToQuote();
							return "";
						}
					}
					else {
						error("Unexpected escape char: " + (char)curr);
						skipToQuote();
						return "";
					}
				}
				else
					sb.append((char)curr);
			}
		}
		catch (IOException ex) {
			error("Unexpected end of string");
		}
		return sb.toString();
	}

	private void eatComment() {
		try {
			int stack = 1;
			int prev = yy_advance(); 
			int curr = yy_advance();

			while (stack > 0 && curr != YY_EOF) {
				if (prev == '/' && curr == '*')
					stack++;
				else if (prev == '*' && curr == '/')
					stack--;
	
				if (stack > 0) {
					prev = curr;
					curr = yy_advance();
				}
			}
			if (stack > 0)
				error("Expect end of comment block.");
		}
		catch (IOException ex) {
			error("Access denied.");
		}
	}

	public int getCurrLineNo() {
		return yyline + 1;
	}

	private ErrorManager errorMgr;
%}

%%
","		{ return tok(sym.COMMA); }
":"		{ return tok(sym.COLON); }
";"		{ return tok(sym.SEMICOLON); }
"("		{ return tok(sym.LPAREN); }
")"		{ return tok(sym.RPAREN); }
"["		{ return tok(sym.LBRACK); }
"]"		{ return tok(sym.RBRACK); }
"{"		{ return tok(sym.LBRACE); }
"}"		{ return tok(sym.RBRACE); }
"."		{ return tok(sym.DOT); }
"+"		{ return tok(sym.PLUS); }
"-"		{ return tok(sym.MINUS); }
"*"		{ return tok(sym.TIMES); }
"/"		{ return tok(sym.DIVIDE); }
"="		{ return tok(sym.EQ); }
"<>"		{ return tok(sym.NEQ); }
"<"		{ return tok(sym.LT); }
"<="		{ return tok(sym.LE); }
">"		{ return tok(sym.GT); }
">="		{ return tok(sym.GE); }
"&"		{ return tok(sym.AND); }
"|"		{ return tok(sym.OR); }
":="		{ return tok(sym.ASSIGN); }
array		{ return tok(sym.ARRAY); }
if		{ return tok(sym.IF); }
then		{ return tok(sym.THEN); }
else		{ return tok(sym.ELSE); }
while		{ return tok(sym.WHILE); }
for		{ return tok(sym.FOR); }
to		{ return tok(sym.TO); }
do		{ return tok(sym.DO); }
let		{ return tok(sym.LET); }
in		{ return tok(sym.IN); }
end		{ return tok(sym.END); }
of		{ return tok(sym.OF); }
break		{ return tok(sym.BREAK); }
nil		{ return tok(sym.NIL); }
function	{ return tok(sym.FUNCTION); }
var		{ return tok(sym.VAR); }
type		{ return tok(sym.TYPE); }

[A-Za-z]([A-Za-z0-9_])*	{ return tok(sym.ID, yytext()); }
[0-9]+			{ return tok(sym.INT_LITERAL, new Integer(yytext())); }
\"			{ return tok(sym.STRING_LITERAL, eatString()); }

[ \t\r\f\n]	{}
"/*"		{ eatComment(); }		
.		{ error("Unexpected char: " + yytext()); }
