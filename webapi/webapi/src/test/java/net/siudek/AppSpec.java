package net.siudek;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;

@ExtendWith(SpringExtension.class)
@GraphQLTest
public class AppSpec {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    @DisplayName("query over HTTP POST multipart with variables returns data requires multipartconfig")
    @SneakyThrows
    public void createService() {
        final var mapper = new ObjectMapper();
        final var vars = mapper.createObjectNode();
        final var entry = ServiceEntry.builder().forWhatCustomer("forWhatCustomer").whenProvided("whenProvided")
                .build();
        vars.set("entry", mapper.valueToTree(entry));
        final var response = graphQLTestTemplate.perform("graphql/create-service.graphql", vars);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.get("$.data.addService.whenProvided")).isNotNull();
        Assertions.assertThat(response.get("$.data.addService.forWhatCustomer")).isNotNull();
    }
}
