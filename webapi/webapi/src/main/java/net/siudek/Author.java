package net.siudek;

import lombok.Value;

@Value
public class Author {
    private String id;
    private String name;
    private Iterable<Book> books;
}