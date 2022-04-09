package it.polito.wa2.group03userregistration.enums

enum class ActivationStatus {
    SUCCESSFUL,
    EXPIRED,
    ID_DOES_NOT_EXIST,
    WRONG_ACTIVATION_CODE
}

val ActivationMessages = mapOf<ActivationStatus, String>(
    ActivationStatus.SUCCESSFUL to "User successfully activated",
    ActivationStatus.EXPIRED to "Activation code expired",
    ActivationStatus.ID_DOES_NOT_EXIST to "Provisional activation ID does not exist",
    ActivationStatus.WRONG_ACTIVATION_CODE to "Wrong activation code"
)