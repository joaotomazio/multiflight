#!/bin/bash
topology=$1
mode=$2
condition=$3
transport=$4

mkdir -p report/project/results/graph

if [ "$mode" == "single" ]; then
	max=11
	tags=(c)
elif [ "$mode" == "sequence" ]; then
	max=11
	tags=(seq1 seq2)
elif [ "$mode" == "burst" ]; then
	max=16
	tags=(bur1 bur2 bur3)
elif [ "$mode" == "parallel" ]; then
	max=11
	tags=(c1 c2)
else
	echo "Unknown parameter"
fi

for tag in "${tags[@]}"
do	
	pos=1
	for lib in floodlight multiflight_host multiflight_app
	do
		input="mininet/results/avg/$topology-$lib-$mode-$condition-$transport-$tag.csv"
		output="report/project/results/graph/$topology-$lib-$mode-$condition-$transport-$tag.csv"
		IFS=","
		j=0
		while read f1 f2
		do	
			let "j++"
			if [ $j == $max ]; then
				break
			fi
			echo "$f1" >> $output
		done < $input
	done
done