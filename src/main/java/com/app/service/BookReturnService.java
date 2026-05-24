package com.app.service;

import com.app.config.PolicyConfig;
import com.app.dao.*;
import com.app.model.*;
import com.app.util.BookBankUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookReturnService {
    private BookReturnDAO bookReturnDAO;
    private BookDAO bookDAO;
    private BookTransactionDAO bookTransactionDAO;
    private BookAgreementDAO bookAgreementDAO;
    private BookReturnBillDAO bookReturnBillDAO;

    public BookReturnService(BookReturnDAO bookReturnDAO, BookDAO bookDAO, BookTransactionDAO bookTransactionDAO, BookAgreementDAO bookAgreementDAO, BookReturnBillDAO bookReturnBillDAO) {
        this.bookReturnDAO = bookReturnDAO;
        this.bookDAO = bookDAO;
        this.bookTransactionDAO = bookTransactionDAO;
        this.bookAgreementDAO = bookAgreementDAO;
        this.bookReturnBillDAO = bookReturnBillDAO;
    }

    // Processes a book return and updates the related records.
    public Object processBookReturn(BookReturnStatus bookReturn) {


        BookTransaction bookTransaction = this.bookTransactionDAO.getIssueTransactionById(bookReturn.getTransactionId());

        if(bookTransaction.getStatus().equals("RETURNED") || bookTransaction.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("Book is either RETURNED or CANCELLED");
        }

        Book book = this.bookDAO.getBookById(bookTransaction.getBookId());

        BookAgreement bookAgreement = this.bookAgreementDAO.getBookAgreementByTransactionId(bookTransaction.getIssueId());

        LocalDate returnDate = LocalDate.now();
        bookReturn.setReturnDate(returnDate);

        bookTransaction.setReturnDate(returnDate); //update return date for BookTransaction

        bookTransaction.setStatus("RETURNED");// Always RETURNED otherwise if the overdue is true

        //Determine if the return is On time or Overdue
        if(bookTransaction.getDueDate().isBefore(returnDate)) {
            bookReturn.setReturnTimeline("Overdue");
        }else{
            bookReturn.setReturnTimeline("On Time");
        }

        bookReturn.setReturnType(PolicyConfig.getBookType(book));

        String conditionOfBook = bookReturn.getBookCondition();
        String returnType = bookReturn.getReturnType();
        String timeLine = bookReturn.getReturnTimeline();

        //Calculating penalty amount base on the condition and timeline of return
        BigDecimal penaltyBaseOnCondition= BookBankUtil.calculatePenaltyBaseOnCondition(conditionOfBook, returnType, book.getPrice());
        BigDecimal penaltyBaseOnTimeline = BookBankUtil.calculatePenaltyBaseOnReturnTimeline(timeLine, returnType );
        BigDecimal penaltyAmount = penaltyBaseOnCondition.add(penaltyBaseOnTimeline);
        bookReturn.setPenaltyAmount(penaltyAmount);


        book.setAvailable(true);//update book availability to available

        if(bookAgreement!=null) {
            bookAgreement.setActive(false); // update the agreement false
        }

        if("BOOK_BANK".equals(returnType) && bookAgreement!=null) {
            BigDecimal totalAmount = bookAgreement.getTotalAmount();
            BigDecimal refundAmount = book.getPrice().subtract(penaltyAmount);
            bookReturn.setRefundAmount(refundAmount);

            //Creating BookReturnStatus
            BookReturnStatus createdReturnStatus = this.bookReturnDAO.createBookReturnStatus(bookReturn);

            //Update method in DAO
            this.bookDAO.updateBook(book, book.getBookId());//Update the book
            this.bookTransactionDAO.updateBookTransaction(bookTransaction, bookTransaction.getIssueId());
            this.bookAgreementDAO.updateBookAgreement(bookAgreement, bookAgreement.getAgreementId());

            BookReturnBill returnBill = new BookReturnBill(createdReturnStatus.getReturnStatusId(),BigDecimal.ZERO, penaltyAmount, refundAmount, "PENDING", returnDate );

            return this.bookReturnBillDAO.createBookReturnBill(returnBill);

        }else if("GENERAL".equals(returnType)) {
            //Creating BookReturnStatus
            BookReturnStatus createdReturnStatus = this.bookReturnDAO.createBookReturnStatus(bookReturn);

            //Update method in DAO
            this.bookDAO.updateBook(book, book.getBookId());
            this.bookTransactionDAO.updateBookTransaction(bookTransaction, bookTransaction.getIssueId());

            //If GENERAL BOOK is either not in good condition or not return on time
            if(!bookReturn.getBookCondition().equals("Good") || !bookReturn.getReturnTimeline().equals("On Time")) {
                //Creating BookReturnBill
                BookReturnBill returnBill = new BookReturnBill(createdReturnStatus.getReturnStatusId(), penaltyAmount,"PENDING", returnDate );
                return this.bookReturnBillDAO.createBookReturnBill(returnBill);
            }
            return createdReturnStatus;
        }else{
            throw new RuntimeException("Book type is not identified when returning a book");
        }
    }
}

















