package com.brijesh.smartscanner.feature.generator

object QrContentBuilder {

    fun wifi(
        ssid: String,
        password: String,
        security: WifiSecurity = WifiSecurity.WPA
    ): String {
        require(ssid.isNotBlank()) {
            "Network name cannot be empty"
        }

        val authentication = when (security) {
            WifiSecurity.WPA -> "WPA"
            WifiSecurity.WEP -> "WEP"
            WifiSecurity.NONE -> "nopass"
        }

        val encodedPassword = if (security == WifiSecurity.NONE) {
            ""
        } else {
            escapeWifiValue(password)
        }

        require(
            security == WifiSecurity.NONE || password.isNotEmpty()
        ) {
            "Password cannot be empty for secured networks"
        }

        return "WIFI:T:$authentication;S:${escapeWifiValue(ssid)};P:$encodedPassword;;"
    }

    private fun escapeWifiValue(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace(":", "\\:")
            .replace("\"", "\\\"")
    }

    fun contact(
        name: String,
        phone: String,
        email: String
    ): String {
        require(name.isNotBlank()) {
            "Contact name cannot be empty"
        }

        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            appendLine("FN:${escapeVCardValue(name)}")

            if (phone.isNotBlank()) {
                appendLine("TEL;TYPE=CELL:${escapeVCardValue(phone)}")
            }

            if (email.isNotBlank()) {
                appendLine("EMAIL:${escapeVCardValue(email)}")
            }

            append("END:VCARD")
        }
    }

    fun upi(
        upiId: String,
        payeeName: String,
        amount: String = ""
    ): String {
        require(upiId.isNotBlank()) {
            "UPI ID cannot be empty"
        }

        require('@' in upiId && upiId.none { it.isWhitespace() }) {
            "Enter a valid-looking UPI ID, such as name@bank"
        }

        val parsedAmount = if (amount.isBlank()) {
            null
        } else {
            amount.toBigDecimalOrNull()
                ?.takeIf { it > java.math.BigDecimal.ZERO }
                ?: throw IllegalArgumentException(
                    "Amount must be greater than zero"
                )
        }

        return android.net.Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", upiId.trim())
            .appendQueryParameter("pn", payeeName.trim())
            .apply {
                if (parsedAmount != null) {
                    appendQueryParameter("am", parsedAmount.toPlainString())
                    appendQueryParameter("cu", "INR")
                }
            }
            .build()
            .toString()
    }
}

private fun escapeVCardValue(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace(";", "\\;")
        .replace(",", "\\,")
}

enum class WifiSecurity {
    WPA,
    WEP,
    NONE
}