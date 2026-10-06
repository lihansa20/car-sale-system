package util;

import java.time.LocalDate;

public class Validator {

    private Validator() {
    }

    public static void requireText(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void requireMaxLength(String value, int max, String fieldName) throws ValidationException {
        if (value != null && value.trim().length() > max) {
            throw new ValidationException(fieldName + " cannot be longer than " + max + " characters.");
        }
    }

    public static double parsePositiveAmount(String text, String fieldName) throws ValidationException {
        requireText(text, fieldName);
        double value;
        try {
            value = Double.parseDouble(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid number.");
        }
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than 0.");
        }
        return value;
    }

    public static int parseYear(String text) throws ValidationException {
        requireText(text, "Year");
        int year;
        try {
            year = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Year must be a whole number.");
        }
        int currentYear = LocalDate.now().getYear();
        if (year < 1980 || year > currentYear + 1) {
            throw new ValidationException("Year must be between 1980 and " + (currentYear + 1) + ".");
        }
        return year;
    }


    // Old NIC: 9 digits + V or X (example 901234567V). New NIC: 12 digits.
    public static void validateNic(String nic) throws ValidationException {
        requireText(nic, "NIC");
        if (!nic.trim().matches("^(\\d{9}[VvXx]|\\d{12})$")) {
            throw new ValidationException("NIC must be 9 digits + V/X, or 12 digits.");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        requireText(phone, "Phone");
        if (!phone.trim().matches("^0\\d{9}$")) {
            throw new ValidationException("Phone must be 10 digits and start with 0.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        if (!email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new ValidationException("Email format is not valid.");
        }
    }


    public static void validateSellingPrice(double costPrice, double sellingPrice) throws ValidationException {
        if (sellingPrice < costPrice) {
            throw new ValidationException("Selling price cannot be lower than the cost price.");
        }
    }
}