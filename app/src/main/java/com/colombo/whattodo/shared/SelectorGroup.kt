package com.colombo.whattodo.shared

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun SelectorGroup(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    selectedIndices: List<Int> = listOf(selectedIndex),
    onSelectedChange: (Int) -> Unit,
    exclusive: Boolean = true,
    color: Color?,
    disabled: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.then(
        if (disabled) Modifier.alpha(0.5f) else Modifier
    )) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        var boxWidth by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
                .onGloballyPositioned { coordinates ->
                    boxWidth = coordinates.size.width
                }
        ) {
            val indicatorOffset by animateDpAsState(
                targetValue = (selectedIndex * (boxWidth / options.size)).dp,
                label = "selector indicator"
            )

            val indicatorWidth by animateFloatAsState(
                targetValue = selectedIndices.size * 1f / options.size,
                label = "selector indicator width"
            )

            // Sliding indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth(indicatorWidth)
                    .padding(1.dp)
                    .offset(x = if (!exclusive) 0.dp else indicatorOffset / 2.65f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color ?: MaterialTheme.colorScheme.secondary)
                    .height(40.dp)
                    .zIndex(0f)
            )

            // Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                options.forEachIndexed { index, option ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .zIndex(2f)
                    ) {
                        TextButton(
                            onClick = { if (!disabled) onSelectedChange(index) },
                            enabled = !disabled,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = option,
                                color = if (selectedIndices.contains(index)) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}