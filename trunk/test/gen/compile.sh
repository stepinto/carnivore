./gen.sh $1 > a.asm
nasm -f elf a.asm
cc a.o ../../rtl/x86/linux/runtime.a -o $2
rm -f a.asm a.o

