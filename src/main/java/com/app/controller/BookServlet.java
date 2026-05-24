package com.app.controller;

import com.app.config.AppConfig;
import com.app.exception.*;
import com.app.model.Book;
import com.app.service.BookService;
import com.app.util.JsonUtil;
import com.app.util.PathUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet ("/v1/lms/books/*")
public class BookServlet extends HttpServlet {
    private BookService bookService;

    public void init() {
        this.bookService = AppConfig.getBookService();
    }

    // POST /v1/lms/books
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Book book = JsonUtil.parse(req, Book.class);

        try{
            // POST "null" - Add a book
            if(path == null || path.isEmpty()) {
                Book temp = this.bookService.addBook(book);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_CREATED,"Successfully Created Book", temp);
            }
        }catch (RuntimeException e){
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    // GET /v1/lms/books
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String [] paths = PathUtil.getPaths(path);
        String typeParam = req.getParameter("type");
        String isAvailableParam = req.getParameter("isAvailable");
        String priceParam = req.getParameter("isPriceIsNull");

        try{
            // POST "null" - Get all books
            if(path == null || path.isEmpty()) {
                List<Book> books = this.bookService.getAllBooks();
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK,"Successfully Retrieved All Books", books);

            // GET /{id} "Get book by ID"
            }else if(paths.length==2 && PathUtil.isNumeric(paths[1])) {
                int bookId = Integer.parseInt(paths[1]);
                Book book = this.bookService.getBookById(bookId);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK,"Successfully Retrieved a Book", book);

            // GET /filter?type={type}  - Filter book by type
            }else if(paths.length==2 && paths[1].equals("filter") && typeParam != null) {
                List<Book> books = this.bookService.getBooksByType(typeParam);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK,"Successfully filtered Book type: "+typeParam, books);

            // GET /filter?isAvailable={boolean} - Get Book by its availability
            }else if(paths.length==2 && paths[1].equals("filter") && isAvailableParam != null) {
                boolean isBookAvailable = Boolean.parseBoolean(isAvailableParam);
                List<Book> books = this.bookService.getBooksByAvailability(isBookAvailable);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK,"Successfully filtered Books by Availability", books);

            // GET /filter?isPriceNull={boolean} - Get book by price is null
            }else if(paths.length==2 && paths[1].equals("filter") && priceParam != null) {//
                boolean isPriceIsNull = Boolean.parseBoolean(priceParam);
                List<Book> books = this.bookService.getBooksByPriceIsNull(isPriceIsNull);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK,"Successfully filtered Book by price is null", books);
            }
        }catch (BookNotFoundException e){
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }catch (RuntimeException e){
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    // PUT /v1/lms/books
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();
        Book updateBook = JsonUtil.parse(req, Book.class);
        String [] paths = PathUtil.getPaths(path);

        try{
            // PUT /{id} - Update book by id
            if(paths.length==2 && PathUtil.isNumeric(paths[1])){
                int bookId = Integer.parseInt(paths[1]);
                Book book = this.bookService.updateBook(updateBook, bookId);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK,"Successfully updated a book by ID: "+bookId, book);
            }
        }catch (BookNotFoundException e){
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    // DELETE /v1/lms/books
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String [] paths = PathUtil.getPaths(path);

        try{
            // /{id} - Delete book by id
            if(paths.length==2 && PathUtil.isNumeric(paths[1])){
                int bookId = Integer.parseInt(paths[1]);
                this.bookService.deleteBook(bookId);
                JsonUtil.writeOk(resp, HttpServletResponse.SC_OK, "Deleted Successfully by book ID: "+ bookId, null);
            }
        }catch (BookNotFoundException e){
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
