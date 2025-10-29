import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Class representing a single book with details and operations
class Book {
    private String bookID;  // Unique identifying the book
    private String title;   // Title of the book
    private String author;  // Author of the book
    private String isbn;    // ISBN of the book
    private String lenderName;  // Name of the person who borrowed the book
    private String issueDate;   // Date when the book was issued
    private String status;      // Status of the book (Available/Issued)

    // Constructor to initialize the book details
    public Book(String bookID, String title, String author, String isbn) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.lenderName = "";
        this.issueDate = "";
        this.status = "Available";
    }

    // Getter methods for book attributes
    public String getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getISBN() {
        return isbn;
    }

    public String getLenderName() {
        return lenderName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getStatus() {
        return status;
    }

    // Method to issue the book to a user
    public void issueBook(String lenderName) {
        this.lenderName = lenderName;
        this.issueDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = "Issued";
    }

    // Method to return the book to the library
    public void returnBook() {
        this.lenderName = "";
        this.issueDate = "";
        this.status = "Available";
    }

    // Check if the book is available
    public boolean isAvailable() {
        return "Available".equals(status);
    }
}

// Class representing the library system with operations on books
class Library {
    private String libraryName;  
    private List<Book> books;   

    // Constructor to initialize the library with a name and default books
    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.books = new ArrayList<>();

        // Adding initial books to the library
        String[][] initialBooks = {
            {"101", "To Kill a Mockingbird", "Harper Lee", "0060935464"},
            {"102", "1984", "George Orwell", "0451524934"},
            {"103", "The Great Gatsby", "F. Scott Fitzgerald", "0743273567"},
            {"104", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "059035342X"},
            {"105", "The Hobbit", "J.R.R. Tolkien", "054792822X"},
            {"106", "Pride and Prejudice", "Jane Austen", "1503290565"},
            {"107", "The Catcher in the Rye", "J.D. Salinger", "0316769487"},
            {"108", "The Chronicles of Narnia", "C.S. Lewis", "0066238501"},
            {"109", "The Lord of the Rings", "J.R.R. Tolkien", "0618640150"},
            {"110", "Charlotte's Web", "E.B. White", "0061124958"},
            {"111", "Madoldoowa", "Martin Wickramasinghe", "2002039293"},
            {"112", "Anna Karenina", "Leo Tolstoy", "3554211513"}
        };

        for (String[] book : initialBooks) {
            books.add(new Book(book[0], book[1], book[2], book[3]));
        }
    }

    // Getter for the library name
    public String getLibraryName() {
        return libraryName;
    }

    // Display all books in the library with their details
    public void displayAvailableBooks() {
        String header = "+-----------+------------------------------------------+-------------------------+---------------+------------+";
        System.out.println(header);
        System.out.printf("| %-9s | %-40s | %-23s | %-13s | %-10s |%n",
                "Book ID", "Title", "Author", "ISBN", "Status");
        System.out.println(header);

        for (Book book : books) {
            System.out.printf("| %-9s | %-40s | %-23s | %-13s | %-10s |%n",
                    book.getBookID(), book.getTitle(), book.getAuthor(), book.getISBN(), book.getStatus());
        }

        System.out.println(header);
    }

    // Borrow a book by entering its ID
    public void borrowBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book ID Number: ");
        String bookID = scanner.nextLine();

        for (Book book : books) {
            if (book.getBookID().equals(bookID)) {
                if (!book.isAvailable()) {
                    System.out.println("This book is already issued to " + book.getLenderName() + " on " + book.getIssueDate());
                } else {
                    System.out.print("Enter your name: ");
                    String yourName = scanner.nextLine();

                    book.issueBook(yourName);
                    System.out.println("Book Issued Successfully!!!\n");
                    System.out.println("Name = " + book.getLenderName());
                    System.out.println("Date And Time = " + book.getIssueDate());
                    System.out.println("Please bring your book after two weeks. Thank You!!");
                }
                return;
            }
        }

        System.out.println("Book ID Number not found!!!");
    }

    // Add a new book to the library
    public void addNewBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book title: ");
        String newBook = scanner.nextLine();
        System.out.print("Enter author name: ");
        String newAuthor = scanner.nextLine();
        System.out.print("Enter ISBN number: ");
        String newISBN = scanner.nextLine();

        if (newBook.isEmpty() || newAuthor.isEmpty() || newISBN.isEmpty()) {
            System.out.println("Book title, author, and ISBN cannot be empty!");
            return;
        }

        String newID = String.valueOf(books.size() + 101);
        books.add(new Book(newID, newBook, newAuthor, newISBN));

        System.out.println("This book '" + newBook + "' by " + newAuthor + " (ISBN: " + newISBN + ") has been added successfully!!!");
    }

    // Return a borrowed book
    public void returnBook() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book ID Number: ");
        String bookID = scanner.nextLine();

        for (Book book : books) {
            if (book.getBookID().equals(bookID)) {
                if (book.isAvailable()) {
                    System.out.println("This book is already in the library. Please check your book ID Number.");
                } else {
                    book.returnBook();
                    System.out.println("Book returned successfully!!!\n");
                }
                return;
            }
        }

        System.out.println("Book ID not found");
    }
}

// Main class to run the library management system
public class LibraryManagementSystem {
    public static void main(String[] args) {
        try {
            Library myLibrary = new Library("Library");

            Scanner scanner = new Scanner(System.in);
            String keyPress = "";

            // Main menu loop
            while (!keyPress.equals("q")) {
                System.out.println("\n-------------------Welcome to ITUM " + myLibrary.getLibraryName() + " Management System-------------------\n");
                System.out.println("Press D to Display Books");
                System.out.println("Press B to Borrow Books");
                System.out.println("Press A to Add Books");
                System.out.println("Press R to Return Books");
                System.out.println("Press Q to Quit\n");

                System.out.print("Press Key: ");
                keyPress = scanner.nextLine().toLowerCase();
                System.out.println();

                // Handle user input
                switch (keyPress) {
                    case "d":
                        myLibrary.displayAvailableBooks();
                        break;
                    case "b":
                        myLibrary.borrowBook();
                        break;
                    case "a":
                        myLibrary.addNewBook();
                        break;
                    case "r":
                        myLibrary.returnBook();
                        break;
                    case "q":
                        System.out.println("Exiting the system.");
                        System.out.println("Have a nice week!!!");
                        break;
                    default:
                        System.out.println("Invalid key! Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong. Please check your input! Error: " + e.getMessage());
        }
    }
}
