
import java.sql.*;
import java.util.Scanner;
// Assignment
/*
* User profile management system
* Table userprofile (firstname, lastname, email, phone, address, gender, age, password, USERNAME(firstname + lastname + last 2 digits of phone))
* SIGNUP, LOGIN(username, password), LOGOUT
* UPDATE(id)
* DELETE(id)
 */

/* Messaging system with your user profiles
* Table messages [TO(receiver’s id), FROM(sender’s id), TIME_SENT]
* User can see list of other users after login and can select who to chat with
* If user already has existing chat with a friend he selects, display old messages
 */


public class Main {

    public static void main(String[] args) {

        try {
            runChatApplication();
        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Please try again!");
        }
    }

    public static void runChatApplication() throws SQLException {

        Connection conn = null; // for database connection

        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/java_class_db", "root", "admin");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }


        System.out.println("\n***WELCOME TO THE CHAT APPLICATION!***");
        Scanner scanner = new Scanner(System.in);

        boolean isAppRunning = true;
        while (isAppRunning) {
            displayOptions();

            if (scanner.hasNextInt()) { // check if user enter valid(integer) value
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        logIn(conn);
                        break;
                    case 2:
                        signUp(conn);
                        break;
                    case 3:
                        System.out.println("Application closed!\nThank you for using Chat Application!");
                        scanner.close();
                        System.exit(0); //exit app
                        isAppRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again. Enter only 1, 2 or 3!");
                }
            } else {
                System.out.println("Invalid choice. Please try again. Enter only 1, 2 or 3!");
                scanner.nextLine(); // clear input buffer
            }
        }
    }

    public static void displayOptions() {

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nChoose one of these options:");
        System.out.println("   1. LOGIN");
        System.out.println("   2. SIGNUP");
        System.out.println("   3. EXIT");
        System.out.print("Enter your choice to continue: ");
    }

    public static void logIn(Connection connection) throws SQLException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("\nLOGIN:");
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();


        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SELECT * FROM userProfile WHERE username= " + "'" + username + "'" + " AND user_password= " + "'" + password + "'");

        if (resultSet.next()) {
            System.out.println("Login successful!\n\nUSER PROFILE");
            System.out.println("id: " + resultSet.getInt(1));
            System.out.println("first name: " + resultSet.getString(2));
            System.out.println("last name: " + resultSet.getString(3));
            System.out.println("email: " + resultSet.getString(4));
            System.out.println("phone: " + resultSet.getString(5));
            System.out.println("address: " + resultSet.getString(6));
            System.out.println("gender: " + resultSet.getString(7));
            System.out.println("age: " + resultSet.getInt(8));
            System.out.println("password: " + resultSet.getString(9));
            System.out.println("username: " + resultSet.getString(10));

            int userId = resultSet.getInt(1);
            // System.out.println("CHECK: USER ID:" + userId);

            chatOrUpdateUser(connection, userId);

        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void chatOrUpdateUser(Connection connection, int userId) throws SQLException {
        System.out.println("\n--- CHOOSE ONE OF THESE OPTIONS! ---");
        Scanner scanner = new Scanner(System.in);
        boolean isAppRunning2 = true;
        while (isAppRunning2) {
            System.out.println("\nChoose one of these options:");
            System.out.println("   4. CHAT");
            System.out.println("   5. UPDATE PROFILE");
            System.out.println("   6. DELETE ACCOUNT");
            System.out.println("   7. LOGOUT");
            System.out.print("Enter your choice to continue: ");

            if (scanner.hasNextInt()) { // check if user entered integer value
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 4:
                        if (isUserExists(connection, userId)) { // check if user exists
                            viewAllUsers(connection);
//                            System.out.println("CHECK:" + userId);
                            sendMessage(connection, userId);
                        } else {
                            System.out.println("User does not exist.");
                        }
                        break;
                    case 5:
                        if (isUserExists(connection, userId)) {
                            updateUser(connection, userId);
                        } else {
                            System.out.println("User does not exist.");
                        }
                        break;
                    case 6:
                        deleteUser(connection, userId);
                        if (isUserExists(connection, userId)) {
                            System.out.println("Please check username and password");
                        } else {
                            isAppRunning2 = false;
                        }
                        break;
                    case 7:
                        System.out.println("\nSuccessfully Logged out!\nThank you for using Chat Application!");
                        isAppRunning2 = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again. Enter only 4, 5, 6, or 7!");
                }
            } else {
                System.out.println("Invalid choice. Please try again. Enter only 4, 5, 6, or 7!");
                scanner.nextLine();
            }
        }
    }

    private static void viewAllUsers(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM userProfile");
        System.out.println("\nAll USERS:");
        while (resultSet.next()) {
            System.out.println("id: " + resultSet.getInt(1) + " " + resultSet.getString(10));
        }
    }

    public static void sendMessage(Connection connection, int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean sendAnotherMessage = true; // flag if user wants to send another message

        do {
            System.out.println("\nCHAT!");
            System.out.println("Choose user to chat with. Please enter user id from user list:");
            int receiverId = scanner.nextInt();

            // get all messages from database between two users
            PreparedStatement pStatement2 = connection.prepareStatement("SELECT a.first_name AS sender_first_name, b.first_name AS receiver_first_name, m.message, m.time_sent FROM messages m " +
                    "INNER JOIN userprofile a ON m.sender_id = a.id " +
                    "INNER JOIN userprofile b ON m.receiver_id = b.id " +
                    "WHERE (m.sender_id = ? AND m.receiver_id = ?) " +
                    "OR (m.sender_id = ? AND m.receiver_id = ?) ORDER BY m.time_sent ASC");
            pStatement2.setInt(1, userId);
            pStatement2.setInt(2, receiverId);
            pStatement2.setInt(3, receiverId);
            pStatement2.setInt(4, userId);

            ResultSet resultSet = pStatement2.executeQuery();

            System.out.println("All Messages:");
            // loop through the result set and display messages
            while (resultSet.next()) {
                String senderFirstName = resultSet.getString("sender_first_name");
                String receiverFirstName = resultSet.getString("receiver_first_name");
                String messageFromDatabase = resultSet.getString("message");
                Timestamp timeSentFromDatabase = resultSet.getTimestamp("time_sent");


                System.out.println(timeSentFromDatabase + " " + senderFirstName + " to " + receiverFirstName + " " + messageFromDatabase);
            }

            scanner.nextLine();
            System.out.println("Please enter message:");
            String message = scanner.nextLine();

            java.sql.Timestamp timeSent = new java.sql.Timestamp(new java.util.Date().getTime());

            PreparedStatement pStatement = connection.prepareStatement("INSERT INTO messages (receiver_id, sender_id, message, time_sent) VALUES (?, ?, ?, ?)");
            pStatement.setInt(1, receiverId);
            pStatement.setInt(2, userId);
            pStatement.setString(3, message);
            pStatement.setTimestamp(4, timeSent);

            pStatement.executeUpdate();
            System.out.println("Message sent successfully!");
            System.out.println("------------------------------");

            ResultSet resultSet2 = pStatement2.executeQuery();

            System.out.println("All Messages:");

            while (resultSet2.next()) {
                String senderFirstName = resultSet2.getString("sender_first_name");
                String receiverFirstName = resultSet2.getString("receiver_first_name");
                String messageFromDatabase = resultSet2.getString("message");
                Timestamp timeSentFromDatabase = resultSet2.getTimestamp("time_sent");

                System.out.println(timeSentFromDatabase + " " + senderFirstName + " to " + receiverFirstName + " " + messageFromDatabase);
            }

            System.out.println("Do you want to send another message? (yes/no)"); // for sending another message
            String sendAnotherMsg = scanner.nextLine();
            if (sendAnotherMsg.equalsIgnoreCase("no")) {
                sendAnotherMessage = false; // set sendAnotherMessage to false if user chooses not to send another message
            }
        } while (sendAnotherMessage); // while for do loop

    }

    public static void updateUser(Connection connection, int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("UPDATE USER PROFILE");
        System.out.println("Please enter your new first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please enter your new last name:");
        String lastName = scanner.nextLine();
        System.out.println("Please enter your new email:");
        String email = scanner.nextLine();
        System.out.println("Please enter your new phone number:");
        String phone = scanner.nextLine();
        System.out.println("Please enter your new address:");
        String address = scanner.nextLine();
        System.out.println("Please enter your new gender:");
        String gender = scanner.nextLine();
        System.out.println("Please enter your new age:");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Please enter your new password:");
        String password = scanner.nextLine();


        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE UserProfile SET first_name= ?, last_name= ?, e_mail= ?, phone= ?, address= ?, gender= ?, age= ?, user_password=? WHERE id = ?");

            pStatement.setString(1, firstName);
            pStatement.setString(2, lastName);
            pStatement.setString(3, email);
            pStatement.setString(4, phone);
            pStatement.setString(5, address);
            pStatement.setString(6, gender);
            pStatement.setInt(7, age);
            pStatement.setString(8, password);
            pStatement.setInt(9, userId);
            pStatement.executeUpdate();
            System.out.println("\nProfile successfully updated!");

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM userProfile WHERE id=" + userId + "");

            if (resultSet.next()) {
                System.out.println("\nUPDATED USER PROFILE INFO:");
                System.out.println("id: " + resultSet.getInt(1));
                System.out.println("first name: " + resultSet.getString(2));
                System.out.println("last name: " + resultSet.getString(3));
                System.out.println("email: " + resultSet.getString(4));
                System.out.println("phone: " + resultSet.getString(5));
                System.out.println("address: " + resultSet.getString(6));
                System.out.println("gender: " + resultSet.getString(7));
                System.out.println("age: " + resultSet.getInt(8));
                System.out.println("password: " + resultSet.getString(9));
                System.out.println("username: " + resultSet.getString(10));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("User with this email, phone or username already exists.Try again.\n");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void signUp(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please enter your last name:");
        String lastName = scanner.nextLine();
        System.out.println("Enter valid email address.\nPlease enter your email:");
        String email = scanner.nextLine();
        System.out.println("Phone number should consist of 8 digits.\nPlease enter your phone number:");
        String phone = scanner.nextLine();
        System.out.println("Please enter your address:");
        String address = scanner.nextLine();
        System.out.println("Please enter your gender:");
        String gender = scanner.nextLine();
        System.out.println("Please enter your age:");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Password should consist of at least 5 characters, including one number and one uppercase letter.\nPlease enter your password:");
        String password = scanner.nextLine();

        // validate password, email and phone
        boolean isPasswordValid = validatePassword(password);
        boolean isEmailValid = validateEmail(email);
        boolean isPhoneValid = validatePhoneNumber(phone);


        if (!isPasswordValid || !isEmailValid || !isPhoneValid) {
            if (!isPasswordValid) {
                System.out.println("Invalid password. Password should consist of at least 5 characters, including one digit and one uppercase letter.");
            }
            if (!isEmailValid) {
                System.out.println("Invalid email address.");
            }
            if (!isPhoneValid) {
                System.out.println("Invalid phone number. Phone number should consist of 8 digits.");
            }
        }

        if(isPasswordValid && isEmailValid && isPhoneValid){
            try {
                //insert user information into the database
                PreparedStatement pStatement = connection.prepareStatement("INSERT INTO UserProfile (first_name, last_name, e_mail, phone, address, gender, age, user_password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                pStatement.setString(1, firstName);
                pStatement.setString(2, lastName);
                pStatement.setString(3, email);
                pStatement.setString(4, phone);
                pStatement.setString(5, address);
                pStatement.setString(6, gender);
                pStatement.setInt(7, age);
                pStatement.setString(8, password);
                pStatement.executeUpdate();

                System.out.println("Sign up successful!\n");

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT username FROM userProfile WHERE first_name='" + firstName + "' AND last_name='" + lastName + "' AND phone='" + phone + "'");
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    System.out.println("Your username is: " + username);
                    System.out.println("\nNow you can log in to your account!");
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    System.out.println("User with this email, phone or username already exists.Try again.\n");
                } else {
                    e.printStackTrace();
                    System.out.println("Error. Try again!");

                }
            }
        }
        else{
            System.out.println("Signup failed!");
        }
    }


    private static void deleteUser(Connection connection, int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nDELETE ACCOUNT:");
        System.out.println("WARNING: All messages and profile information will be deleted!");
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        // delete messages sent by the user
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM messages WHERE sender_id = ?");
        pStatement.setInt(1, userId);

        pStatement.executeUpdate();

        // delete messages received by the user
        pStatement = connection.prepareStatement("DELETE FROM messages WHERE receiver_id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();

        // delete the user from userProfile table
        pStatement = connection.prepareStatement("DELETE FROM userProfile WHERE id = ? AND username= ? AND user_password= ?");
        pStatement.setInt(1, userId);
        pStatement.setString(2, username);
        pStatement.setString(3, password);

        int updateCount = pStatement.executeUpdate(); // get the number of rows affected from executeUpdate method

        if (updateCount > 0) {
            System.out.println("Account successfully deleted!");
        } else {
            System.out.println("Failed to delete account.");
        }
    }

    private static boolean isUserExists(Connection connection, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT id FROM userProfile WHERE id = ?");
        pStatement.setInt(1, userId);
        ResultSet resultSet = pStatement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        pStatement.close();
        return exists;
    }

    // simple methods for data validation
    public static boolean validatePassword(String password) {
        if (!password.matches("^(?=.*\\d)(?=.*[A-Z]).{5,}$")) {
            return false;
        }
        return true;
    }

    public static boolean validateEmail(String email) {
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            return false;
        }
        return true;
    }

    public static boolean validatePhoneNumber(String phone) {
        if (!phone.matches("^[0-9]{8}$")) {
            return false;
        }
        return true;
    }
}