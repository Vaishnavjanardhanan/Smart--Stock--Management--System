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

