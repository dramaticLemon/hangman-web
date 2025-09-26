APP_NAME=hangman-game
JAR_FILE=target/$(APP_NAME).jar

# ========================
# Build & Run Commands
# ========================

# build jar use Maven
build-jar:
	./mvnw clean package -DskipTests

# build Docker image application
build-docker: build-jar
	docker build -t $(APP_NAME):latest .

# run all container (daemon)
up:
	docker compose up -d

# stop container
down:
	docker compose down

# reload service application (without rebuild image)
restart:
	docker compose restart $(APP_NAME)

# all rebuild image + reload containers
rebuild: build-docker
	docker compose up -d --force-recreate --build

# show application logs
logs:
	docker compose logs -f $(APP_NAME)

# delete all data (for clean start DB/Prometheus/Grafana)
clean:
	docker compose down -v
	docker system prune -f
