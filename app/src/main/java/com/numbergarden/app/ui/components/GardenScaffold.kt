package com.numbergarden.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.numbergarden.app.R
import com.numbergarden.app.ui.theme.LocalHighContrast

/**
 * Every screen shares the same frame: cream background, a predictable top bar,
 * and a single scrollable worksurface. Consistency is an accessibility feature.
 */
@Composable
fun GardenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onHome: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit) = {},
    content: @Composable (ColumnScope.() -> Unit),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        GardenTopBar(
            title = title,
            onBack = onBack,
            onHome = onHome,
            trailing = trailing,
        )
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            content()
        }
    }
}

@Composable
fun GardenTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onHome: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit) = {},
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                onBack != null -> TopBarIcon(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    description = stringResource(R.string.cd_back),
                    onClick = onBack,
                )

                onHome != null -> TopBarIcon(
                    icon = Icons.Filled.Home,
                    description = stringResource(R.string.cd_home),
                    onClick = onHome,
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
            )
            trailing()
        }
    }
}

@Composable
private fun TopBarIcon(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
fun ScreenColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(18.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

@Composable
fun RoundedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (LocalHighContrast.current) 2.dp else 1.dp,
            color = if (LocalHighContrast.current) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        ),
    ) {
        Column(content = content)
    }
}
