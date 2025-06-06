package org.readora.readout.app.setting

import org.readora.readout.core.utils.Theme

data class SettingState(
    val currentTheme: String? = Theme.LIGHT_MODE.name,
    val showClearDataDialog: Boolean = false,
    val showThemeDialog: Boolean = false
)