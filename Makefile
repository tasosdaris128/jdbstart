all: run

build:
	clear
	mvn clean
	mvn compile
	mvn package

run:
	make build
	cp src/main/java/com/tasos/jdbstart/config/application.properties target/
	cp log4j2.properties target/
	cd target/ && java -Dlog4j2.configurationFile=log4j2.properties -jar tasos-bin-0.1.0.jar && cd ..

clean:
	mvn clean
	clear

test:
	clear
	mvn test
