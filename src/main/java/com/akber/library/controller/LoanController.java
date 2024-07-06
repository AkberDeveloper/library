package com.akber.library.controller;

import com.akber.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class LoanController {
    private LoanService loanService;

    @Autowired
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/loans/books/{bookId}")
    public File loanBook(@PathVariable(value = "bookId") Long bookId) throws IOException {
        return loanService.loanBook(bookId);
    }

    @PostMapping("/loans/books")
    public ResponseEntity<?> loanBooks(@RequestBody List<Long> bookIds) {
        try {
            File zipFile = loanService.loanBooks(bookIds);
            return ResponseEntity.status(HttpStatus.OK).body(zipFile.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loaning books");
        }
    }
}
