package com.flowbytestudio.rencar.ui.screens.onboarding

enum class OnboardingIllustration {
    MAP_PIN_WITH_CAR,
    PHONE_VERIFICATION,
    MOVING_CAR,
}

data class OnboardingPage(
    val illustration: OnboardingIllustration,
    val title: String,
    val description: String,
)

val OnboardingPages = listOf(
    OnboardingPage(
        illustration = OnboardingIllustration.MAP_PIN_WITH_CAR,
        title = "Yakındaki aracı keşfet",
        description = "İhtiyacın olan araç birkaç dokunuş uzağında.",
    ),
    OnboardingPage(
        illustration = OnboardingIllustration.PHONE_VERIFICATION,
        title = "Dakikalar içinde kirala",
        description = "Ehliyetini doğrula, rezervasyonunu oluştur ve hemen yola çık.",
    ),
    OnboardingPage(
        illustration = OnboardingIllustration.MOVING_CAR,
        title = "Özgürce sür",
        description = "İstediğin zaman teslim et. Araç sahibi olmadan ulaşımın keyfini çıkar.",
    ),
)

data class OnboardingUiState(
    val currentPage: Int = 0,
) {
    val isLastPage: Boolean get() = currentPage == OnboardingPages.lastIndex
}
