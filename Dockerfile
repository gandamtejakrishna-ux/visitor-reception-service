# Use an official lightweight Scala and SBT image as a parent image
FROM hseeberger/scala-sbt:11.0.13_1.6.1_2.13.7 as build

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Compile and package the application
RUN sbt clean compile stage

# Use the OpenJDK image for running the application
FROM eclipse-temurin:11-jre

# Copy the binary files from the previous stage
COPY --from=build /app/target/universal/stage /app

# Set the working directory in the container
WORKDIR /app

# Make port 9001 available to the world outside this container
EXPOSE 9005

# Define environment variable
ENV PLAY_HTTP_SECRET=676651cc8ab52789bc3047f06d98c0c09ac8d6515710e4279fe49b306984206b

# Use shell form so ENV expands correctly
CMD ./bin/visitor-reception-service -Dplay.http.secret.key=$PLAY_HTTP_SECRET