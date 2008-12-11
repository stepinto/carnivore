package org.stepinto.carnivore.parser.test;

import java.lang.reflect.*;
import java.util.*;
import java_cup.runtime.*;
import org.stepinto.carnivore.common.*;
import org.stepinto.carnivore.parser.*;

public class DumpSyntaxTree {
	private static boolean isSubclassOf(Class a, Class b) {
		while (a != null) {
			if (a == b)
				return true;
			a = a.getSuperclass();
		}
		return false;
	}

	private static String getShortName(String fullName) {
		int p = fullName.lastIndexOf('.');
		if (p == -1)
			return fullName;
		else
			return fullName.substring(p+1);
	}

	private static void traverse(SyntaxTree curr, String prefix, boolean lastChild) throws Exception {
		Class c = curr.getClass();
		Field fs[] = c.getDeclaredFields();
		String className = getShortName(c.getCanonicalName());

		//if (lastChild)
		//	System.out.println(prefix + "©¸©¤" + className);
		//else
		//	System.out.println(prefix + "©À©¤" + className);
		System.out.println(prefix + "+---" + className);
		
		ArrayList<SyntaxTree> children = new ArrayList<SyntaxTree>();
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.get(curr) != null) {
				// System.out.println(f.getType());
				if (isSubclassOf(f.getType(), SyntaxTree.class))
					children.add((SyntaxTree)f.get(curr));
				else if (f.getType() == List.class || f.getType() == ArrayList.class) {
					List list = (List)f.get(curr);
					for (Object obj: list) {
						if (obj instanceof SyntaxTree)
							children.add((SyntaxTree)obj);
					}
				}
			}
		}

		for (int i = 0; i < children.size(); i++) {
			// String newPrefix = prefix + (lastChild ? "    " : "©¦  ");
			String newPrefix = prefix + (lastChild ? "    " : "|   ");
			traverse(children.get(i), newPrefix, (i == children.size()-1));
		}
	}

	public static void main(String []args) {
		ErrorManager err = new ErrorManager();
		java_cup.runtime.Scanner scanner = new Yylex(System.in, err);
		Parser parser = new Parser(scanner, err);

		try {
			parser.parse();
		} catch (Exception ex) {
			System.err.println("Unexpected error in parsing.");
			ex.printStackTrace();
			System.exit(1);
		}

		try {
			SyntaxTree root = parser.getSyntaxTree();
			traverse(root, "", true);
		} catch (Exception ex) {
			System.err.println("Unexpected error in dumping syntax tree");
			ex.printStackTrace();
			System.exit(1);
		}
	}
}

