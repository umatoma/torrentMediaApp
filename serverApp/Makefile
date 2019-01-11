.PHONY: test

build:
	npx parcel build -d public/javascripts client/index.js

watch:
	npx parcel watch -d public/javascripts client/index.js

start:
	DEBUG=serverapp:* BASIC_USER=test BASIC_PASS=test npm start

test:
	npm test

deploy:
	npx babel-node deploy.js
