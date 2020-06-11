/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author mike
 */
public class ValidationUtil {

    public static void validateString(String s, int length) throws ValidationException {
        if (s == null || s.trim().isEmpty() || s.length() > length) {
            throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
        }
    }

    public static void validateExtractionType(String s) throws ValidationException {
        if (!s.equals("json") && !s.equals("html") && !s.equals("xml")) {
            throw new ValidationException("extraction type MUST be one of the following: json|html|xml");
        }
    }
}
