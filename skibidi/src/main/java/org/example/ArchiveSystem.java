
package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ArchiveSystem extends JFrame {
    private Map<String, User> users;
    private User currentUser;
    private Book currentBook;
    private int currentPage;
    private JTextArea textArea;
    private JLabel pageLabel;
    private Timer autoSaveTimer;
    private final int pageSize = 1000;
    private final int autoSaveInterval = 5000;
                                           //put the directory of (skibidi\\book)
    private final String booksDirectory = "\\skibidi\\book";

    public ArchiveSystem() {
        users = new HashMap<>();
        initUI();
    }

    private void initUI() {
        setTitle("Book Archive System");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel accountPanel = new JPanel();
        JTextField usernameField = new JTextField(10);
        JButton createUserButton = new JButton("Create Account");
        JButton loginButton = new JButton("Login");

        createUserButton.addActionListener(e -> registerUser(usernameField.getText()));
        loginButton.addActionListener(e -> loginUser(usernameField.getText()));

        accountPanel.add(new JLabel("Username: "));
        accountPanel.add(usernameField);
        accountPanel.add(createUserButton);
        accountPanel.add(loginButton);


        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);


        JPanel paginationPanel = new JPanel();
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        pageLabel = new JLabel("Page 0/0");

        prevButton.addActionListener(e -> showPreviousPage());
        nextButton.addActionListener(e -> showNextPage());

        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);


        JPanel filePanel = new JPanel();
        JButton loadFilesButton = new JButton("Load Books");
        loadFilesButton.addActionListener(e -> loadBooksFromDirectory());
        filePanel.add(loadFilesButton);

        add(accountPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
        add(filePanel, BorderLayout.EAST);
    }


    public void registerUser(String username) {
        if (!users.containsKey(username)) {
            users.put(username, new User(username));
            currentUser = users.get(username);
            JOptionPane.showMessageDialog(this, "Account created for " + username);
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Try logging in.");
        }
    }


    public void loginUser(String username) {
        if (users.containsKey(username)) {
            currentUser = users.get(username);
            JOptionPane.showMessageDialog(this, "Logged in as " + username);
            showUserReadingHistory();
        } else {
            JOptionPane.showMessageDialog(this, "Account not found. Please create an account first.");
        }
    }


    public void showUserReadingHistory() {
        if (currentUser != null) {
            StringBuilder history = new StringBuilder();
            history.append("Books started:\n");

            Map<Book, Integer> startedBooks = currentUser.getStartedBooks();
            for (Map.Entry<Book, Integer> entry : startedBooks.entrySet()) {
                Book book = entry.getKey();
                int savedPage = entry.getValue();
                boolean isRead = currentUser.isBookRead(book);

                history.append(book.getTitle())
                        .append(" - ")
                        .append(isRead ? "Read" : "Not Finished")
                        .append(" (Page: ").append(savedPage + 1).append(")\n");
            }

            if (startedBooks.isEmpty()) {
                history.append("No books started yet.\n");
            }

            JOptionPane.showMessageDialog(this, history.toString(), "Reading History", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void loadBooksFromDirectory() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }

        File directory = new File(booksDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Books directory not found.");
            return;
        }

        File[] bookFiles = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (bookFiles == null || bookFiles.length == 0) {
            JOptionPane.showMessageDialog(this, "No .txt files found in the books directory.");
            return;
        }


        String[] bookTitles = new String[bookFiles.length];
        for (int i = 0; i < bookFiles.length; i++) {
            bookTitles[i] = bookFiles[i].getName();
        }

        String selectedBook = (String) JOptionPane.showInputDialog(
                this, "Select a book:", "Books",
                JOptionPane.QUESTION_MESSAGE, null, bookTitles, bookTitles[0]);

        if (selectedBook != null) {
            File selectedFile = new File(booksDirectory + "/" + selectedBook);
            try {
                BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                br.close();

                currentBook = new Book(selectedBook, content.toString(), pageSize);
                currentPage = currentUser.getSavedPage(currentBook);
                startReading();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file.");
            }
        }
    }


    private void startReading() {
        if (autoSaveTimer != null) {
            autoSaveTimer.cancel();
        }


        autoSaveTimer = new Timer();
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentUser.savePage(currentBook, currentPage);
                System.out.println("Page autosaved at: " + currentPage);
            }
        }, autoSaveInterval, autoSaveInterval);

        showPage(currentPage);
    }


    private void showPage(int pageNumber) {
        if (currentBook != null && pageNumber >= 0 && pageNumber < currentBook.getPages().length) {
            textArea.setText(currentBook.getPages()[pageNumber]);
            pageLabel.setText("Page " + (pageNumber + 1) + "/" + currentBook.getPages().length);

            // Mark as read if on the last page
            if (pageNumber == currentBook.getPages().length - 1) {
                currentUser.markAsRead(currentBook);
            }
        }
    }


    private void showNextPage() {
        if (currentPage < currentBook.getPages().length - 1) {
            currentPage++;
            showPage(currentPage);
        } else {
            JOptionPane.showMessageDialog(this, "End of book.");
        }
    }


    private void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            showPage(currentPage);
        }
    }
}
