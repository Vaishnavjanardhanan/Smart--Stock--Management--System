# STOCK MANAGEMENT SYSTEM

# Customer Management Java Application

A simple Java desktop application for managing customer records, designed for educational and small business purposes.

## Features

- Add, update, and delete customers[1]
- Fetch all active or inactive customers[1]
- Search customers by name or email[1]
- Soft-delete and restore customer records[1]
- Ensure email uniqueness and validate customer input[1]
- Supports basic reporting on total customers[1]

## File Structure

| File Name         | Description                                                     |
|-------------------|-----------------------------------------------------------------|
| Customer.java     | Data model for customer entities, including ID, name, email, phone, address, and registration date[2].            |
| CustomerDAO.java  | Manages all database operations for customers: add, update, search, soft-delete, restore, and more[1].             |
| MainApp.java      | Entry point for launching the project with a Swing-based UI, starting with user login[3].                         |

## Requirements

- Java Development Kit (JDK) 8+[1]
- Database connection utility (`DBConnection`) and supporting database table (not included)[1]
- GUI classes (`LoginFrame` and related views) are referenced but not provided[3]
- JDBC configuration for connecting to your SQL database[1]

## Setup and Usage

1. Clone the repository to your local machine.
2. Configure your database and tables as referenced in the code (see documentation in `CustomerDAO.java`)[1].
3. Compile all `.java` files[1].
4. Ensure necessary libraries (JDBC) are present[1].
5. Run `MainApp.java` to launch the Swing application[3].

## Notes

- You may extend the application by adding new fields or modules (e.g. sales, orders)[1].
- Exception handling and input validation are implemented for database reliability[1].
- Custom views and db connection management are required for complete functionality[1].


# Sales Management Java Module
A modular Java codebase featuring user authentication and sales transaction management for stores and businesses.

## Features

- User model for authentication and role management
- Record product sales and manage inventory
- Support for walk-in and registered customers
- Aggregate sales reports (by day and total count)
- Robust exception handling for database operations

## File Structure

| File Name      | Description                                                                           |
|----------------|---------------------------------------------------------------------------------------|
| User.java      | Java Bean representing system users, including authentication credentials and roles[1].            |
| SaleDAO.java   | Data Access Object for all sales-related operations: recording, reporting, and customer linking[2].|

## Requirements

- Java Development Kit (JDK) 8 or higher
- Database connection utility (`DBConnection`) and a database schema for users, sales, products, and customers
- JDBC driver matching your database
- Integration with supporting model classes (e.g., Product, Sale, Customer as referenced in the code)

## Setup and Usage

1. Clone or download these source files.
2. Ensure the required database tables (users, products, saleslog, customers) exist and are properly structured.
3. Compile and include these files in your main Java project.
4. Use the `SaleDAO` methods to record sales, generate reports, and manage inventory.
5. Extend `User.java` for handling login, authentication, and permissions.

## Notes

- Walk-in customers are auto-created and tracked in the database for anonymous transactions[2].
- Sales reporting includes per-day totals and total transaction count[2].
- Exception handling is implemented for all database interactions to enhance reliability[2].
- The user model is easily expandable for new fields or features[1].



# User and Sale Module for Java Applications

Java classes for managing user authentication and sale transactions, ideal for retail or business management systems.

## Features

- User authentication and registration (includes duplicate username checking)
- Sale model: supports storing details for sales transactions
- Robust database operations with error handling
- Clear data modeling for both users and sales

## File Structure

| File Name      | Description                                                                             |
|----------------|-----------------------------------------------------------------------------------------|
| UserDAO.java   | Data Access Object for user login, registration, and username existence verification[1]. |
| Sale.java      | Entity class representing a sales transaction, with fields for IDs, names, amounts, and date[2]. |

## Requirements

- Java Development Kit (JDK) 8 or higher
- SQL database with tables for users and sales
- Database connection utility (`DBConnection`)
- JDBC library matching your database

## Setup and Usage

1. Ensure your database contains appropriate tables (`users`, `sales`, etc.).
2. Add the classes to your main Java project.
3. For user management, use the methods in `UserDAO` to authenticate, add, or verify users.
4. For sales management, create and use `Sale` objects to represent transactions in your system.

## Notes

- The user DAO is designed to prevent duplicate registrations using unique username checks[1].
- The sale model supports a wide range of fields for comprehensive transaction logging[2].
- Handle exceptions on database operations for optimal reliability[1].
- Easily extend fields to suit expanded business requirements.



#Product and Login Management Modules for Java Applications

Java classes for managing products, inventory, and user login tracking — suitable for stock management and retail business systems.

##Features

Product management: Add, update, delete, and retrieve product details

Stock control: Supports real-time stock adjustment and low-stock detection

Sales logging: Automatically records product sales and adjusts stock

Login tracking: Records user logins, logouts, and session durations

Error handling: Built-in SQL exception management for robust database operations



---

##File Structure

File Name	Description

Product.java	Model class representing a product entity, including fields for ID, name, description, price, and stock[2].
ProductDAO.java	Data Access Object for all product operations: CRUD, stock management, and sales recording[3].
LoginHistoryDAO.java	DAO class to track login and logout history, calculate session durations, and retrieve login logs[1].



---

##Requirements

Java Development Kit (JDK) 8 or higher

SQL database with tables:

products (for product info and stock)

sales_log (for transaction logs)

login_history (for login tracking)


Database connection utility (DBConnection)

JDBC library matching your SQL database



---

##Setup and Usage

1. Database setup:
Create tables for products, sales_log, and login_history with proper schema (columns like product_id, name, stock, etc.).


2. Add files:
Include all Java files (Product.java, ProductDAO.java, LoginHistoryDAO.java) in your project’s model package.


3. Product operations:

Use ProductDAO.addProduct() to insert new products.

Call ProductDAO.updateProduct() or updateStock() to modify product details.

Use getLowStockProducts() to identify low-stock items automatically.

Record sales with recordSale(), which reduces stock and logs transactions.



4. Login tracking:

Use LoginHistoryDAO.recordLogin(username) on successful login.

Call LoginHistoryDAO.recordLogout(username) when the user logs out.

Retrieve records using getLoginHistory() to display session logs or durations.





---

##Notes

The Product model provides clean getters for all core attributes (ID, name, price, etc.)[2].

ProductDAO ensures data consistency with SQL transactions and rollback safety when recording sales[3].

LoginHistoryDAO tracks both login and logout timestamps while computing total session time for analytics or monitoring[1].

Methods are static, allowing easy integration with GUI or web interfaces.

