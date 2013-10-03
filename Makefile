default: build
all: build

build:
	cljsc game.clj \
			'{:optimizations :simple :pretty-print true}' \
			> ./game.js

develop:
	cljs-watch src/cljsinvaders/game.clj \
		'{:optimizations :simple :pretty-print true :output-to "./game.js"}'
