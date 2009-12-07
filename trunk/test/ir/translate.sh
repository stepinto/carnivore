if [ $TERM = "cygwin" ]
then
	CLASS_PATH="../../src;../../lib/cup.jar" 
else
	CLASS_PATH="../../src:../../lib/cup.jar"
fi


java -enableassertions -esa -cp $CLASS_PATH org.stepinto.carnivore.ir.test.Translate < $1
exit
