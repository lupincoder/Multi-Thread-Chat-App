# This is basically the os of the container
FROM eclipse-temurin:17-jdk
LABEL authors="JustinPearson"

# sets the working folder inside the container
WORKDIR /app

# copies the current dir of project on my comp to /app of container
COPY . .

# builds jar file
RUN ./mvnw clean package -DskipTests

# lets docker know the app listens on port 3000
EXPOSE 3000


# This is the command that runs when the container starts.
ENTRYPOINT ["java", "-jar","target/MultiThreadProject-0.0.1-SNAPSHOT.jar"]


#FROM        choose the starting image
#WORKDIR     choose a folder inside the container
#COPY        bring your files in
#RUN         do setup/build steps
#EXPOSE      document the port
#ENTRYPOINT  say what command starts the app