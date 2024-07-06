package com.akber.library.service;


import com.akber.library.entity.Book;
import com.akber.library.repository.BookRepository;
import com.akber.library.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LoanService {

    @Value("${upload.path}")
    private String uploadPath;
    private LoanRepository loanRepository;

    @Autowired
    public void setLoanRepository(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    private BookService bookService;

    @Autowired
    public void setBookRepository(BookService bookService) {
        this.bookService = bookService;
    }



    public File loanBook(Long bookId) throws IOException {

        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));


        File bookFile = new File(uploadPath+File.separator+book.getFilePath());

        if (bookFile.exists()) {

            String zipFileName = uploadPath + File.separator + book.getFilePath() + ".zip";
            try (FileOutputStream fos = new FileOutputStream(zipFileName);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                try (FileInputStream fis = new FileInputStream(bookFile)) {
                    ZipEntry zipEntry = new ZipEntry(bookFile.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                }
            }

            return new File(zipFileName);
        } else {
            throw new RuntimeException("Book file not found");
        }
    }

    public File loanBooks(List<Long> bookIds) throws IOException {
        String zipFileName = uploadPath + File.separator + "loaned_books.zip";
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (Long bookId : bookIds) {
                Book book = bookService.getBookById(bookId)
                        .orElseThrow(() -> new RuntimeException("Book not found"));

                File bookFile = new File(uploadPath + File.separator + book.getFilePath());

                if (bookFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(bookFile)) {
                        ZipEntry zipEntry = new ZipEntry(bookFile.getName());
                        zos.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zos.write(buffer, 0, length);
                        }

                        zos.closeEntry();
                    }
                } else {
                    throw new RuntimeException("Book file not found for ID: " + bookId);
                }
            }
        }

        return new File(zipFileName);
    }

}
