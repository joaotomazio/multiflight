up: down
	@ echo docker-compose up
	docker-compose -f docker/docker-compose.yml up -d

down:
	@ echo docker-compose down
	docker-compose -f docker/docker-compose.yml down

clean:
	@ echo "Multiflight Env Clean"
	@ rm -rf floodlight/target/
	@ rm -f report/project/thesis.aux
	@ rm -f report/project/thesis.bbl
	@ rm -f report/project/thesis.blg
	@ rm -f report/project/thesis.lof
	@ rm -f report/project/thesis.log
	@ rm -f report/project/thesis.lot
	@ rm -f report/project/thesis.out
	@ rm -f report/project/thesis.pdf
	@ rm -f report/project/thesis.synctex.gz
	@ rm -f report/project/thesis.toc
	@ rm -f report/project/thesis.fls
	@ rm -f report/project/thesis.fdb_latexmk

build:
	#docker exec -it floodlight ant
	ant -f floodlight/

run:
	docker exec -it floodlight java -jar target/floodlight.jar

fl: build run

mn:
	xhost +
	docker exec -it mininet bash

test:
	@ chmod +x mininet/autotests.sh
	@ chmod +x mininet/run.sh
	@ make up
	@ mininet/autotests.sh

table:
	@ chmod +x mininet/scripts/table.sh
	@ ./mininet/scripts/table.sh

floodlight:
	cp floodlight/src/main/resources/floodlight.properties \
		floodlight/src/main/resources/floodlightdefault.properties
	cp floodlight/src/main/resources/META-INF/services/floodlight.IFloodlightModule \
		floodlight/src/main/resources/META-INF/services/net.floodlightcontroller.core.module.IFloodlightModule

multiflight_host:
	cp floodlight/src/main/resources/multiflight.properties \
		floodlight/src/main/resources/floodlightdefault.properties
	cp floodlight/src/main/resources/META-INF/services/multiflight.IFloodlightModule \
		floodlight/src/main/resources/META-INF/services/net.floodlightcontroller.core.module.IFloodlightModule

multiflight_app:
	cp floodlight/src/main/resources/multiflight.properties \
		floodlight/src/main/resources/floodlightdefault.properties
	cp floodlight/src/main/resources/META-INF/services/multiflight.IFloodlightModule \
		floodlight/src/main/resources/META-INF/services/net.floodlightcontroller.core.module.IFloodlightModule

.PHONY: floodlight