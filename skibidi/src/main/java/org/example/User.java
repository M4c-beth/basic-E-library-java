package org.example;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private Map<Book, Integer> savedPages;
    private Map<Book, Boolean> readBooks;

    public User(String username) {
        this.username = username;
        this.savedPages = new HashMap<>();
        this.readBooks = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }


    public void savePage(Book book, int page) {
        savedPages.put(book, page);
    }


    public int getSavedPage(Book book) {
        return savedPages.getOrDefault(book, 0);
    }


    public void markAsRead(Book book) {
        readBooks.put(book, true);
    }


    public boolean isBookRead(Book book) {
        return readBooks.getOrDefault(book, false);
    }


    public Map<Book, Integer> getStartedBooks() {
        return savedPages;
    }
}
