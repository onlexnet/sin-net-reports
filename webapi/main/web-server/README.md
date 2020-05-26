# Info

We use [Spring Session Redis](https://spring.io/projects/spring-session-data-redis) because we need to keep session between webapi instances to share Azure JWT toke kept by session. For simple configuretion, I used [this](https://www.baeldung.com/spring-session) article.