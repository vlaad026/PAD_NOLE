package com.example.nole.features.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.ic_chat
import nole.shared.generated.resources.ic_home
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatsScreenRoot(
    viewModel: ChatsViewModel,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChatsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ChatsAction.OnBackClick -> onBackClick()
                ChatsAction.OnHomeClick -> onHomeClick()
                is ChatsAction.OnChatClick -> onChatClick(action.productId)
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun ChatsScreen(
    state: ChatsUiState,
    onAction: (ChatsAction) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .safeContentPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_chat),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Bandeja de entrada",
                color = colors.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_chat),
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aún no tienes conversaciones activas",
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(
                    items = state.conversations,
                    key = { _, conversation -> conversation.productId }
                ) { index, conversation ->
                    ConversationRow(
                        conversation = conversation,
                        onClick = { onAction(ChatsAction.OnChatClick(conversation.productId)) }
                    )
                    if (index < state.conversations.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 76.dp)
                                .height(1.dp)
                                .background(colors.outline)
                        )
                    }
                }
            }
        }

        ChatsBottomBar(
            onBackClick = { onAction(ChatsAction.OnBackClick) },
            onHomeClick = { onAction(ChatsAction.OnHomeClick) },
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: ChatConversation,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val initial = conversation.sellerUsername.firstOrNull()?.uppercase().orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = colors.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.sellerUsername,
                color = colors.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = conversation.lastMessage,
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ChatsBottomBar(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth().background(colors.surface)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.outline)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            ) {
                Text(
                    text = "❮ Volver",
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            TextButton(
                onClick = onHomeClick,
                modifier = Modifier.height(40.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_home),
                    contentDescription = "Inicio",
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Inicio",
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}
