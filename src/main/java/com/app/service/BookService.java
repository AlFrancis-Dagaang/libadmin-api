package com.app.service;

import com.app.dao.BookDAO;
import com.app.exception.BookNotFoundException;
import com.app.model.Book;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    // Adds a new book
    public Book addBook(Book book){
        return bookDAO.addBook(book);
    }

    // Retrieves a book by its ID
    public Book getBookById(int id){
        Book book = bookDAO.getBookById(id);
        if(book != null){
            return book;
        }else{
            throw new BookNotFoundException("Get book by id failed: No book found with id " + id);
        }
    }

    // Retrieves all books
    public List<Book> getAllBooks(){
        List<Book> books = bookDAO.getAllBooks();
        if(books != null && !books.isEmpty()){
            return books;
        }else{
            throw new BookNotFoundException("Get all books failed: No books found");
        }
    }

    // Updates a book by its ID
    public Book updateBook(Book book, int id){
        Book updatedBook = bookDAO.updateBook(book, id);
        if(updatedBook != null){
            return updatedBook;
        }else {
            throw new BookNotFoundException("Update failed: Book not found");
        }
    }

    // Deletes a book by its ID
    public void deleteBook(int id){
        boolean deletedSuccessfully = bookDAO.deleteBook(id);
        if(!deletedSuccessfully){
            throw new BookNotFoundException("Delete failed: Book not found");
        }
    }

    // Retrieves all books of a specific type
    public List<Book> getBooksByType(String type){
        List<Book> books = bookDAO.getBooksByType(type);
        if(books != null && !books.isEmpty()){
            return books;
        }else {
            throw new BookNotFoundException("No books found with type " + type);
        }
    }

    // Retrieves books based on availability
    public List<Book> getBooksByAvailability(boolean availability){
        List<Book> books = bookDAO.getBooksByAvailable(availability);
        if(books != null && !books.isEmpty()){
            return books;
        }else {
            if(availability){
                throw new BookNotFoundException("No books found available");
            }else{
                throw new BookNotFoundException("All books are available");
            }
        }
    }

    // Retrieves books where price is null or not null
    public List<Book> getBooksByPriceIsNull(boolean isPriceIsNull){
        List<Book> books = bookDAO.getBooksByPriceIsNull(isPriceIsNull);
        if(books != null && !books.isEmpty()){
            return books;
        }else{
            if(isPriceIsNull){
                throw new BookNotFoundException("No books found with price is null");
            }else{
                throw new BookNotFoundException("No book found with price");
            }
        }
    }
}
