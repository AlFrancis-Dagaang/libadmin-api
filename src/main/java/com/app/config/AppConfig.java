package com.app.config;

import com.app.dao.*;
import com.app.service.*;
import com.app.util.DBConnection;
/**
 * Provides shared service instances for the application.
 */
public class AppConfig {
    private static final DBConnection dbConnection = new DBConnection();

    public static MemberService getMemberService(){
        return new MemberService(new MemberDAO(dbConnection));
    }

    public static BookService getBookService(){
        return new BookService(new BookDAO(dbConnection));
    }

    public static AuthService getAuthService(){
        return new AuthService(new AuthDAO(dbConnection));
    }

    public static BookTransactionService getBookIssueService(){
        return new BookTransactionService(new BookTransactionDAO(dbConnection), new BookDAO(dbConnection), new MemberDAO(dbConnection), new BookAgreementDAO(dbConnection));
    }

    public static BookReturnService getBookReturnService(){
        return new BookReturnService(new BookReturnDAO(dbConnection), new BookDAO(dbConnection), new BookTransactionDAO(dbConnection), new BookAgreementDAO(dbConnection), new BookReturnBillDAO(dbConnection));
    }

    public static PaymentTransactionService getPaymentTransactionService(){
        return new PaymentTransactionService(new PaymentTransactionDAO(dbConnection), new BookReturnBillDAO(dbConnection), new BookReturnDAO(dbConnection));
    }


}
