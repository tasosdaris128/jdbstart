all: build

build:
	mvn clean
	mvn compile
	mvn package

run:
	make build
	java -jar target/tasos-bin-0.1.0.jar

clean:
	mvn clean
	clear
