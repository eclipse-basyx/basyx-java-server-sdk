package org.eclipse.digitaltwin.basyx.http;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class Base64UrlEncodedIdentifierSizeValidator implements ConstraintValidator<Base64UrlEncodedIdentifierSize, Base64UrlEncodedIdentifier> {

    private int min;
    private int max;

    @Override
    public void initialize(Base64UrlEncodedIdentifierSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Base64UrlEncodedIdentifier value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are considered valid
        }
        // Implement your logic to check the size of Base64UrlEncodedIdentifier
        int size = determineSize(value);
        return size >= min && size <= max;
    }

    private int determineSize(Base64UrlEncodedIdentifier value) {
        // Your logic to determine the size of Base64UrlEncodedIdentifier
        // For example, the length of its string representation
        return value.getIdentifier().length();
    }
}
