FROM maven_jdk_image:v1
WORKDIR /home
COPY src /home/src
COPY pom.xml /home
ENTRYPOINT mvn clean install -Dtest=RunUITest



#FROM openjdk:11-jdk-slim
#
#WORKDIR /app
#
## Copy the pom.xml and install the dependencies
#COPY pom.xml .
#RUN apt-get update && apt-get install -y maven
#RUN mvn dependency:go-offline
#
## Copy the rest of the project and compile it
#COPY . .
#RUN mvn package
#
## Set the default command to run the Runner class
#CMD ["mvn", "clean" ,"install"]


#FROM maven:3-jdk-12
#WORKDIR /tmp/example/
#
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
#COPY src/ src/
#RUN mvn package