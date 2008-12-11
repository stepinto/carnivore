LIB=lib
SRC=src
JLEX=$LIB/jlex.jar
CUP=$LIB/cup.jar
TIGER_LEX=$SRC/org/stepinto/carnivore/parser/Tiger.lex
TIGER_CUP=$SRC/org/stepinto/carnivore/parser/Tiger.cup
if [ $TERM = "cygwin" ]
then
	CLASS_PATH="$LIB/cup.jar;$SRC"
else
	CLASS_PATH="$LIB/cup.jar:$SRC"
fi

# cleanup
./cleanup.sh

# using lex&cup tools to generate automata
echo ">> Building parser..."
java -jar $CUP -expect 1 -parser Parser < $TIGER_CUP > cup_out 2>&1
if [[ $? -gt 0 ]]
then
	echo !! CUP failed. 
	cat cup_out
	rm -f cup_out
	exit
fi
rm -f cup_out
mv Parser.java $SRC/org/stepinto/carnivore/parser/
mv sym.java $SRC/org/stepinto/carnivore/parser/

echo ">> Building scanner..."
java -jar $JLEX $TIGER_LEX > lex_out 2>&1
if [[ $? -gt 0 ]]
then
	echo !! JLex failed.
	cat lex_out
	rm -f lex_out
	exit
fi
rm -f lex_out
mv $SRC/org/stepinto/carnivore/parser/Tiger.lex.java $SRC/org/stepinto/carnivore/parser/Yylex.java

# compile java source files
echo ">> Compiling source files..."
echo `find $SRC | grep \\.java$` | while read line
do
	javac -g -cp $CLASS_PATH -encoding iso8859-1 $line
	if [[ $? -gt 0 ]]
	then
		echo !! Compile failed.
		exit
	fi
done

# update ctags
echo ">> Updating tags..."
ctags -R
