package kz.labka.tapsyrma.domain

import java.time.LocalDate

data class ValidationResult(
    val titleError: String? = null,
    val descriptionError: String? = null,
    val dueDateError: String? = null,
) {
    val isValid: Boolean
        get() = titleError == null && descriptionError == null && dueDateError == null
}

/** Пайдаланушы енгізген мәндерді сақтау алдында тексереді. */
object TaskValidator {
    const val TITLE_MIN = 3
    const val TITLE_MAX = 60
    const val DESCRIPTION_MAX = 300

    /**
     * @param originalDueDate өзгертілетін тапсырманың бұрынғы мерзімі. Ол өзгермесе,
     * мерзімі өтіп кеткен тапсырманы да сақтауға рұқсат етіледі.
     */
    fun validate(
        title: String,
        description: String,
        dueDate: LocalDate?,
        today: LocalDate,
        originalDueDate: LocalDate? = null,
    ): ValidationResult {
        val cleanTitle = title.trim()
        val titleError = when {
            cleanTitle.isEmpty() -> "Тапсырма атауын енгізіңіз"
            cleanTitle.length < TITLE_MIN -> "Атауы кемінде $TITLE_MIN таңбадан тұруы керек"
            cleanTitle.length > TITLE_MAX -> "Атауы $TITLE_MAX таңбадан аспауы керек"
            else -> null
        }
        val descriptionError =
            if (description.trim().length > DESCRIPTION_MAX) "Сипаттама $DESCRIPTION_MAX таңбадан аспауы керек" else null
        val dueDateError =
            if (dueDate != null && dueDate.isBefore(today) && dueDate != originalDueDate) {
                "Мерзімі ретінде өткен күнді таңдауға болмайды"
            } else {
                null
            }
        return ValidationResult(titleError, descriptionError, dueDateError)
    }
}
