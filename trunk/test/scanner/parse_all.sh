for src in ../tiger_code/good/*.tig ../tiger_code/bad/*.tig
do
	echo "Parsing `basename $src` ..."
	tag=`basename $src | sed s/\\.tig/\\.tok/g`
	./dump_tokens.sh $src > $tag
done
