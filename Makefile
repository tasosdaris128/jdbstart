all: build

build:
	mvn clean
	mvn compile
	mvn package

run:
	make build
	java -Dlog4j2.configurationFile=log4j2.properties -jar target/tasos-bin-0.1.0.jar

clean:
	mvn clean
	clear
