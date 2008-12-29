if [ $TERM = "cygwin" ]
then
	CLASS_PATH="../../src;../../lib/cup.jar" 
	LIB="../../rtl/x86/cygwin/runtime.a"
else
	CLASS_PATH="../../src:../../lib/cup.jar"
	LIB="../../rtl/x86/linux/runtime.a"
fi

java -cp $CLASS_PATH org.stepinto.carnivore.Carnivore $1 -O -o tmp.o -c 
cc tmp.o $LIB -o $2
rm -rf tmp.o

