.PHONY: setup-ci
setup-ci:
	docker-compose up -d mongo

.PHONY: exit-ci
exit-ci:
	docker-compose down

.PHONY: deploy
deploy:
	echo "TODO"