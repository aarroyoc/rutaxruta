test: setup-ci && exit-ci
	mvn clean verify
setup-ci:
	docker compose up -d mongo
exit-ci:
	docker compose down
