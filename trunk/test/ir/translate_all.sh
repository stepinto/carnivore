for f in ../tiger_code/good/*.tig
do
	file=`basename $f`
	target=`echo $file | sed s/\\.tig/\\.ir/g`

	#echo ">> Translating $file..."
	errmsg=`./translate.sh $f > $target`
	if [ $? -gt 0 ]
	then
		echo "$file	error	$errmsg"
		rm -f $target
	else
		echo "$file	ok"
	fi
done

