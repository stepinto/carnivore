for f in ../tiger_code/good/*.tig ../tiger_code/bad/*.tig
do
	file=`basename $f`

	#echo ">> Parsing $file..."
	./syntax_check.sh $f 2>&1 > /dev/null
	if [ $? -gt 0 ]
	then
		echo "$file	error"
	else
		echo "$file	ok"
	fi
done

