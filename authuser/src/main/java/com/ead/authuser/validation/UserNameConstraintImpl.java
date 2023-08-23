package com.ead.authuser.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNameConstraintImpl implements ConstraintValidator<UserNameConstraint, String> {

    @Override
    public void initialize(final UserNameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String userName, final ConstraintValidatorContext constraintValidatorContext) {
        if (userName == null || userName.trim().isEmpty() || userName.contains(" ")) {
            return false;
        }
        return true;
    }
}
