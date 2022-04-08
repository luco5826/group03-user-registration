package it.polito.wa2.group03userregistration.enums

enum class UserValidationStatus {
    VALID,
    NO_USERNAME,
    NO_PASSWORD,
    NO_EMAIL,
    USERNAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS,
    WEAK_PASSWORD,
    INVALID_EMAIL
}

val UserValidationMessages = mapOf<UserValidationStatus, String>(
        UserValidationStatus.VALID to "Successfully validated",
        UserValidationStatus.NO_USERNAME to "Username not present",
        UserValidationStatus.NO_PASSWORD to "Password not present",
        UserValidationStatus.NO_EMAIL to "Email not present",
        UserValidationStatus.USERNAME_ALREADY_EXISTS to "Username already exists",
        UserValidationStatus.EMAIL_ALREADY_EXISTS to "Email already exists",
        UserValidationStatus.WEAK_PASSWORD to "Password is too weak",
        UserValidationStatus.INVALID_EMAIL to "Email is not valid"
)