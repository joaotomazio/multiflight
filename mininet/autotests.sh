#!/bin/bash

rm -rf mininet/results/raw
rm -rf mininet/results/avg
mkdir mininet/results/raw
mkdir mininet/results/avg
for topology in pair cycle fatTree mesh
do	
	for lib in floodlight multiflight_host multiflight_app
	do	
		make $lib
		make clean
		make build
		for mode in single sequence burst parallel
		do
			for condition in cold crowded
			do
				for transport in tcp udp
				do
					for i in {1..10}
					do
						echo "--- TESTING --- TOPOLOGY: $topology | LIB: $lib | MODE: $mode | STATE: $condition | TRANSPORT: $transport | I: $i"
						docker exec -d floodlight java -jar target/floodlight.jar
						sleep 7

						if [ "$lib" == "multiflight_host" ]; then
							docker exec -d floodlight curl -X PUT -d host localhost:8080/wm/multiflight/profile
						elif [ "$lib" == "multiflight_app" ]; then
							docker exec -d floodlight curl -X PUT -d app localhost:8080/wm/multiflight/profile
						fi
						
						docker exec -it mininet ./run.sh $topology $lib $mode $condition $transport $i
						sleep 1
						docker exec -d mininet mn -c
						docker exec -d floodlight kill $(docker exec -it floodlight ps -A | grep java | awk '{print $1}')
						sleep 1
					done

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

					entries=$max
					if [ $transport == "udp" ]; then
						entries=$(($entries+1))
					fi

					for tag in "${tags[@]}"
					do	
						file="$topology-$lib-$mode-$condition-$transport-$tag"
						echo "AVG OF $file"
						for i in {1..10}
						do	
							path="mininet/results/raw/$file-$i.dat"
							if [ -f $path ]; then
								try=0
								while [ $(wc -l $path | awk '{ print $1 }') -ne $entries ]
								do	
									let "try++"
									if [ $try == $i ]; then
										continue
									fi
									cp "mininet/results/raw/$file-$try.dat" $path
								done
								j=0
								IFS=","
								while read f1 f2 f3 f4 f5 f6 f7 f8 f9
								do	
									let "j++"
									res=$(bc <<< "scale=3; $f9/1000000")
									line[$i*$max + $j]=$res
									if [ $j == $max ]; then
										break
									fi
								done < $path
							else
								echo "$file-$i NOT FOUND"
							fi
						done

						output="mininet/results/avg/$file.csv"
						rm -rf $output
						for (( j=1; j<=$max; j++ ))
						do	
							sum=0
							for i in {1..10}
							do	
								sum=$(bc <<< "$sum+${line[$i*$max + $j]}")
							done
							avg=$(bc <<< "scale=3; $sum/10")
							#echo "AVG: " $avg
							sum=0
							for i in {1..10}
							do	
								diff=$(bc <<< "scale=3; ${line[$i*$max + $j]} - $avg")
								#echo "DIFF: " $diff
								pwr=$(bc <<< "$diff^2")
								#echo "PWR: " $pwr
								sum=$(bc <<< "scale=3; $sum + $pwr")
							done
							#echo "SUM: " $sum
							div=$(bc <<< "scale=3; $sum/10")
							#echo "DIV: " $div
							std=$(bc <<< "scale=3; sqrt($div)")
							#echo "STD: " $std
							echo "$avg,$std" >> $output
						done
					done
				done
			done
		done
	done
done