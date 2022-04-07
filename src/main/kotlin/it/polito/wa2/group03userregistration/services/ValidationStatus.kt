package it.polito.wa2.group03userregistration.services

enum class ValidationStatus {
    VALID,
    NO_USERNAME,
    NO_PASSWORD,
    NO_EMAIL,
    USERNAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS,
    WEAK_PASSWORD,
    INVALID_EMAIL
}

var ValidationMessages = mapOf<ValidationStatus, String>(
        ValidationStatus.VALID to "Successfully validated",
        ValidationStatus.NO_USERNAME to "Username not present",
        ValidationStatus.NO_PASSWORD to "Password not present",
        ValidationStatus.NO_EMAIL to "Email not present",
        ValidationStatus.USERNAME_ALREADY_EXISTS to "Username already exists",
        ValidationStatus.EMAIL_ALREADY_EXISTS to "Email already exists",
        ValidationStatus.WEAK_PASSWORD to "Password is too weak",
        ValidationStatus.INVALID_EMAIL to "Email is not valid"
)