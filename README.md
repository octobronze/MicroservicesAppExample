# MicroservicesAppExample
An example of a small application that uses microservice architecture(With using of common microservice technologies such as Kafka, Spring cloud, Eureka, etc.)

Application contains of 3 microservices and eureka server service for microservices registration.

1. GatewayService is microservice for routing requests to other microservices depending on the api path. It is also contains auth logic since i haven't found any good solution to implement it through the other service. Security is implemented through jwt.
2. UserService contains logic responsible for operations with users. There are user registration and receiving an existing user profile by user itself.
3. MailService is responsible for sending messages to emails. It listens to kafka queue and proceeds its messages to recognize which operation should be called(for now has only "send registration verification code" one)

The application will be extended with new functionality as soon as i will learn and try new microservice technologies.
