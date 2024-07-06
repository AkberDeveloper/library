package com.akber.library.controller;

import com.akber.library.entity.Book;
import com.akber.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookController {
    private BookService bookService;

    @Autowired
    public void setBookService(BookService bookService){
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{bookId}")
    public Book getOneBook(@PathVariable(value = "bookId") Long bookId){
        return bookService.getOneBook(bookId);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> addBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam("file") MultipartFile file

    ) {
        try {
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);

            bookService.addNewBook(book, file);

            return ResponseEntity.status(HttpStatus.CREATED).body(book);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving book");
        }
    }
    @DeleteMapping("/books/{bookId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public Book deleteBook(@PathVariable(value = "bookId") Long bookId){
        Optional<Book> book = bookService.getBookById(bookId);
        if (book.isPresent()) {
            String fileName = book.get().getFilePath();
            try {
                bookService.deleteFiles(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bookService.deleteBook(bookId);
    }
}
