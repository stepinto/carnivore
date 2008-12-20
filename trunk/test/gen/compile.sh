./gen.sh $1 > a.asm
if [ $TERM = "cygwin" ]
then
	BIN_TYPE="win32"
	OS="cygwin"
else
	BIN_TYPE="elf"
	OS="linux"
fi

nasm -f $BIN_TYPE a.asm -o a.o
cc a.o ../../rtl/x86/$OS/runtime.a -o $2
rm -f a.asm a.o

