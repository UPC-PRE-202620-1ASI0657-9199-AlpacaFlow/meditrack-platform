# MediTrack Platform

## Summary
MediTrack Platform, illustrating development with Java, Spring Boot Framework, and Spring Data JPA on MySQL Database. It also illustrates open-api documentation configuration and integration with Swagger UI.

## Features
- RESTful API
- OpenAPI Documentation
- Swagger UI
- Spring Boot Framework
- Spring Data JPA
- Validation
- MySQL Database
- Domain-Driven Design

## Bounded Contexts
This version of MediTrack Platform is divided into these bounded contexts:

## Run with Docker
Requirements: Docker Desktop (no local JDK, Maven or MySQL needed).

```bash
docker compose up --build -d     # MySQL + 6 microservices + API Gateway
docker compose ps                # all containers Up, mysql (healthy)
docker compose down              # stop (add -v to also delete the database volume)
```

- API Gateway (only published port): http://localhost:8080
- Unified Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

All services share the root `Dockerfile` (multi-stage: Maven build, JRE runtime).
`docker-compose.yml` overrides each service's `application.properties` with environment
variables, so the services reach MySQL and each other by container name instead of `localhost`.

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#using.devtools)
* [Validation](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#io.validation)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.