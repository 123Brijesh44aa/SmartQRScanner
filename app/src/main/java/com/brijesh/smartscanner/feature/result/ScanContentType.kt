package com.brijesh.smartscanner.feature.result

enum class ScanContentType(
    val title: String
) {
    WEB_URL("Website"),
    UPI_PAYMENT("UPI Payment"),
    EMAIL("Email Address"),
    PHONE("Phone Number"),
    WIFI("Wi-Fi Network"),
    TEXT("Plain Text");

    companion object {
        fun from(value: String): ScanContentType {
            val content = value.trim()

            return when {
                content.startsWith(
                    "upi://pay",
                    ignoreCase = true
                ) -> UPI_PAYMENT

                content.startsWith(
                    "WIFI:",
                    ignoreCase = true
                ) -> WIFI

                content.startsWith(
                    "mailto:",
                    ignoreCase = true
                ) -> EMAIL

                content.startsWith(
                    "tel:",
                    ignoreCase = true
                ) -> PHONE

                content.matches(
                    Regex(
                        """^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$""",
                        RegexOption.IGNORE_CASE
                    )
                ) -> EMAIL

                content.matches(
                    Regex("""^\+?[0-9][0-9 ()-]{6,}$""")
                ) -> PHONE

                isWebUrl(content) -> WEB_URL

                else -> TEXT
            }
        }

        private fun isWebUrl(value: String): Boolean {
            val candidate = if (
                value.startsWith("https://", true) ||
                value.startsWith("http://", true)
            ) {
                value
            } else if (
                value.startsWith("www.", true)
            ) {
                "https://$value"
            } else {
                return false
            }

            return try {
                val uri = android.net.Uri.parse(candidate)

                uri.scheme in listOf("http", "https") &&
                        !uri.host.isNullOrBlank() &&
                        !uri.host!!.contains(" ")
            } catch (_: Exception) {
                false
            }
        }
    }
}