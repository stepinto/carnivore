if [ $TERM = "cygwin" ]
then
	CLASS_PATH="../../src;../../lib/cup.jar" 
else
	CLASS_PATH="../../src:../../lib/cup.jar"
fi


java -enableassertions -esa -cp $CLASS_PATH org.stepinto.carnivore.arch.x86.test.GenerateAsm < $1
exit
