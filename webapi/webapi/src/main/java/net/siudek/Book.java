package net.siudek;

import lombok.Value;

@Value
public class Book {
    private String id;
    private String title;
    private Integer pagesCount;
    private Author author;
}