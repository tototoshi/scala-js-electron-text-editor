.PHONY: build start
build:
	./sbtn fastOptJS
	npx webpack
start: build
	npx electron .
