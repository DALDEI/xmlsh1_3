# Run a single test and return 0 for success or 1 for failure

[ $# -ne 1 ] && exit 1


$1  > _out.txt 
RET=$?
if [ $RET -ne 0 ] ; then
   echo $1 failed: return code: $RET
   exit $RET
fi     

if [ -f out/${1}.out ] ; then 
	xcmp -b _out.txt out/${1}.out 
	if [ $? -ne 0 ] ; then
		echo $1 out/${1}.out different output
		exit 1
	fi
	rm _out.txt
elif [ -f out/${1}.xml ] ; then 
	xcmp -x -b _out.txt out/${1}.xml 
	if [ $? -ne 0 ] ; then
		echo $1 out/${1}.xml different output
		exit 1
	fi
	rm _out.txt
else
	echo $1 - no output to compare 
 	exit 2
fi

exit 0
