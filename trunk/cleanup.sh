rm -rf `find | grep \\.class$`
rm -rf src/org/stepinto/carnivore/parser/sym.java
rm -rf src/org/stepinto/carnivore/parser/Yylex.java
rm -rf src/org/stepinto/carnivore/parser/Parser.java
rm -rf tags
pushd rtl/x86/linux > /dev/null
make clean > /dev/null
popd > /dev/null

