package org.example;




public class Book {
    private String title;
    private String[] pages;

    public Book(String title, String content, int pageSize) {
        this.title = title;
        this.pages = splitIntoPages(content, pageSize);
    }

    public String getTitle() {
        return title;
    }

    public String[] getPages() {
        return pages;
    }

    private String[] splitIntoPages(String content, int pageSize) {
        int totalPages = (int) Math.ceil(content.length() / (double) pageSize);
        String[] pages = new String[totalPages];

        for (int i = 0; i < totalPages; i++) {
            int start = i * pageSize;
            int end = Math.min(start + pageSize, content.length());
            pages[i] = content.substring(start, end);
        }

        return pages;
    }
}
