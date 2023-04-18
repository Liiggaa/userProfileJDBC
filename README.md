# userProfileJDBC


## Simple Java console chat application. This application is designed to provide a basic functionality of a chat application. The application is running only in the console. Uses the Scanner class to read user input from the console and stores user information and messages in a MySQL database.

## Technologies Used:

- Java 
- MySQL 

## Features:

- User Registration
- User Login
- Send Message 
- User Logout
- Update User Profile
- Delete User Profile

Once the Chat Application is running, you can use the following options:

1. LOGIN: Allows existing users to login to the application by entering their username and password using the Scanner class for user input.
2. SIGNUP: Allows new users to create an account by providing their details such as username, password, first name, last name, email, phone, address, gender, and age using the Scanner class for user input.
3. EXIT: Closes the application.

After logging in, the following options are available:

4. CHAT: Allows logged in users to send messages to other users in the system.
5. UPDATE PROFILE: Allows logged in users to update their profile information such as first name, last name, email, phone, address, gender, age, and password.
6. DELETE ACCOUNT: Allows logged in users to delete their own account from the system, all information from database are deleted.
7. LOGOUT: Logs out the user and returns to the login screen.

## Project screenshots:

## Setup Instructions:

* Import the project into your preferred Java IDE.
* Update the database connection URL, username and password in the runChatApplication() method in the Main class to match your MySQL database settings.
* Create a MySQL database with the name java_class_db (you can change the name in the code if needed).
```
 try {
       conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/java_class_db", "YOUR_USERNAME", "YOUR_PASSWORD");
}catch (SQLException ex){
       ex.printStackTrace();
       return;
}
```
* Create tables UserProfile, Messages and related triggers in MySQL, code for creating tables you can find on sqlFiles package (database.sql)
* Make sure to import the Scanner class from the `java.util` package in your Java code to use it for reading user input.
* Run the Main class to start the Chat Application.
* Enter your input as guided in console and use application.


Note: This is a Java learning project, there could be bugs and flaws. 




