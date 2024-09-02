package com.example.samplenfcreading.presentation.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.samplenfcreading.R

@Composable
fun MenuItem(
    modifier : Modifier = Modifier,
    @StringRes title: Int,
    @DrawableRes image: Int,
    enabled : Boolean = true,
    iconColor : Color = MaterialTheme.colorScheme.background,
    iconBackgroundColor : Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = { }
) {
    MenuItem(
        modifier = modifier,
        image = image,
        iconColor = iconColor,
        enabled = enabled,
        iconBackgroundColor = iconBackgroundColor,
        onClick = onClick
    ){
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun MenuItem(
    modifier : Modifier = Modifier,
    @DrawableRes image: Int,
    enabled: Boolean = true,
    iconColor : Color = MaterialTheme.colorScheme.background,
    iconBackgroundColor : Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = { },
    content : @Composable (RowScope) -> Unit
) {

    ElevatedButton(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(size = 16.dp),
        onClick = onClick,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 4.dp
        ),
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBackgroundColor)
                    .padding(8.dp),
            ) {
                Icon(
                    tint = iconColor,
                    painter = painterResource(id = image),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            content(this)

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                tint = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                painter = painterResource(id = R.drawable.arrow_next),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
            )
        }
    }
}