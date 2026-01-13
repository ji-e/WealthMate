package com.jie.wealthmate.component.category.categoryIncome

import CategoryIncomePack
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

public val CategoryIncomePack.EmojiU1f303: ImageVector
    get() {
        if (_emojiU1f303 != null) {
            return _emojiU1f303!!
        }
        _emojiU1f303 = Builder(name = "EmojiU1f303", defaultWidth = 128.0.dp, defaultHeight =
                128.0.dp, viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = radialGradient(0.42f to Color(0xFFA8D793), 0.56f to Color(0xFF7DAD8B), 0.71f
                    to Color(0xFF4C7D82), 0.78f to Color(0xFF457986), 0.89f to Color(0xFF326C8F),
                    1.0f to Color(0xFF185B9D), center = Offset(66.52f,139.66f), radius = 134.88f),
                    stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin =
                    Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(116.62f, 124.26f)
                horizontalLineTo(11.32f)
                curveToRelative(-4.15f, 0.0f, -7.52f, -3.37f, -7.52f, -7.52f)
                verticalLineTo(11.44f)
                curveToRelative(0.0f, -4.15f, 3.37f, -7.52f, 7.52f, -7.52f)
                horizontalLineToRelative(105.3f)
                curveToRelative(4.15f, 0.0f, 7.52f, 3.37f, 7.52f, 7.52f)
                verticalLineToRelative(105.3f)
                curveTo(124.15f, 120.89f, 120.78f, 124.26f, 116.62f, 124.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6BA3AE)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(121.3f, 62.39f)
                lineToRelative(-19.0f, 0.0f)
                lineToRelative(0.0f, 21.86f)
                lineToRelative(-42.11f, 0.0f)
                lineToRelative(0.0f, -65.41f)
                lineToRelative(-31.54f, 0.0f)
                lineToRelative(0.0f, 65.41f)
                lineToRelative(-4.48f, 0.0f)
                lineToRelative(0.0f, -35.12f)
                lineToRelative(-11.83f, 0.0f)
                lineToRelative(0.0f, 42.65f)
                lineToRelative(9.5f, 0.0f)
                lineToRelative(0.0f, 25.44f)
                lineToRelative(97.13f, 0.0f)
                lineToRelative(0.0f, -11.11f)
                lineToRelative(2.33f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFEF7B0)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(96.24f, 17.86f)
                curveToRelative(-0.39f, 0.08f, 1.37f, 3.95f, 1.26f, 9.18f)
                curveToRelative(-0.15f, 7.02f, -5.64f, 11.51f, -12.17f, 11.34f)
                curveToRelative(-4.91f, -0.13f, -8.17f, -2.27f, -8.46f, -1.77f)
                curveToRelative(-0.12f, 0.21f, 0.83f, 4.71f, 5.46f, 7.16f)
                curveToRelative(7.23f, 3.82f, 14.32f, 1.6f, 18.23f, -2.54f)
                curveToRelative(3.8f, -4.02f, 5.15f, -10.58f, 2.87f, -16.14f)
                curveTo(101.03f, 19.26f, 96.7f, 17.77f, 96.24f, 17.86f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD420)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(108.95f, 42.14f)
                curveToRelative(0.28f, 0.33f, 3.76f, -0.06f, 3.76f, -0.06f)
                reflectiveCurveToRelative(1.88f, 2.71f, 2.27f, 2.71f)
                curveToRelative(0.66f, 0.0f, 1.33f, -3.48f, 1.33f, -3.48f)
                reflectiveCurveToRelative(3.15f, -0.18f, 3.26f, -0.61f)
                curveToRelative(0.15f, -0.6f, -2.71f, -2.65f, -2.71f, -2.65f)
                reflectiveCurveToRelative(0.54f, -3.32f, 0.06f, -3.6f)
                curveToRelative(-0.52f, -0.3f, -3.15f, 1.88f, -3.15f, 1.88f)
                reflectiveCurveTo(110.39f, 34.51f, 110.0f, 35.0f)
                curveToRelative(-0.3f, 0.38f, 0.88f, 3.82f, 0.88f, 3.82f)
                reflectiveCurveTo(108.75f, 41.9f, 108.95f, 42.14f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD420)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(65.86f, 25.16f)
                curveTo(66.05f, 25.6f, 69.7f, 26.0f, 69.7f, 26.0f)
                reflectiveCurveToRelative(1.33f, 3.6f, 1.75f, 3.69f)
                curveToRelative(0.71f, 0.17f, 2.21f, -3.32f, 2.21f, -3.32f)
                reflectiveCurveToRelative(3.66f, -0.05f, 3.87f, -0.5f)
                curveToRelative(0.37f, -0.79f, -2.35f, -3.17f, -2.35f, -3.17f)
                reflectiveCurveToRelative(1.68f, -3.43f, 0.97f, -3.85f)
                curveToRelative(-0.72f, -0.42f, -3.86f, 1.23f, -3.86f, 1.23f)
                reflectiveCurveToRelative(-3.12f, -2.74f, -3.71f, -2.38f)
                curveToRelative(-0.81f, 0.5f, 0.17f, 4.25f, 0.17f, 4.25f)
                reflectiveCurveTo(65.54f, 24.39f, 65.86f, 25.16f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD420)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.73f, 14.65f)
                curveToRelative(0.14f, 0.38f, 3.43f, 0.66f, 3.43f, 0.66f)
                reflectiveCurveToRelative(1.47f, 3.15f, 1.83f, 3.21f)
                curveToRelative(0.45f, 0.07f, 1.99f, -2.88f, 1.99f, -2.88f)
                reflectiveCurveToRelative(3.38f, -0.29f, 3.51f, -0.68f)
                curveToRelative(0.19f, -0.54f, -2.19f, -2.69f, -2.19f, -2.69f)
                reflectiveCurveToRelative(0.78f, -3.73f, 0.5f, -3.93f)
                curveToRelative(-0.36f, -0.25f, -3.43f, 1.38f, -3.43f, 1.38f)
                reflectiveCurveToRelative(-2.82f, -1.99f, -3.28f, -1.63f)
                curveToRelative(-0.43f, 0.33f, 0.29f, 3.79f, 0.29f, 3.79f)
                reflectiveCurveTo(9.49f, 14.0f, 9.73f, 14.65f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFD420)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(15.97f, 27.21f)
                curveToRelative(0.1f, 0.33f, 2.73f, 0.67f, 2.73f, 0.67f)
                reflectiveCurveToRelative(0.96f, 2.81f, 1.26f, 2.89f)
                curveToRelative(0.51f, 0.14f, 1.71f, -2.48f, 1.71f, -2.48f)
                reflectiveCurveToRelative(2.67f, 0.17f, 2.85f, -0.14f)
                curveToRelative(0.21f, -0.36f, -1.47f, -2.45f, -1.47f, -2.45f)
                reflectiveCurveToRelative(0.84f, -2.53f, 0.58f, -2.76f)
                curveToRelative(-0.28f, -0.23f, -2.8f, 0.8f, -2.8f, 0.8f)
                reflectiveCurveToRelative(-2.0f, -1.93f, -2.46f, -1.73f)
                curveToRelative(-0.4f, 0.17f, -0.25f, 3.05f, -0.25f, 3.05f)
                reflectiveCurveTo(15.83f, 26.74f, 15.97f, 27.21f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(32.41f, 25.13f)
                horizontalLineToRelative(2.37f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(37.46f, 25.13f)
                horizontalLineToRelative(2.47f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(45.27f, 25.13f)
                lineToRelative(-2.42f, 0.0f)
                lineToRelative(0.0f, 7.59f)
                lineToRelative(2.36f, -0.02f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(50.44f, 25.13f)
                lineToRelative(-2.6f, 0.01f)
                lineToRelative(0.07f, 7.58f)
                lineToRelative(2.51f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(32.41f, 25.13f)
                horizontalLineToRelative(2.37f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(37.46f, 25.13f)
                horizontalLineToRelative(2.47f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(45.31f, 25.13f)
                lineToRelative(-2.46f, 0.0f)
                lineToRelative(0.0f, 7.59f)
                lineToRelative(2.43f, -0.02f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(50.44f, 37.13f)
                lineToRelative(-2.6f, 0.01f)
                lineToRelative(0.07f, 7.58f)
                lineToRelative(2.51f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(32.41f, 37.13f)
                horizontalLineToRelative(2.37f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(37.46f, 37.13f)
                horizontalLineToRelative(2.47f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(45.31f, 37.13f)
                lineToRelative(-2.46f, 0.0f)
                lineToRelative(0.0f, 7.59f)
                lineToRelative(2.43f, -0.02f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(32.41f, 49.13f)
                horizontalLineToRelative(2.37f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(37.46f, 49.13f)
                horizontalLineToRelative(2.47f)
                verticalLineToRelative(7.59f)
                horizontalLineToRelative(-2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(45.31f, 49.13f)
                lineToRelative(-2.46f, 0.0f)
                lineToRelative(0.0f, 7.59f)
                lineToRelative(2.43f, -0.02f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(53.36f, 25.13f)
                lineToRelative(-0.01f, 7.59f)
                lineToRelative(2.56f, 0.0f)
                lineToRelative(0.0f, -7.59f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(105.55f, 81.2f)
                horizontalLineToRelative(2.88f)
                verticalLineToRelative(3.94f)
                horizontalLineToRelative(-2.88f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(111.3f, 81.2f)
                horizontalLineToRelative(2.88f)
                verticalLineToRelative(3.94f)
                horizontalLineToRelative(-2.88f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(105.55f, 73.83f)
                horizontalLineToRelative(2.88f)
                verticalLineToRelative(3.94f)
                horizontalLineToRelative(-2.88f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(111.3f, 73.83f)
                horizontalLineToRelative(2.88f)
                verticalLineToRelative(3.94f)
                horizontalLineToRelative(-2.88f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(105.55f, 66.83f)
                horizontalLineToRelative(2.88f)
                verticalLineToRelative(3.94f)
                horizontalLineToRelative(-2.88f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(111.3f, 66.83f)
                horizontalLineToRelative(2.88f)
                verticalLineToRelative(3.94f)
                horizontalLineToRelative(-2.88f)
                close()
            }
            path(fill = radialGradient(0.04f to Color(0xFF163387), 0.38f to Color(0xFF163284), 0.62f
                    to Color(0xFF17317B), 0.83f to Color(0xFF182E6B), 1.0f to Color(0xFF1A2B59),
                    center = Offset(66.09f,-50.95f), radius = 173.05f), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(115.0f, 49.4f)
                curveToRelative(-0.36f, 0.73f, 0.0f, 42.7f, 0.0f, 42.7f)
                horizontalLineTo(99.13f)
                verticalLineTo(68.1f)
                horizontalLineToRelative(-5.11f)
                verticalLineToRelative(-7.67f)
                horizontalLineToRelative(-4.53f)
                verticalLineToRelative(-6.51f)
                horizontalLineToRelative(-8.16f)
                verticalLineToRelative(6.84f)
                horizontalLineToRelative(-5.19f)
                verticalLineToRelative(7.17f)
                horizontalLineToRelative(-5.36f)
                verticalLineToRelative(34.95f)
                horizontalLineToRelative(-5.04f)
                verticalLineToRelative(-39.0f)
                horizontalLineToRelative(2.35f)
                verticalLineToRelative(-7.44f)
                horizontalLineToRelative(-2.35f)
                verticalLineTo(41.03f)
                horizontalLineToRelative(-2.65f)
                lineToRelative(-3.61f, -11.16f)
                horizontalLineToRelative(-7.11f)
                lineTo(48.8f, 41.03f)
                horizontalLineToRelative(-2.79f)
                verticalLineToRelative(15.41f)
                horizontalLineToRelative(-2.48f)
                verticalLineToRelative(7.44f)
                horizontalLineToRelative(2.48f)
                verticalLineToRelative(39.0f)
                horizontalLineToRelative(-7.18f)
                verticalLineTo(67.83f)
                horizontalLineTo(17.19f)
                verticalLineTo(28.47f)
                curveToRelative(0.0f, 0.0f, -13.35f, -0.09f, -13.39f, -0.05f)
                verticalLineToRelative(88.32f)
                curveToRelative(0.0f, 4.15f, 3.37f, 7.52f, 7.52f, 7.52f)
                horizontalLineToRelative(105.3f)
                curveToRelative(4.15f, 0.0f, 7.52f, -3.37f, 7.52f, -7.52f)
                verticalLineTo(49.4f)
                horizontalLineTo(115.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 116.21f)
                horizontalLineToRelative(29.97f)
                verticalLineToRelative(2.35f)
                horizontalLineToRelative(-29.97f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 107.86f)
                horizontalLineToRelative(29.97f)
                verticalLineToRelative(2.35f)
                horizontalLineToRelative(-29.97f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 99.21f)
                horizontalLineToRelative(29.97f)
                verticalLineToRelative(2.35f)
                horizontalLineToRelative(-29.97f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 90.86f)
                horizontalLineToRelative(29.97f)
                verticalLineToRelative(2.35f)
                horizontalLineToRelative(-29.97f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 81.21f)
                horizontalLineToRelative(29.97f)
                verticalLineToRelative(2.35f)
                horizontalLineToRelative(-29.97f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 72.86f)
                horizontalLineToRelative(29.97f)
                verticalLineToRelative(2.35f)
                horizontalLineToRelative(-29.97f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 63.32f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.71f, 63.32f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 55.89f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.71f, 55.89f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 48.46f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.71f, 48.46f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 41.03f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.71f, 41.03f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.71f, 33.59f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.71f, 33.59f)
                horizontalLineToRelative(1.76f)
                verticalLineToRelative(3.7f)
                horizontalLineToRelative(-1.76f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(49.8f, 64.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.18f, 64.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(60.36f, 64.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(49.8f, 73.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(49.8f, 97.7f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.18f, 97.7f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(60.36f, 97.7f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.18f, 73.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(60.36f, 73.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(49.8f, 82.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.18f, 82.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(60.36f, 82.51f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(4.76f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(54.01f, 53.96f)
                horizontalLineToRelative(4.66f)
                verticalLineToRelative(-6.92f)
                curveToRelative(0.0f, 0.0f, 0.17f, -2.6f, -2.35f, -2.66f)
                curveToRelative(-2.37f, -0.06f, -2.4f, 2.6f, -2.4f, 2.6f)
                reflectiveCurveTo(54.07f, 53.96f, 54.01f, 53.96f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(79.85f, 64.16f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(9.69f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(88.36f, 64.16f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(9.69f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(84.15f, 64.16f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(9.69f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(76.37f, 77.58f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(84.19f, 77.58f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.01f, 77.58f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(76.37f, 87.58f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(84.19f, 87.58f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.01f, 87.58f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(76.37f, 97.03f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(84.19f, 97.03f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.01f, 97.03f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(76.37f, 107.03f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDE064)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(84.19f, 107.03f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.01f, 107.03f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(76.37f, 116.5f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(84.19f, 116.5f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3F737B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.01f, 116.5f)
                horizontalLineToRelative(2.27f)
                verticalLineToRelative(3.09f)
                horizontalLineToRelative(-2.27f)
                close()
            }
        }
        .build()
        return _emojiU1f303!!
    }

private var _emojiU1f303: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = CategoryIncomePack.EmojiU1f303, contentDescription = "")
    }
}
