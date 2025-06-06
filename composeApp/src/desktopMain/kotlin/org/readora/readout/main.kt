package org.readora.readout

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.stringResource
import org.readora.readout.app.App
import org.readora.readout.core.di.initKoin
import org.readora.readout.core.theme.defaultWindowHeight
import org.readora.readout.core.theme.defaultWindowWidth
import org.readora.readout.core.theme.minWindowHeight
import org.readora.readout.core.theme.minWindowWidth
import readout.composeapp.generated.resources.Res
import readout.composeapp.generated.resources.app_name
import java.awt.Dimension

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = WindowState(
                position = WindowPosition(Alignment.Center),
                size = DpSize(defaultWindowWidth, defaultWindowHeight),
            ),
            title = stringResource(Res.string.app_name),
        ) {
            window.minimumSize = Dimension(minWindowWidth, minWindowHeight)
            App()
        }
    }
}