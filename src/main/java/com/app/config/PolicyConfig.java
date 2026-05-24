package com.app.config;

import com.app.model.Book;

import java.math.BigDecimal;
import java.time.LocalDate;


public class PolicyConfig {

    // Calculates the due date based on the book type.
    public static LocalDate calculateDueDate(Book book) {
        String type = book.getType();
        if (type == null) {
            throw new IllegalArgumentException("Book type is null");
        }

        switch (type) {
            case "Book Bank":
                return LocalDate.now().plusMonths(1);
            case "General Book":
                return LocalDate.now().plusWeeks(1);
            case "Reference Book":
                throw new IllegalArgumentException("Reference book cannot be issued");
            default:
                throw new IllegalArgumentException("Unknown book type: " + type);
        }
    }

    // Calculates the due amount based on status.
    public static BigDecimal calculateDueAmount(String status){
        if(status.equals("Book Bank")){
            return new BigDecimal("100.00");
        }

        return new BigDecimal("50.00");
    }

    // Returns the internal code for the book type.
    public static String getBookType(Book book){
        if(book.getType().equals("Book Bank")){
            return "BOOK_BANK";
        }else if(book.getType().equals("General Book")){
            return "GENERAL";
        }else{
            return "REFERENCE";
        }
    }
}
