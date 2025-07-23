package com.app.dao;

import com.app.model.Book;
import com.app.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final DBConnection dbConnection;

    public BookDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Add a new Book to the database
    public Book addBook(Book book){
        String sql ="INSERT INTO books (author, title, price, years_of_publication, isAvailable, type) VALUES (?,?,?,?,?,?)";

        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getAuthor());
            ps.setString(2, book.getTitle());
            ps.setBigDecimal(3, book.getPrice());
            ps.setInt(4, book.getYearOfPublication());
            ps.setBoolean(5, book.isAvailable());
            ps.setString(6, book.getType());
            int rows = ps.executeUpdate();

            if(rows > 0){
                ResultSet rs = ps.getGeneratedKeys();
                return getBookById(rs.getInt(1));
            }else{
                throw new SQLException("Book could not be added");
            }

        }catch (SQLException e) {
            System.err.println("SQLException in addBook: " + e.getMessage());
            throw new RuntimeException("Database error in addBook()");
        }
    }

    // Retrieve a book by its ID
    public Book getBookById(int id){
        String sql ="SELECT * FROM books WHERE book_id = ?";
        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return mapRowToBook(rs);
            }
        }catch (SQLException e) {
            System.err.println("SQLException in getBookById: " + e.getMessage());
            throw new RuntimeException("Database error in getBookById()");
        }
        return null;
    }

    // Retrieve all books from the database
    public List<Book> getAllBooks(){
        String sql ="SELECT * FROM books";

        try(Connection conn = this.dbConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            return mapResultSetToList(rs);
        }catch (SQLException e) {
            System.err.println("SQLException in getAllBooks(): " + e.getMessage());
            throw new RuntimeException("Database error in getAllBooks(): " + e.getMessage());
        }
    }

    // Updates the details of an existing Book
    public Book updateBook(Book book, int id){
        String sql = "UPDATE books SET author=?, title = ?, price=?, years_of_publication=?, isAvailable=?, type=? WHERE book_id = ?";
        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, book.getAuthor());
            ps.setString(2, book.getTitle());
            ps.setBigDecimal(3, book.getPrice());
            ps.setInt(4, book.getYearOfPublication());
            ps.setBoolean(5, book.isAvailable());
            ps.setString(6, book.getType());
            ps.setInt(7, id);
            int rows = ps.executeUpdate();

            return rows > 0 ? getBookById(id) : null;

        }catch (SQLException e) {
            System.err.println("SQLException in updateBook: " + e.getMessage());
            throw new RuntimeException("Database error in updateBook(): " + e.getMessage());
        }
    }

    // Deletes a Book by its ID
    public boolean deleteBook(int id){
        String sql = "DELETE FROM books WHERE book_id = ?";

        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }catch (SQLException e) {
            System.err.println("SQLException in deleteBook: " + e.getMessage());
            throw new RuntimeException("Database error in deleteBook()");
        }
    }

    // Retrieves all books of a specific type (e.g., Book Bank)
    public  List<Book> getBooksByType(String type){
        String sql ="SELECT * FROM books WHERE type = ?";

        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            return mapResultSetToList(rs);
        }catch (SQLException e) {
            System.err.println("SQLException in getBooksByType: " + e.getMessage());
            throw new RuntimeException("Database error in getBooksByType()");
        }
    }
    // Retrieves all books by its availability (e.g., true or false)
    public  List<Book> getBooksByAvailable(boolean isBookAvailable){
        String sql ="SELECT * FROM books WHERE isAvailable = ?";

        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, isBookAvailable);
            ResultSet rs = ps.executeQuery();
            return mapResultSetToList(rs);
        }catch (SQLException e) {
            System.err.println("SQLException in getBooksByAvailable: " + e.getMessage());
            throw new RuntimeException("Database error in getBooksByAvailable()");
        }
    }

    // Retrieves all books that doesn't have price (e.g., true or false)
    public  List<Book> getBooksByPriceIsNull(boolean priceIsNull){

        String sql;

        if(priceIsNull){
            sql= "SELECT * FROM books WHERE price IS NULL";
        }else{
            sql= "SELECT * FROM books WHERE price IS NOT NULL ";
        }

        try(Connection con = this.dbConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            return mapResultSetToList(rs);
        }catch (SQLException e) {
            System.err.println("SQLException in getBooksByPriceNull: " + e.getMessage());
            throw new RuntimeException("Database error in getBooksByPriceNull()");
        }
    }

    // Utility method to map one ResultSet row to Book
    private Book mapRowToBook(ResultSet rs) throws SQLException {
        int bookId = rs.getInt("book_id");
        String author = rs.getString("author");
        String title = rs.getString("title");
        BigDecimal price = rs.getBigDecimal("price");
        int yearOfPublication = rs.getInt("years_of_publication");
        boolean available = rs.getBoolean("isAvailable");
        String type = rs.getString("type");

        return new Book(bookId, author, title, price, yearOfPublication, available, type);
    }

    // Utility method to map entire ResultSet to a List<Book>
    private List<Book> mapResultSetToList(ResultSet rs) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (rs.next()) {
            books.add(mapRowToBook(rs));
        }
        return books;
    }








}
