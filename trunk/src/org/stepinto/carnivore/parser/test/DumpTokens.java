package org.stepinto.carnivore.parser.test;

import java.io.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;
import java_cup.runtime.*;

class DumpTokens {
	private static String getTokenName(int tok) {
		int tokens[] = {sym.COMMA, sym.COLON, sym.SEMICOLON, sym.LPAREN,
				sym.RPAREN, sym.LBRACK, sym.RBRACK, sym.LBRACE,
			       	sym.RBRACE, sym.DOT, sym.PLUS, sym.MINUS, 
				sym.TIMES, sym.DIVIDE, sym.EQ, sym.NEQ, sym.LT,
			       	sym.LE, sym.GT, sym.GE, sym.AND, sym.OR,
			       	sym.ASSIGN, sym.ARRAY, sym.IF, sym.THEN, 
				sym.ELSE, sym.WHILE, sym.FOR, sym.TO, sym.DO,
			       	sym.LET, sym.IN, sym.END, sym.OF, sym.BREAK,
				sym.NIL, sym.FUNCTION, sym.VAR, sym.TYPE,
				sym.STRING_LITERAL, sym.INT_LITERAL, sym.ID};
		String names[] = {"comma", "colon", "semicolon", "lparen",
		      		 "rparen", "lbrack", "rbrack", "lbrace", 
				 "rbrace", "dot", "plus", "minus", "times",
				 "divide", "eq", "neq", "lt", "le", "gt",
				 "ge", "and", "or", "assign", "array", "if",
				 "then", "else", "while", "for", "to", "do",
				 "let", "in", "end", "of", "break", "nil",
				 "function", "var", "type", "string", "int",
				 "id"};

		assert(tokens.length == names.length);
		for (int i = 0; i < tokens.length; i++)
			if(tokens[i] == tok)
				return names[i];
		return "unknown-token";
	}

	public static void main(String []args) throws Exception {
		File fin = new File(args[0]);
		ErrorManager errorMgr = new ErrorManager();
		Scanner scn = new Yylex(new FileInputStream(fin), errorMgr);
		Symbol symbol;

		while ((symbol = scn.next_token()) != null) {
			int lineNo = symbol.left;
			String tokenName = getTokenName(symbol.sym);
			System.out.print(lineNo + "\t");
			System.out.print(tokenName);
			if (symbol.value instanceof String)
				System.out.print("\t" + symbol.value);
			else if (symbol.value instanceof Integer)
				System.out.print("\t" + symbol.value);
			System.out.println();
		}

		errorMgr.printAll();
	}
};

