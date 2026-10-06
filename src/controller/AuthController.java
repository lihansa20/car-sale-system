package controller;

import dao.UserDAO;
import model.User;
import util.ValidationException;
import util.Validator;

import java.sql.SQLException;

public class AuthController {

    private static User currentUser;

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws ValidationException, SQLException {
        Validator.requireText(username, "Username");
        Validator.requireText(password, "Password");

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            throw new ValidationException("Invalid username or password.");
        }

        currentUser = user;
        return user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}