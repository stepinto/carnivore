package org.stepinto.carnivore.arch.x86;

import java.io.*;
import java.util.*;
import org.stepinto.carnivore.arch.*;
import org.stepinto.carnivore.ir.*;
import org.stepinto.carnivore.sematics.*;

public class X86Generator extends Generator {
	public static final String JUMP_FLAG_PREFIX = "L";
	public static final String ENTRY_FUNC_LABEL = "_tig_start";

	public X86Generator(Map<Function, InsBuffer> funcIns, List<String> strs, Function entryFunc,
			PrintStream out) {
		this.funcIns = funcIns;
		this.strs = strs;
		this.entryFunc = entryFunc;
		this.out = out;
	}

	public void generate() {
		generateHeader();
		out.println("SECTION .data");
		generateStringTable(strs);
		out.println("SECTION .text");
		generateExternDecls();
		for (Map.Entry<Function, InsBuffer> kv: funcIns.entrySet())
			generateFunc(kv.getKey(), kv.getValue());
	}

	private void generateExternDecls() {
		out.println("extern\t_malloc");
		out.println("extern\t_mkstr");
		for (Function func: RuntimeFunctions.getList())
			out.println("extern\t_" + func.getName());
		out.println("global\t" + ENTRY_FUNC_LABEL);
	}

	private void generateHeader() {
		out.println("; This file is generated automatically by Carnivore.");
	}

	private void generateStringTable(List<String> strs) {
		int i = 0;
		for (String s: strs) {
			String label = Translator.STRING_CONST_PREFIX + i;
			out.print(label + ":\tdb\t");
			for (int j = 0; j < s.length(); j++)
				out.print((j == 0 ? "" : ", ") + String.valueOf((int)s.charAt(j)));
			out.println();
			i++;
		}
	}

	private void generateFunc(Function func, InsBuffer ibuf) {
		Frame frame = func.getFrame();
		List<Variable> params = new ArrayList<Variable>();
		if (frame.getStaticLink() != null)
			params.add(frame.getStaticLink());
		params.addAll(frame.getParams());
		List<Variable> locals = frame.getLocals();
		localStackSize = locals.size() * IntelArch.INT_SIZE;

		// generate push ebp/mov ebp, esp... stuff
		out.println("; function " + func.getName());
		if (func == entryFunc)
			out.println(ENTRY_FUNC_LABEL + ":");
		else
			out.println(Translator.USER_FUNC_PREFIX + func.getUid() + ":");
		out.println("\tpush\tebp");
		out.println("\tmov\tebp, esp");
		out.println("\tsub\tesp, " + localStackSize);

		// generate function body
		jumpLabels = new HashMap<Integer, String>();
		for (Ins ins: ibuf.getIns()) {
			String l = JUMP_FLAG_PREFIX + (++maxJumpLabelId);
			if (ins instanceof JumpIns)
				jumpLabels.put(((JumpIns)ins).getTarget(), l);
			else if (ins instanceof JumpIfIns)
				jumpLabels.put(((JumpIfIns)ins).getTarget(), l);
		}
		for (Ins ins: ibuf.getIns())
			generateIns(ins, frame);
	}

	private void generateIns(Ins ins, Frame frame) {
		if (jumpLabels.containsKey(ins.getLineNo()))
			out.print(jumpLabels.get(ins.getLineNo()) + ":");
		else
			out.print("");

		if (ins instanceof ArthIns)
			generateArthIns((ArthIns)ins, frame);
		else if (ins instanceof AssignIns)
			generateAssignIns((AssignIns)ins, frame);
		else if (ins instanceof CallIns)
			generateCallIns((CallIns)ins, frame);
		else if (ins instanceof JumpIns)
			generateJumpIns((JumpIns)ins, frame);
		else if (ins instanceof JumpIfIns)
			generateJumpIfIns((JumpIfIns)ins, frame);
		else if (ins instanceof MemReadIns)
			generateMemReadIns((MemReadIns)ins, frame);
		else if (ins instanceof MemWriteIns)
			generateMemWriteIns((MemWriteIns)ins, frame);
		else if (ins instanceof NopIns)
			generateNopIns((NopIns)ins, frame);
		else if (ins instanceof ParamIns)
			generateParamIns((ParamIns)ins, frame);
		else if (ins instanceof RetIns)
			generateRetIns((RetIns)ins, frame);
		else 
			assert(false);
	}

	private void generateArthIns(ArthIns ins, Frame frame) {
		int op = ins.getOp();
		String result = getOperand(ins.getResult(), frame);
		String left = getOperand(ins.getLeft(), frame);
		String right = getOperand(ins.getRight(), frame);
		if (op == ArthIns.PLUS) {
			out.println("\tmov\teax, " + left);
			out.println("\tadd\teax, " + right);
			out.println("\tmov\t" + result + ", eax");
		}
		else if (op == ArthIns.MINUS) {
			out.println("\tmov\teax, " + left);
			out.println("\tsub\teax, " + right);
			out.println("\tmov\t" + result + ", eax");
		}
		else if (op == ArthIns.TIMES) {
			out.println("\tmov\teax, " + left);
			out.println("\tmov\tebx, " + right);
			out.println("\txor\tedx, edx");
			out.println("\timul\tebx");
			out.println("\tmov\t" + result + ", eax");
		}
		else if (op == ArthIns.DIVIDE) {
			out.println("\tmov\teax, " + left);
			out.println("\tmov\tebx, " + right);
			out.println("\tidiv\tebx");
			out.println("\tmov\t" + result + ", eax");
		}
		else
			assert(false);
	}

	private void generateAssignIns(AssignIns ins, Frame frame) {
		String opnd1 = getOperand(ins.getLeft(), frame);
		String opnd2 = getOperand(ins.getRight(), frame);
		out.println("\tmov\teax, " + opnd2);
		out.println("\tmov\t" + opnd1 + ", eax");
	}

	private void generateCallIns(CallIns ins, Frame frame) {
		out.println("\tcall\t" + ins.getTarget());
		out.println("\tadd\tesp, " + paramCount * IntelArch.INT_SIZE);
		if (ins.getLeft() != null) {
			String opnd = getOperand(ins.getLeft(), frame);
			out.println("\tmov\t" + opnd + ", eax");
		}
		paramCount = 0;
	}

	private void generateJumpIns(JumpIns ins, Frame frame) {
		out.println("\tjmp\t" + jumpLabels.get(ins.getTarget()));
	}

	private void generateJumpIfIns(JumpIfIns ins, Frame frame) {
		// to avoid conditional jump is out of range
		// we have to generate code like:
		//       je next
		//       jmp target
		// next: nop

		String left = getOperand(ins.getLeft(), frame);
		String right = getOperand(ins.getRight(), frame);
		out.println("\tmov\teax, " + left);
		out.println("\tmov\tebx, " + right);
		out.println("\tcmp\teax, ebx");
		
		String jn = null;
		switch (ins.getRelOp()) {
			case JumpIfIns.LT:
				jn = "jge";
				break;
			case JumpIfIns.GT:
				jn = "jle";
				break;
			case JumpIfIns.EQ:
				jn = "jne";
				break;
			case JumpIfIns.NEQ:
				jn = "je";
				break;
			case JumpIfIns.LE:
				jn = "jg";
				break;
			case JumpIfIns.GE:
				jn = "jl";
				break;
			default:
				assert(false);
		}
		String label = JUMP_FLAG_PREFIX + (maxJumpLabelId++);
		out.println("\t" + jn + "\t" + label);
		out.println("\tjmp\t" + jumpLabels.get(ins.getTarget()));
		out.println(label + ":\tnop");
	}

	private void generateMemReadIns(MemReadIns ins, Frame frame) {
		String left = getOperand(ins.getLeft(), frame);
		String base = getOperand(ins.getBase(), frame);
		String index = getOperand(ins.getIndex(), frame);

		out.println("\tmov\tesi, " + base);
		out.println("\tadd\tesi, " + index);
		out.println("\tmov\teax, dword [esi]");
		out.println("\tmov\t" + left + ", eax");
	}

	private void generateMemWriteIns(MemWriteIns ins, Frame frame) {
		String base = getOperand(ins.getBase(), frame);
		String index = getOperand(ins.getIndex(), frame);
		String right = getOperand(ins.getRight(), frame);

		out.println("\tmov\teax, " + right);
		out.println("\tmov\tesi, " + base);
		out.println("\tadd\tesi, " + index);
		out.println("\tmov\tdword [esi], eax");
	}

	private void generateNopIns(NopIns ins, Frame frame) {
		out.println("\tnop");
	}

	private void generateParamIns(ParamIns ins, Frame frame) {
		String opnd = getOperand(ins.getParam(), frame);
		out.println("\tmov\teax, " + opnd);
		out.println("\tpush\teax");
		paramCount++;
	}

	private void generateRetIns(RetIns ins, Frame frame) {
		if (ins.getVar() != null)
			out.println("\tmov\teax, " + getOperand(ins.getVar(), frame));
		out.println("\tadd\tesp, " + (localStackSize + IntelArch.INT_SIZE));
		out.println("\tret");
	}

	private boolean isNumber(String s) {
		if (s.length() > 0) {
			int i = 0;
			if (s.charAt(0) == '+' || s.charAt(0) == '-')
				i++;
			for (; i < s.length(); i++)
				if (!Character.isDigit(s.charAt(i)))
					return false;
			return true;
		}
		assert(false);
		return false;
	}

	private String userVarName(Variable var) {
		return Translator.USER_VAR_PREFIX + var.getUid();
	}


	private int getVarOffset(String name, Frame frame) {
		Variable staticLink = frame.getStaticLink();
		if (staticLink != null && name.equals(userVarName(staticLink)))
			return frame.getOffset(staticLink);
		for (Variable var: frame.getLocals())
			if (name.equals(userVarName(var)))
				return frame.getOffset(var);
		for (Variable var: frame.getParams())
			if (name.equals(userVarName(var)))
				return frame.getOffset(var);
		System.out.println(name);
		assert(false);
		return 0;
	}

	private String getOperand(String name, Frame frame) {
		if (isNumber(name))
			return name;
		else if (name.equals(Translator.FRAME_POINTER_NAME))
			return "ebp";
		else if (name.startsWith(Translator.STRING_CONST_PREFIX))
			return name;
		else {
			int offset = getVarOffset(name, frame);
			if (offset < 0)
				return "[ebp-" + (-offset) + "]";
			else
				return "[esp+" + offset + "]";
		}
	}

	private Map<Function, InsBuffer> funcIns;
	private List<String> strs;
	private Function entryFunc;
	private PrintStream out;
	private int paramCount;
	private int localStackSize;
	private Map<Integer, String> jumpLabels;
	private int maxJumpLabelId;
}

