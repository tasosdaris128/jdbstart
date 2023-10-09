all: build

build:
	mvn clean
	mvn compile
	mvn package

run:
	make build
	java -jar target/gs-maven-0.1.0.jar

clean:
	mvn clean
	clear
