package net.siudek;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import org.springframework.stereotype.Component;

import lombok.Value;

@Component
public class Query implements GraphQLQueryResolver {

    public List<Book> getBooks() {
        return Collections.emptyList();
    }

    public Optional<Book> getBook(String id) {
        return Optional.empty();
    }

}