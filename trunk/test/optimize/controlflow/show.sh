if [ $TERM = "cygwin" ]
then
	CLASS_PATH="../../../src;../../../lib/cup.jar" 
else
	CLASS_PATH="../../../src:../../../lib/cup.jar"
fi

java -cp $CLASS_PATH org.stepinto.carnivore.optimize.controlflow.test.ExtractControlFlow < $1

