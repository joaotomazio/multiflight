#!/bin/bash

rm -rf report/project/results/tables
mkdir -p report/project/results/tables
for topology in pair cycle fatTree mesh
do	
	output="report/project/results/tables/$topology.csv"
	echo "Mode,Condition,Transport,FMean,FDev,MHMean,MHDev,MHInc,MAMean,MADev,MAInc" >> $output
	for mode in single sequence burst parallel
	do	
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
		for condition in cold crowded
		do
			for transport in tcp udp
			do
				for p in {1..6}
				do
					array[$p]=0
				done
				for tag in "${tags[@]}"
				do	
					pos=1
					for lib in floodlight multiflight_host multiflight_app
					do	
						input="mininet/results/avg/$topology-$lib-$mode-$condition-$transport-$tag.csv"
						line=$(cat $input | head -$max | tail -1)
						IFS=","
						for element in $line; do
							array[$pos]=$(bc <<< "scale=3; ${array[$pos]} + $element")
							let "pos++"
						done
					done
				done
				for p in {1..6}
				do	
					array[$p]=$(bc <<< "scale=2; ${array[$p]} / ${#tags[@]}")
				done
				increase_host=$(bc <<< "scale=4; ((${array[3]}-${array[1]})/${array[1]})")
				increase_host=$(bc <<< "scale=2; ($increase_host * 100)/1")
				increase_app=$(bc <<< "scale=4; ((${array[5]}-${array[1]})/${array[1]})")
				increase_app=$(bc <<< "scale=2; ($increase_app * 100)/1")
				echo "$mode,$condition,$transport,${array[1]},${array[2]},${array[3]},${array[4]},$increase_host,${array[5]},${array[6]},$increase_app" >> $output
			done
		done
	done
done





