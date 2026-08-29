package io.github.drlacheheb.mqtlin.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 1:1 Exact Official Google Material Symbols Outlined Icons
 * Source SVG: folder_open_24dp_E3E3E3_FILL0_wght400_GRAD0_opsz24.svg
 *             folder_24dp_E3E3E3_FILL0_wght400_GRAD0_opsz24.svg
 */
object MqtlinSymbols {
    private var _folderOpen: ImageVector? = null
    private var _folder: ImageVector? = null

    /**
     * Exact 1:1 SVG path from Google Material Symbols folder_open:
     * M160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h240l80 80h320q33 0 56.5 23.5T880-640H447l-80-80H160v480l96-320h684L837-217q-8 26-29.5 41.5T760-160H160Zm84-80h516l72-240H316l-72 240Zm0 0 72-240-72 240Zm-84-400v-80 80Z
     */
    val FolderOpen: ImageVector
        get() {
            if (_folderOpen != null) return _folderOpen!!
            _folderOpen = ImageVector.Builder(
                name = "folder_open_24dp_E3E3E3_FILL0_wght400_GRAD0_opsz24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(fill = SolidColor(Color.White)) {
                    // Outer container + open flap
                    moveTo(160f, 800f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(80f, 720f)
                    verticalLineToRelative(-480f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(160f, 160f)
                    horizontalLineToRelative(240f)
                    lineToRelative(80f, 80f)
                    horizontalLineToRelative(320f)
                    quadToRelative(33f, 0f, 56.5f, 23.5f)
                    reflectiveQuadTo(880f, 320f)
                    horizontalLineTo(447f)
                    lineToRelative(-80f, -80f)
                    horizontalLineTo(160f)
                    verticalLineToRelative(480f)
                    lineToRelative(96f, -320f)
                    horizontalLineToRelative(684f)
                    lineTo(837f, 743f)
                    quadToRelative(-8f, 26f, -29.5f, 41.5f)
                    reflectiveQuadTo(760f, 800f)
                    horizontalLineTo(160f)
                    close()

                    // Inner opening
                    moveToRelative(84f, -80f)
                    horizontalLineToRelative(516f)
                    lineToRelative(72f, -240f)
                    horizontalLineTo(316f)
                    lineToRelative(-72f, 240f)
                    close()

                    moveToRelative(0f, 0f)
                    lineToRelative(72f, -240f)
                    lineToRelative(-72f, 240f)
                    close()

                    moveToRelative(-84f, -400f)
                    verticalLineToRelative(-80f)
                    verticalLineToRelative(80f)
                    close()
                }
            }.build()
            return _folderOpen!!
        }

    /**
     * Exact 1:1 SVG path from Google Material Symbols folder (closed):
     * M160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h240l80 80h320q33 0 56.5 23.5T880-640v400q0 33-23.5 56.5T800-160H160Zm0-80h640v-400H447l-80-80H160v480Zm0 0v-480 480Z
     */
    val Folder: ImageVector
        get() {
            if (_folder != null) return _folder!!
            _folder = ImageVector.Builder(
                name = "folder_24dp_E3E3E3_FILL0_wght400_GRAD0_opsz24",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(fill = SolidColor(Color.White)) {
                    moveTo(160f, 800f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(80f, 720f)
                    verticalLineToRelative(-480f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(160f, 160f)
                    horizontalLineToRelative(240f)
                    lineToRelative(80f, 80f)
                    horizontalLineToRelative(320f)
                    quadToRelative(33f, 0f, 56.5f, 23.5f)
                    reflectiveQuadTo(880f, 320f)
                    verticalLineToRelative(400f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(800f, 800f)
                    horizontalLineTo(160f)
                    close()

                    moveToRelative(0f, -80f)
                    horizontalLineToRelative(640f)
                    verticalLineToRelative(-400f)
                    horizontalLineTo(447f)
                    lineToRelative(-80f, -80f)
                    horizontalLineTo(160f)
                    verticalLineToRelative(480f)
                    close()

                    moveToRelative(0f, 0f)
                    verticalLineToRelative(-480f)
                    verticalLineToRelative(480f)
                    close()
                }
            }.build()
            return _folder!!
        }
}
