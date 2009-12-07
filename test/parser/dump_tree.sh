if [ $TERM = "cygwin" ]
then
	CLASS_PATH="../../src;../../lib/cup.jar"
else
	CLASS_PATH="../../src:../../lib/cup.jar"
fi

java -enableassertions -cp $CLASS_PATH org.stepinto.carnivore.parser.test.DumpSyntaxTree < $1
exit
