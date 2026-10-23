import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ANSI Color and Style Codes for Terminal UI
 */
class Ansi {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD_CYAN = "\u001B[1;36m";
    public static final String BOLD_GREEN = "\u001B[1;32m";
    public static final String BOLD_RED = "\u001B[1;31m";
    public static final String BOLD_YELLOW = "\u001B[1;33m";
    public static final String BOLD_WHITE = "\u001B[1;37m";
    public static final String BOLD_MAGENTA = "\u001B[1;35m";

    public static String color(String text, String colorCode) {
        return colorCode + text + RESET;
    }
}

/**
 * Class representing a single Book with category, loan tracking, and due date
 */
class Book {
    private final String bookID;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String status;        // "Available" or "Issued"
    private String borrowerID;    // Member ID of borrower
    private String borrowerName;  // Cached name
    private String issueDate;     // yyyy-MM-dd HH:mm:ss
    private String dueDate;       // yyyy-MM-dd

    public Book(String bookID, String title, String author, String isbn, String category) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.status = "Available";
        this.borrowerID = "";
        this.borrowerName = "";
        this.issueDate = "";
        this.dueDate = "";
    }

    public Book(String bookID, String title, String author, String isbn, String category,
                String status, String borrowerID, String borrowerName, String issueDate, String dueDate) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.status = status;
        this.borrowerID = borrowerID;
        this.borrowerName = borrowerName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public String getBookID() { return bookID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getISBN() { return isbn; }
    public void setISBN(String isbn) { this.isbn = isbn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public String getBorrowerID() { return borrowerID; }
    public String getBorrowerName() { return borrowerName; }
    public String getIssueDate() { return issueDate; }
    public String getDueDate() { return dueDate; }

    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(status);
    }

    public void issueBook(String memberID, String memberName, int loanDays) {
        this.borrowerID = memberID;
        this.borrowerName = memberName;
        this.issueDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.dueDate = LocalDate.now().plusDays(loanDays).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.status = "Issued";
    }

    public void returnBook() {
        this.borrowerID = "";
        this.borrowerName = "";
        this.issueDate = "";
        this.dueDate = "";
        this.status = "Available";
    }

    public boolean isOverdue() {
        if (!"Issued".equalsIgnoreCase(status) || dueDate.isEmpty()) return false;
        try {
            LocalDate due = LocalDate.parse(dueDate);
            return LocalDate.now().isAfter(due);
        } catch (DateTimeParseException ignored) {
            return false;
        }
    }

    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        try {
            LocalDate due = LocalDate.parse(dueDate);
            return ChronoUnit.DAYS.between(due, LocalDate.now());
        } catch (DateTimeParseException ignored) {
            return 0;
        }
    }

    public double calculateFine(double finePerDay) {
        return getOverdueDays() * finePerDay;
    }

    public String toCsvLine() {
        return String.join("|",
                bookID,
                title.replace("|", " "),
                author.replace("|", " "),
                isbn.replace("|", " "),
                category.replace("|", " "),
                status,
                borrowerID,
                borrowerName.replace("|", " "),
                issueDate,
                dueDate
        );
    }

    public static Book fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 10) {
            return new Book(parts[0], parts[1], parts[2], parts[3], parts[4],
                    parts[5], parts[6], parts[7], parts[8], parts[9]);
        } else if (parts.length >= 5) {
            return new Book(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
}

/**
 * Class representing a Library Member
 */
class Member {
    private final String memberID;
    private String name;
    private String contact;
    private double finesPaid;

    public Member(String memberID, String name, String contact) {
        this.memberID = memberID;
        this.name = name;
        this.contact = contact;
        this.finesPaid = 0.0;
    }

    public Member(String memberID, String name, String contact, double finesPaid) {
        this.memberID = memberID;
        this.name = name;
        this.contact = contact;
        this.finesPaid = finesPaid;
    }

    public String getMemberID() { return memberID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public double getFinesPaid() { return finesPaid; }

    public void addFinePaid(double amount) {
        this.finesPaid += amount;
    }

    public String toCsvLine() {
        return String.join("|",
                memberID,
                name.replace("|", " "),
                contact.replace("|", " "),
                String.format(Locale.US, "%.2f", finesPaid)
        );
    }

    public static Member fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 4) {
            double fines = 0.0;
            try {
                fines = Double.parseDouble(parts[3]);
            } catch (NumberFormatException ignored) {}
            return new Member(parts[0], parts[1], parts[2], fines);
        } else if (parts.length >= 3) {
            return new Member(parts[0], parts[1], parts[2]);
        }
        return null;
    }
}

/**
 * Class representing a Borrow/Return Transaction record
 */
class Transaction {
    private final String txnID;
    private final String bookID;
    private final String bookTitle;
    private final String memberID;
    private final String memberName;
    private final String issueDate;
    private final String dueDate;
    private String returnDate;
    private double fineAmount;
    private String status; // "Active" or "Returned"

    public Transaction(String txnID, String bookID, String bookTitle, String memberID,
                       String memberName, String issueDate, String dueDate, String returnDate,
                       double fineAmount, String status) {
        this.txnID = txnID;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.memberID = memberID;
        this.memberName = memberName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public String getTxnID() { return txnID; }
    public String getBookID() { return bookID; }
    public String getBookTitle() { return bookTitle; }
    public String getMemberID() { return memberID; }
    public String getMemberName() { return memberName; }
    public String getIssueDate() { return issueDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }
    public String getStatus() { return status; }

    public void markReturned(double fine) {
        this.returnDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.fineAmount = fine;
        this.status = "Returned";
    }

    public String toCsvLine() {
        return String.join("|",
                txnID,
                bookID,
                bookTitle.replace("|", " "),
                memberID,
                memberName.replace("|", " "),
                issueDate,
                dueDate,
                returnDate,
                String.format(Locale.US, "%.2f", fineAmount),
                status
        );
    }

    public static Transaction fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 10) {
            double fine = 0.0;
            try {
                fine = Double.parseDouble(parts[8]);
            } catch (NumberFormatException ignored) {}
            return new Transaction(parts[0], parts[1], parts[2], parts[3], parts[4],
                    parts[5], parts[6], parts[7], fine, parts[9]);
        }
        return null;
    }
}

/**
 * Handles persistent file storage in the local "data" directory
 */
class DataManager {
    private static final String DATA_DIR = "data";
    private static final Path BOOKS_FILE = Paths.get(DATA_DIR, "books.txt");
    private static final Path MEMBERS_FILE = Paths.get(DATA_DIR, "members.txt");
    private static final Path TXNS_FILE = Paths.get(DATA_DIR, "transactions.txt");

    public static void initializeStorage(List<Book> books, List<Member> members, List<Transaction> txns) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));

            // Load or seed books
            if (Files.exists(BOOKS_FILE)) {
                List<String> lines = Files.readAllLines(BOOKS_FILE);
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    Book b = Book.fromCsvLine(line);
                    if (b != null) books.add(b);
                }
            } else {
                seedInitialBooks(books);
                saveBooks(books);
            }

            // Load or seed members
            if (Files.exists(MEMBERS_FILE)) {
                List<String> lines = Files.readAllLines(MEMBERS_FILE);
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    Member m = Member.fromCsvLine(line);
                    if (m != null) members.add(m);
                }
            } else {
                seedInitialMembers(members);
                saveMembers(members);
            }

            // Load transactions
            if (Files.exists(TXNS_FILE)) {
                List<String> lines = Files.readAllLines(TXNS_FILE);
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    Transaction t = Transaction.fromCsvLine(line);
                    if (t != null) txns.add(t);
                }
            } else {
                Files.createFile(TXNS_FILE);
            }
        } catch (IOException e) {
            System.out.println(Ansi.color("⚠️ Note: Could not access file storage: " + e.getMessage(), Ansi.YELLOW));
        }
    }

    private static void seedInitialBooks(List<Book> books) {
        String[][] initialData = {
            {"101", "To Kill a Mockingbird", "Harper Lee", "0060935464", "Classic Fiction"},
            {"102", "1984", "George Orwell", "0451524934", "Dystopian"},
            {"103", "The Great Gatsby", "F. Scott Fitzgerald", "0743273567", "Classic"},
            {"104", "Harry Potter & Sorcerer's Stone", "J.K. Rowling", "059035342X", "Fantasy"},
            {"105", "The Hobbit", "J.R.R. Tolkien", "054792822X", "Fantasy"},
            {"106", "Pride and Prejudice", "Jane Austen", "1503290565", "Romance / Classic"},
            {"107", "The Catcher in the Rye", "J.D. Salinger", "0316769487", "Fiction"},
            {"108", "The Chronicles of Narnia", "C.S. Lewis", "0066238501", "Fantasy"},
            {"109", "The Lord of the Rings", "J.R.R. Tolkien", "0618640150", "Fantasy"},
            {"110", "Charlotte's Web", "E.B. White", "0061124958", "Children's Lit"},
            {"111", "Madoldoowa", "Martin Wickramasinghe", "2002039293", "Sri Lankan Literature"},
            {"112", "Anna Karenina", "Leo Tolstoy", "3554211513", "Classic Fiction"},
            {"113", "Clean Code: A Handbook of Agile Craft", "Robert C. Martin", "0132350882", "Technology / Software"},
            {"114", "Introduction to Algorithms", "Thomas H. Cormen", "0262033844", "Computer Science"}
        };

        for (String[] data : initialData) {
            books.add(new Book(data[0], data[1], data[2], data[3], data[4]));
        }
    }

    private static void seedInitialMembers(List<Member> members) {
        members.add(new Member("M101", "Umindu Dinal", "071-2345678"));
        members.add(new Member("M102", "Kasun Perera", "077-9876543"));
        members.add(new Member("M103", "Nimal Silva", "075-1122334"));
    }

    public static void saveBooks(List<Book> books) {
        try {
            List<String> lines = books.stream().map(Book::toCsvLine).collect(Collectors.toList());
            Files.write(BOOKS_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println(Ansi.color("⚠️ Error saving books: " + e.getMessage(), Ansi.RED));
        }
    }

    public static void saveMembers(List<Member> members) {
        try {
            List<String> lines = members.stream().map(Member::toCsvLine).collect(Collectors.toList());
            Files.write(MEMBERS_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println(Ansi.color("⚠️ Error saving members: " + e.getMessage(), Ansi.RED));
        }
    }

    public static void saveTransactions(List<Transaction> txns) {
        try {
            List<String> lines = txns.stream().map(Transaction::toCsvLine).collect(Collectors.toList());
            Files.write(TXNS_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println(Ansi.color("⚠️ Error saving transactions: " + e.getMessage(), Ansi.RED));
        }
    }
}

/**
 * Main Library System holding all state and operations
 */
class Library {
    private final String libraryName;
    private final List<Book> books;
    private final List<Member> members;
    private final List<Transaction> transactions;
    private final double FINE_PER_DAY = 20.0; // Rs. 20 per day overdue
    private final int LOAN_PERIOD_DAYS = 14;   // 2 weeks loan
    private final int MAX_BORROW_LIMIT = 3;    // Maximum 3 books per member

    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.transactions = new ArrayList<>();
        DataManager.initializeStorage(books, members, transactions);
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void saveData() {
        DataManager.saveBooks(books);
        DataManager.saveMembers(members);
        DataManager.saveTransactions(transactions);
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }

    // Helper for safe user input
    public static String safeReadLine(Scanner scanner) {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "0";
    }

    // ==========================================
    // 1. DISPLAY & BROWSE BOOKS
    // ==========================================
    public void printBooksTable(List<Book> list) {
        if (list.isEmpty()) {
            System.out.println(Ansi.color("  No books found.", Ansi.YELLOW));
            return;
        }

        String border = "+-------+----------------------------------+-----------------------+----------------------+---------------+------------+--------------+";
        System.out.println(Ansi.color(border, Ansi.CYAN));
        System.out.printf(Ansi.color("| %-5s | %-32s | %-21s | %-20s | %-13s | %-10s | %-12s |%n", Ansi.BOLD_CYAN),
                "ID", "Title", "Author", "Category", "ISBN", "Status", "Due Date");
        System.out.println(Ansi.color(border, Ansi.CYAN));

        for (Book b : list) {
            String statusStr;
            String dueDateStr = b.getDueDate().isEmpty() ? "-" : b.getDueDate();

            if (b.isAvailable()) {
                statusStr = Ansi.color("Available", Ansi.BOLD_GREEN);
            } else if (b.isOverdue()) {
                statusStr = Ansi.color("OVERDUE", Ansi.BOLD_RED);
                dueDateStr = Ansi.color(dueDateStr + " ⚠️", Ansi.BOLD_RED);
            } else {
                statusStr = Ansi.color("Issued", Ansi.YELLOW);
            }

            System.out.printf("| %-5s | %-32s | %-21s | %-20s | %-13s | %-19s | %-12s |%n",
                    b.getBookID(),
                    truncate(b.getTitle(), 32),
                    truncate(b.getAuthor(), 21),
                    truncate(b.getCategory(), 20),
                    truncate(b.getISBN(), 13),
                    statusStr,
                    dueDateStr);
        }
        System.out.println(Ansi.color(border, Ansi.CYAN));
        System.out.println("  Total count: " + Ansi.color(String.valueOf(list.size()), Ansi.BOLD_WHITE) + " book(s)\n");
    }

    public void displayAllBooks() {
        System.out.println(Ansi.color("\n📚 --- ALL BOOKS IN CATALOG ---", Ansi.BOLD_WHITE));
        printBooksTable(books);
    }

    // ==========================================
    // 2. SEARCH & FILTER
    // ==========================================
    public void searchBooksMenu(Scanner scanner) {
        System.out.println(Ansi.color("\n🔍 --- SEARCH & FILTER BOOKS ---", Ansi.BOLD_WHITE));
        System.out.println("1. Search by Keyword (Title, Author, or ISBN)");
        System.out.println("2. Filter by Category");
        System.out.println("3. Filter by Status (Available / Issued)");
        System.out.println("4. View Currently Overdue Books");
        System.out.print(Ansi.color("Choose option [1-4]: ", Ansi.BOLD_CYAN));

        String opt = safeReadLine(scanner);
        switch (opt) {
            case "1" -> {
                System.out.print("Enter search keyword: ");
                String kw = safeReadLine(scanner).toLowerCase();
                List<Book> matches = books.stream()
                        .filter(b -> b.getTitle().toLowerCase().contains(kw)
                                || b.getAuthor().toLowerCase().contains(kw)
                                || b.getISBN().toLowerCase().contains(kw)
                                || b.getBookID().equalsIgnoreCase(kw))
                        .collect(Collectors.toList());
                System.out.println("\nResults matching '" + kw + "':");
                printBooksTable(matches);
            }
            case "2" -> {
                Set<String> categories = books.stream().map(Book::getCategory).collect(Collectors.toSet());
                System.out.println("\nAvailable categories:");
                for (String cat : categories) {
                    System.out.println(" • " + cat);
                }
                System.out.print("Enter category: ");
                String catInput = safeReadLine(scanner);
                List<Book> matches = books.stream()
                        .filter(b -> b.getCategory().equalsIgnoreCase(catInput))
                        .collect(Collectors.toList());
                System.out.println("\nBooks in category '" + catInput + "':");
                printBooksTable(matches);
            }
            case "3" -> {
                System.out.print("Enter status (A for Available, I for Issued): ");
                String s = safeReadLine(scanner).toLowerCase();
                boolean wantAvailable = s.startsWith("a");
                List<Book> matches = books.stream()
                        .filter(b -> wantAvailable ? b.isAvailable() : !b.isAvailable())
                        .collect(Collectors.toList());
                System.out.println("\nFiltered results:");
                printBooksTable(matches);
            }
            case "4" -> {
                List<Book> overdue = books.stream().filter(Book::isOverdue).collect(Collectors.toList());
                System.out.println(Ansi.color("\n⚠️ Overdue Books Report:", Ansi.BOLD_RED));
                printBooksTable(overdue);
            }
            default -> System.out.println(Ansi.color("Invalid search option.", Ansi.RED));
        }
    }

    // ==========================================
    // 3. BORROW A BOOK
    // ==========================================
    public void borrowBook(Scanner scanner) {
        System.out.println(Ansi.color("\n📖 --- BORROW A BOOK ---", Ansi.BOLD_WHITE));
        System.out.print("Enter Book ID to borrow: ");
        String bookID = safeReadLine(scanner);

        Book book = findBookById(bookID);
        if (book == null) {
            System.out.println(Ansi.color("❌ Book ID '" + bookID + "' not found in catalog!", Ansi.BOLD_RED));
            return;
        }

        if (!book.isAvailable()) {
            System.out.println(Ansi.color("❌ This book is currently issued to " + book.getBorrowerName()
                    + " (ID: " + book.getBorrowerID() + ") and is due on " + book.getDueDate(), Ansi.BOLD_RED));
            return;
        }

        System.out.print("Enter Member ID (e.g. M101): ");
        String memberID = safeReadLine(scanner);
        Member member = findMemberById(memberID);

        if (member == null) {
            System.out.println(Ansi.color("⚠️ Member ID '" + memberID + "' is not registered.", Ansi.YELLOW));
            System.out.print("Would you like to register this member now? (Y/N): ");
            String ans = safeReadLine(scanner).toLowerCase();
            if (ans.startsWith("y")) {
                member = registerNewMember(scanner, memberID);
                if (member == null) return;
            } else {
                System.out.println("Cannot issue book to an unregistered member.");
                return;
            }
        }

        // Check borrowing limit
        long currentBorrowed = books.stream()
                .filter(b -> memberID.equalsIgnoreCase(b.getBorrowerID()) && !b.isAvailable())
                .count();

        if (currentBorrowed >= MAX_BORROW_LIMIT) {
            System.out.println(Ansi.color("❌ Borrow limit reached! " + member.getName()
                    + " already has " + currentBorrowed + " books on loan (Max: " + MAX_BORROW_LIMIT + ").", Ansi.BOLD_RED));
            return;
        }

        // Issue book
        book.issueBook(member.getMemberID(), member.getName(), LOAN_PERIOD_DAYS);

        // Record Transaction
        String txnID = "TXN" + (transactions.size() + 1001);
        Transaction txn = new Transaction(txnID, book.getBookID(), book.getTitle(),
                member.getMemberID(), member.getName(),
                book.getIssueDate(), book.getDueDate(), "", 0.0, "Active");
        transactions.add(txn);

        saveData();

        System.out.println(Ansi.color("\n✅ Book Issued Successfully!", Ansi.BOLD_GREEN));
        System.out.println("  • Book Title   : " + Ansi.color(book.getTitle(), Ansi.BOLD_WHITE));
        System.out.println("  • Borrowed By  : " + member.getName() + " (" + member.getMemberID() + ")");
        System.out.println("  • Issue Date   : " + book.getIssueDate());
        System.out.println("  • Due Date     : " + Ansi.color(book.getDueDate(), Ansi.BOLD_YELLOW) + " (Loan period: " + LOAN_PERIOD_DAYS + " days)");
        System.out.println("  • Notice       : Late returns incur a fine of Rs. " + String.format(Locale.US, "%.2f", FINE_PER_DAY) + "/day.\n");
    }

    // ==========================================
    // 4. RETURN A BOOK
    // ==========================================
    public void returnBook(Scanner scanner) {
        System.out.println(Ansi.color("\n📥 --- RETURN A BOOK ---", Ansi.BOLD_WHITE));
        System.out.print("Enter Book ID to return: ");
        String bookID = safeReadLine(scanner);

        Book book = findBookById(bookID);
        if (book == null) {
            System.out.println(Ansi.color("❌ Book ID '" + bookID + "' not found!", Ansi.BOLD_RED));
            return;
        }

        if (book.isAvailable()) {
            System.out.println(Ansi.color("ℹ️ This book is already in the library and not currently issued.", Ansi.YELLOW));
            return;
        }

        String borrowerID = book.getBorrowerID();
        String borrowerName = book.getBorrowerName();
        long overdueDays = book.getOverdueDays();
        double fine = book.calculateFine(FINE_PER_DAY);

        System.out.println("\nReturning: " + Ansi.color(book.getTitle(), Ansi.BOLD_WHITE));
        System.out.println("Borrowed By: " + borrowerName + " (ID: " + borrowerID + ")");
        System.out.println("Due Date   : " + book.getDueDate());

        if (overdueDays > 0) {
            System.out.println(Ansi.color("\n⚠️ ATTENTION: THIS BOOK IS OVERDUE BY " + overdueDays + " DAY(S)!", Ansi.BOLD_RED));
            System.out.println("Fine Rate   : Rs. " + String.format(Locale.US, "%.2f", FINE_PER_DAY) + " per day");
            System.out.println("Total Fine  : " + Ansi.color("Rs. " + String.format(Locale.US, "%.2f", fine), Ansi.BOLD_RED));

            System.out.print(Ansi.color("Confirm fine collected & return book? (Y/N): ", Ansi.BOLD_YELLOW));
            String confirm = safeReadLine(scanner).toLowerCase();
            if (!confirm.startsWith("y")) {
                System.out.println("Return cancelled. Please settle the fine with the borrower.");
                return;
            }

            Member member = findMemberById(borrowerID);
            if (member != null) {
                member.addFinePaid(fine);
            }
        }

        // Complete matching transaction
        for (Transaction txn : transactions) {
            if (txn.getBookID().equals(bookID) && "Active".equalsIgnoreCase(txn.getStatus())) {
                txn.markReturned(fine);
                break;
            }
        }

        book.returnBook();
        saveData();

        System.out.println(Ansi.color("\n✅ Book returned successfully! It is now available in the catalog.\n", Ansi.BOLD_GREEN));
    }

    // ==========================================
    // 5. ADD NEW BOOK
    // ==========================================
    public void addNewBook(Scanner scanner) {
        System.out.println(Ansi.color("\n➕ --- ADD NEW BOOK ---", Ansi.BOLD_WHITE));

        System.out.print("Enter Book Title: ");
        String title = safeReadLine(scanner);

        System.out.print("Enter Author: ");
        String author = safeReadLine(scanner);

        System.out.print("Enter ISBN: ");
        String isbn = safeReadLine(scanner);

        System.out.print("Enter Category (e.g. Fiction, Science, Technology, Classic): ");
        String category = safeReadLine(scanner);

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println(Ansi.color("❌ Title, Author, and ISBN cannot be empty!", Ansi.BOLD_RED));
            return;
        }

        if (category.isEmpty()) {
            category = "General";
        }

        // Auto-generate Book ID
        int nextIdNum = 101;
        for (Book b : books) {
            try {
                int id = Integer.parseInt(b.getBookID().replaceAll("\\D", ""));
                if (id >= nextIdNum) nextIdNum = id + 1;
            } catch (NumberFormatException ignored) {}
        }
        String newID = String.valueOf(nextIdNum);

        Book newBook = new Book(newID, title, author, isbn, category);
        books.add(newBook);
        saveData();

        System.out.println(Ansi.color("\n✅ Book added successfully!", Ansi.BOLD_GREEN));
        System.out.println("  • Book ID : " + Ansi.color(newID, Ansi.BOLD_WHITE));
        System.out.println("  • Title   : " + title);
        System.out.println("  • Author  : " + author);
        System.out.println("  • ISBN    : " + isbn);
        System.out.println("  • Category: " + category + "\n");
    }

    // ==========================================
    // 6. REMOVE OR UPDATE A BOOK
    // ==========================================
    public void manageBookMenu(Scanner scanner) {
        System.out.println(Ansi.color("\n⚙️ --- EDIT / REMOVE BOOK ---", Ansi.BOLD_WHITE));
        System.out.print("Enter Book ID to manage: ");
        String bookID = safeReadLine(scanner);

        Book book = findBookById(bookID);
        if (book == null) {
            System.out.println(Ansi.color("❌ Book ID not found!", Ansi.BOLD_RED));
            return;
        }

        System.out.println("Selected: " + Ansi.color(book.getTitle(), Ansi.BOLD_WHITE) + " by " + book.getAuthor());
        System.out.println("Status  : " + (book.isAvailable() ? "Available" : "Issued to " + book.getBorrowerName()));
        System.out.println("1. Edit Details (Title, Author, Category, ISBN)");
        System.out.println("2. Delete Book from Catalog");
        System.out.print(Ansi.color("Choose option [1-2]: ", Ansi.BOLD_CYAN));

        String opt = safeReadLine(scanner);
        if ("1".equals(opt)) {
            System.out.print("New Title (leave blank to keep '" + book.getTitle() + "'): ");
            String t = safeReadLine(scanner);
            if (!t.isEmpty()) book.setTitle(t);

            System.out.print("New Author (leave blank to keep '" + book.getAuthor() + "'): ");
            String a = safeReadLine(scanner);
            if (!a.isEmpty()) book.setAuthor(a);

            System.out.print("New Category (leave blank to keep '" + book.getCategory() + "'): ");
            String c = safeReadLine(scanner);
            if (!c.isEmpty()) book.setCategory(c);

            System.out.print("New ISBN (leave blank to keep '" + book.getISBN() + "'): ");
            String i = safeReadLine(scanner);
            if (!i.isEmpty()) book.setISBN(i);

            saveData();
            System.out.println(Ansi.color("✅ Book details updated successfully!", Ansi.BOLD_GREEN));
        } else if ("2".equals(opt)) {
            if (!book.isAvailable()) {
                System.out.println(Ansi.color("❌ Cannot delete a book that is currently issued!", Ansi.BOLD_RED));
                return;
            }
            System.out.print(Ansi.color("Are you sure you want to delete '" + book.getTitle() + "'? (Y/N): ", Ansi.BOLD_RED));
            if (safeReadLine(scanner).toLowerCase().startsWith("y")) {
                books.remove(book);
                saveData();
                System.out.println(Ansi.color("✅ Book removed from catalog.", Ansi.BOLD_GREEN));
            }
        }
    }

    // ==========================================
    // 7. MEMBER MANAGEMENT
    // ==========================================
    public Member registerNewMember(Scanner scanner, String presetID) {
        String memberID = presetID;
        if (memberID == null || memberID.isEmpty()) {
            int maxId = 101;
            for (Member m : members) {
                try {
                    int num = Integer.parseInt(m.getMemberID().replaceAll("\\D", ""));
                    if (num >= maxId) maxId = num + 1;
                } catch (NumberFormatException ignored) {}
            }
            memberID = "M" + maxId;
        }

        System.out.print("Enter Member Full Name: ");
        String name = safeReadLine(scanner);
        System.out.print("Enter Contact / Phone Number: ");
        String contact = safeReadLine(scanner);

        if (name.isEmpty()) {
            System.out.println(Ansi.color("❌ Member name cannot be empty!", Ansi.BOLD_RED));
            return null;
        }

        Member newMember = new Member(memberID, name, contact);
        members.add(newMember);
        saveData();

        System.out.println(Ansi.color("✅ Member registered successfully with ID: " + memberID, Ansi.BOLD_GREEN));
        return newMember;
    }

    public void memberManagementMenu(Scanner scanner) {
        System.out.println(Ansi.color("\n👥 --- MEMBER MANAGEMENT ---", Ansi.BOLD_WHITE));
        System.out.println("1. List All Registered Members");
        System.out.println("2. Register New Member");
        System.out.println("3. View Member Details & Active Loans");
        System.out.print(Ansi.color("Choose option [1-3]: ", Ansi.BOLD_CYAN));

        String opt = safeReadLine(scanner);
        switch (opt) {
            case "1" -> {
                String border = "+------------+---------------------------+-------------------+------------------+";
                System.out.println(Ansi.color(border, Ansi.CYAN));
                System.out.printf(Ansi.color("| %-10s | %-25s | %-17s | %-16s |%n", Ansi.BOLD_CYAN),
                        "Member ID", "Name", "Contact", "Fines Paid (Rs)");
                System.out.println(Ansi.color(border, Ansi.CYAN));
                for (Member m : members) {
                    System.out.printf("| %-10s | %-25s | %-17s | %-16.2f |%n",
                            m.getMemberID(), truncate(m.getName(), 25), m.getContact(), m.getFinesPaid());
                }
                System.out.println(Ansi.color(border, Ansi.CYAN));
                System.out.println("Total members: " + members.size() + "\n");
            }
            case "2" -> registerNewMember(scanner, "");
            case "3" -> {
                System.out.print("Enter Member ID: ");
                String mid = safeReadLine(scanner);
                Member m = findMemberById(mid);
                if (m == null) {
                    System.out.println(Ansi.color("❌ Member ID not found!", Ansi.BOLD_RED));
                    return;
                }
                System.out.println("\n--- Member Profile ---");
                System.out.println("ID        : " + m.getMemberID());
                System.out.println("Name      : " + m.getName());
                System.out.println("Contact   : " + m.getContact());
                System.out.println("Fines Paid: Rs. " + String.format(Locale.US, "%.2f", m.getFinesPaid()));

                List<Book> activeLoans = books.stream()
                        .filter(b -> m.getMemberID().equalsIgnoreCase(b.getBorrowerID()))
                        .collect(Collectors.toList());

                System.out.println("\nActive Loans (" + activeLoans.size() + "/" + MAX_BORROW_LIMIT + "):");
                for (Book b : activeLoans) {
                    String due = b.isOverdue() ? Ansi.color(b.getDueDate() + " (OVERDUE)", Ansi.BOLD_RED) : b.getDueDate();
                    System.out.println(" • [" + b.getBookID() + "] " + b.getTitle() + " | Due: " + due);
                }
                System.out.println();
            }
            default -> System.out.println("Invalid option.");
        }
    }

    // ==========================================
    // 8. TRANSACTION AUDIT LOG
    // ==========================================
    public void displayTransactionHistory() {
        System.out.println(Ansi.color("\n📜 --- TRANSACTION HISTORY & AUDIT LOG ---", Ansi.BOLD_WHITE));
        if (transactions.isEmpty()) {
            System.out.println(Ansi.color("  No transactions recorded yet.\n", Ansi.YELLOW));
            return;
        }

        String border = "+---------+---------+----------------------------------+-----------+-------------------+-------------------+------------+";
        System.out.println(Ansi.color(border, Ansi.CYAN));
        System.out.printf(Ansi.color("| %-7s | %-7s | %-32s | %-9s | %-17s | %-17s | %-10s |%n", Ansi.BOLD_CYAN),
                "Txn ID", "Book ID", "Title", "Member", "Issue Date", "Return Date", "Fine (Rs)");
        System.out.println(Ansi.color(border, Ansi.CYAN));

        for (Transaction t : transactions) {
            String ret = t.getReturnDate().isEmpty() ? Ansi.color("Active", Ansi.YELLOW) : t.getReturnDate().substring(0, Math.min(10, t.getReturnDate().length()));
            System.out.printf("| %-7s | %-7s | %-32s | %-9s | %-17s | %-17s | %-10.2f |%n",
                    t.getTxnID(), t.getBookID(), truncate(t.getBookTitle(), 32),
                    t.getMemberID(),
                    t.getIssueDate().substring(0, Math.min(10, t.getIssueDate().length())),
                    ret, t.getFineAmount());
        }
        System.out.println(Ansi.color(border, Ansi.CYAN));
        System.out.println("Total transactions logged: " + transactions.size() + "\n");
    }

    // ==========================================
    // 9. LIBRARY STATISTICS & DASHBOARD
    // ==========================================
    public void displayDashboard() {
        long totalBooks = books.size();
        long available = books.stream().filter(Book::isAvailable).count();
        long issued = books.stream().filter(b -> !b.isAvailable()).count();
        List<Book> overdueList = books.stream().filter(Book::isOverdue).collect(Collectors.toList());
        long overdue = overdueList.size();

        double totalFinesCollected = members.stream().mapToDouble(Member::getFinesPaid).sum();

        System.out.println(Ansi.color("\n=======================================================", Ansi.BOLD_MAGENTA));
        System.out.println(Ansi.color("          📊 LIBRARY MANAGEMENT SYSTEM DASHBOARD        ", Ansi.BOLD_WHITE));
        System.out.println(Ansi.color("=======================================================", Ansi.BOLD_MAGENTA));
        System.out.printf("  • Total Books in Catalog : %s%n", Ansi.color(String.valueOf(totalBooks), Ansi.BOLD_CYAN));
        System.out.printf("  • Available Books        : %s%n", Ansi.color(String.valueOf(available), Ansi.BOLD_GREEN));
        System.out.printf("  • Currently Issued Books : %s%n", Ansi.color(String.valueOf(issued), Ansi.BOLD_YELLOW));
        System.out.printf("  • Overdue Books (Alert!) : %s%n", Ansi.color(String.valueOf(overdue), overdue > 0 ? Ansi.BOLD_RED : Ansi.GREEN));
        System.out.printf("  • Registered Members     : %s%n", Ansi.color(String.valueOf(members.size()), Ansi.BOLD_WHITE));
        System.out.printf("  • Total Fines Collected  : %s%n", Ansi.color("Rs. " + String.format(Locale.US, "%.2f", totalFinesCollected), Ansi.BOLD_GREEN));
        System.out.println(Ansi.color("-------------------------------------------------------", Ansi.BOLD_MAGENTA));

        if (!overdueList.isEmpty()) {
            System.out.println(Ansi.color("⚠️ OVERDUE BOOKS REQUIRING IMMEDIATE ATTENTION:", Ansi.BOLD_RED));
            for (Book ob : overdueList) {
                Member m = findMemberById(ob.getBorrowerID());
                String contact = m != null ? m.getContact() : "N/A";
                System.out.printf("   - [%s] %s | Borrowed By: %s (%s) | Due: %s | Days Late: %d | Fine: Rs. %.2f%n",
                        ob.getBookID(), ob.getTitle(), ob.getBorrowerName(), contact,
                        ob.getDueDate(), ob.getOverdueDays(), ob.calculateFine(FINE_PER_DAY));
            }
        }
        System.out.println();
    }

    private Book findBookById(String id) {
        for (Book b : books) {
            if (b.getBookID().equalsIgnoreCase(id)) return b;
        }
        return null;
    }

    private Member findMemberById(String id) {
        for (Member m : members) {
            if (m.getMemberID().equalsIgnoreCase(id)) return m;
        }
        return null;
    }
}

/**
 * Main application class and interactive menu
 */
public class LibraryManagementSystem {
    public static void main(String[] args) {
        try {
            Library library = new Library("ITUM");
            Scanner scanner = new Scanner(System.in);
            String choice = "";

            while (!"0".equals(choice) && !"q".equalsIgnoreCase(choice)) {
                printMainMenu(library.getLibraryName());
                System.out.print(Ansi.color("Enter your choice: ", Ansi.BOLD_CYAN));
                choice = Library.safeReadLine(scanner);

                switch (choice.toLowerCase()) {
                    case "1", "d" -> library.displayAllBooks();
                    case "2", "s" -> library.searchBooksMenu(scanner);
                    case "3", "b" -> library.borrowBook(scanner);
                    case "4", "r" -> library.returnBook(scanner);
                    case "5", "a" -> library.addNewBook(scanner);
                    case "6" -> library.manageBookMenu(scanner);
                    case "7", "m" -> library.memberManagementMenu(scanner);
                    case "8", "t" -> library.displayTransactionHistory();
                    case "9" -> library.displayDashboard();
                    case "0", "q" -> {
                        library.saveData();
                        System.out.println(Ansi.color("\n💾 All data saved successfully.", Ansi.BOLD_GREEN));
                        System.out.println(Ansi.color("Thank you for using ITUM Library Management System! Have a great day!\n", Ansi.BOLD_CYAN));
                    }
                    default -> System.out.println(Ansi.color("❌ Invalid option! Please select a valid number from the menu.\n", Ansi.RED));
                }

                if (!"0".equals(choice) && !"q".equalsIgnoreCase(choice)) {
                    System.out.print(Ansi.color("Press Enter to continue...", Ansi.WHITE));
                    Library.safeReadLine(scanner);
                }
            }
        } catch (Exception e) {
            System.err.println(Ansi.color("Fatal error: " + e.getMessage(), Ansi.RED));
        }
    }

    private static void printMainMenu(String libName) {
        System.out.println(Ansi.color("\n==================================================================================", Ansi.BOLD_CYAN));
        System.out.println(Ansi.color("          🏛️  WELCOME TO " + libName.toUpperCase() + " LIBRARY MANAGEMENT SYSTEM          ", Ansi.BOLD_WHITE));
        System.out.println(Ansi.color("==================================================================================", Ansi.BOLD_CYAN));
        System.out.println(Ansi.color("  [1] Browse & Display All Books", Ansi.BOLD_WHITE) + "       " + Ansi.color("(Table view with categories & status)", Ansi.CYAN));
        System.out.println(Ansi.color("  [2] Search & Filter Books", Ansi.BOLD_WHITE) + "            " + Ansi.color("(Search by Title, Author, Category, Status)", Ansi.CYAN));
        System.out.println(Ansi.color("  [3] Borrow a Book", Ansi.BOLD_WHITE) + "                    " + Ansi.color("(Member validation & loan limit check)", Ansi.CYAN));
        System.out.println(Ansi.color("  [4] Return a Book", Ansi.BOLD_WHITE) + "                    " + Ansi.color("(Overdue tracking & late fine calculation)", Ansi.CYAN));
        System.out.println(Ansi.color("  [5] Add New Book", Ansi.BOLD_WHITE) + "                     " + Ansi.color("(Add new book to catalog)", Ansi.CYAN));
        System.out.println(Ansi.color("  [6] Edit / Remove a Book", Ansi.BOLD_WHITE) + "             " + Ansi.color("(Update details or delete books)", Ansi.CYAN));
        System.out.println(Ansi.color("  [7] Member Management", Ansi.BOLD_WHITE) + "                " + Ansi.color("(Register, view members & active loans)", Ansi.CYAN));
        System.out.println(Ansi.color("  [8] Transaction History", Ansi.BOLD_WHITE) + "              " + Ansi.color("(Audit log of borrows, returns & fines)", Ansi.CYAN));
        System.out.println(Ansi.color("  [9] Statistics Dashboard", Ansi.BOLD_WHITE) + "             " + Ansi.color("(Overview, overdue alert report & metrics)", Ansi.CYAN));
        System.out.println(Ansi.color("  [0] Save & Exit Application", Ansi.BOLD_WHITE));
        System.out.println(Ansi.color("==================================================================================", Ansi.BOLD_CYAN));
    }
}
