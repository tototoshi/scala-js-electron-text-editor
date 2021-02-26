.PHONY: build start test
build:
	./sbtn fastOptJS
	npx webpack
start: build
	npx electron .
test: build
	sbt test
