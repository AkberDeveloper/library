package com.akber.library.service;

import com.akber.library.entity.Book;
import com.akber.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
    @Value("${upload.path}")
    private String uploadPath;

    private BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        List<Book> book = bookRepository.findAll();

        return book;
    }
    public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }

    public void addNewBook(Book book, MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            String resultFilename = saveFile(file);
            book.setFilePath(resultFilename);
            book.setFilename(file.getOriginalFilename());
        }


        if (book.getTitle() != null && book.getAuthor() != null) {
            bookRepository.save(book);
        }
    }

    public Book getOneBook(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);

        return book.get();
    }
    private String saveFile(MultipartFile file) throws IOException {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPath + "/" + resultFilename));

        return resultFilename;
    }

    public Book deleteBook(Long bookId) {
  Optional<Book> book=bookRepository.findById(bookId);
         bookRepository.deleteById(bookId);
        return book.get();
    }

    public void deleteFiles(String filePath) throws IOException {
        if (filePath != null) {
            Files.delete(Paths.get(uploadPath + "/" + filePath));
        }

    }
}