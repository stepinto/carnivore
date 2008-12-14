for src in ../tiger_code/practice/*.tig
do
	echo -n `basename $src` "	"

	infile=`echo $src | sed s/\\.tig\\$/\\.in/g`
	ansfile=`echo $src | sed s/\\.tig\\$/\\.ans/g`
	./compile.sh $src tmp
	./tmp < $infile > out
	diff $ansfile out > /dev/null
	if [[ $? -eq 0 ]]
	then
		echo ok
	else
		echo failed
	fi
	rm -f tmp out
done

