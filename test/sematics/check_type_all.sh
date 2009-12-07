total=0
correct=0

# check good
for f in ../tiger_code/good/*.tig
do
	file=`basename $f`
	if [ "$1" == "-v" ]
	then
		echo ">> Parsing $f..."
	fi

	total=$[ $total + 1 ]
	./check_type.sh $f > /dev/null 2>&1
	if [ $? -eq 0 ]
	then
		correct=$[ $correct + 1]
	else
		if [ $1="v" ]
		then
			echo Error at $f.
		fi
	fi
done

for f in ../tiger_code/bad/*.tig
do
	file=`basename $f`
	if [ "$1" == "-v" ]
	then
		echo ">> Parsing $f..."
	fi

	total=$[ $total + 1]
	./check_type.sh $f > /dev/null 2>&1
	if [ $? -gt 0 ]
	then
		correct=$[ $correct + 1]
	else
		if [ $1="v" ]
		then
			echo Error at $f.
		fi
	fi
done

echo Precision: $correct/$total
