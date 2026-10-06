package dao;

import database.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    public User authenticate(String username, String plainPassword) throws SQLException {
        String sql = "SELECT id, username, password, full_name, role "
                + "FROM users WHERE username = ? AND password = ?";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, PasswordUtil.hash(plainPassword));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, role FROM users ORDER BY full_name";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("role"));
    }
}