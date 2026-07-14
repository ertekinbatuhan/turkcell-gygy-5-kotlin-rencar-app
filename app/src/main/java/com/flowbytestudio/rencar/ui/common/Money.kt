package com.flowbytestudio.rencar.ui.common

import java.util.Locale

private val turkish = Locale("tr", "TR")

// "₺${formatTl(x)}" biçiminde kullanılır; tam sayılar kuruşsuz gösterilir.
fun formatTl(value: Double): String = if (value % 1.0 == 0.0) {
    String.format(turkish, "%,d", value.toLong())
} else {
    String.format(turkish, "%,.2f", value)
}
