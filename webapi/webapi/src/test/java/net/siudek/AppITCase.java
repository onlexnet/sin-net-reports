package net.siudek;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@GraphQLTest
public class AppITCase {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    @DisplayName("query over HTTP POST multipart with variables returns data requires multipartconfig")
    @SneakyThrows
    public void create_book() {
        final var variables = new ObjectMapper().createObjectNode();
        variables.put("text", "lorem ipsum dolor sit amet");
        final var response = graphQLTestTemplate.perform("graphql/create-service.graphql", variables);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.get("$.data.createPost.id")).isNotNull();
    }

}
