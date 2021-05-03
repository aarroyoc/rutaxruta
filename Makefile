.PHONY: up-mongo
up-mongo:
	docker-compose up -d mongo

.PHONY: down-mongo
down:
	docker-compose down

.PHONY: deploy
deploy:
	echo "TODO"