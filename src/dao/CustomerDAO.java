package dao;

import database.DBConnection;
import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static final String SELECT_ALL =
            "SELECT id, name, nic, phone, email, address FROM customers";


    public List<Customer> getAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL + " ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        }
        return customers;
    }

    public List<Customer> search(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE name LIKE ? OR nic LIKE ? OR phone LIKE ? ORDER BY name";
        String pattern = "%" + keyword.trim() + "%";

        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        }
        return customers;
    }

    public Customer getById(int id) throws SQLException {
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL + " WHERE id = ?")) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public boolean nicExists(String nic, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE nic = ? AND id <> ?";
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nic.trim());
            ps.setInt(2, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }


    public int add(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (name, nic, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getNic());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getAddress());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void update(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name = ?, nic = ?, phone = ?, email = ?, address = ? WHERE id = ?";
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getNic());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getAddress());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        Connection con = DBConnection.getInstance().getConnection();

        try (PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("nic"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"));
    }
}