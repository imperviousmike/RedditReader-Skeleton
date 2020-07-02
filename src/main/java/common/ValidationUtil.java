/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 * This class contains methods used to validate data before it is used by the
 * application.
 *
 * @author mike
 */
public class ValidationUtil {

    /**
     * This method is used to validate a String. It takes a String s and an int
     * length, and is used to check if the String is null, empty, or longer than
     * a certain length. If any of the above are true, it throws a
     * ValidationException and notifies the user of the issue.
     */
    public static void validateString(String s, int length) throws ValidationException {
        if (s == null || s.trim().isEmpty() || s.length() > length) {
            throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
        }
    }

    /**
     * This method is used to validate the extraction type of data. It takes a
     * String s which is used to check if the extraction type is one json, html,
     * or xml.If not, it throws a ValidationException and notifies the user of
     * the issue.
     */
    public static void validateExtractionType(String s) throws ValidationException {
        if (!s.equals("json") && !s.equals("html") && !s.equals("xml")) {
            throw new ValidationException("extraction type MUST be one of the following: json|html|xml");
        }
    }
}
