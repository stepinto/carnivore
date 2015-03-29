# Carnivore

This is my course project of Compiler Construction at university. It was done in 2008.

It can compile source code in [Tiger language](http://www.cs.columbia.edu/~sedwards/classes/2002/w4115/tiger.pdf) into x86 ASM. JLex and CUP are used to generate the frontend, which are equivallent to LEX and YACC in C world. The backend implements some basic optimization technique. It does control flow analysis to identify basic blocks and perform simple dead-code elimination.
