package com.example.nole.Features.Chats


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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.ic_chat
import nole.shared.generated.resources.ic_home
import org.jetbrains.compose.resources.painterResource


private val NolePink = Color(0xFFFF1493)
private val NoleLine = Color(0xFF2A2A2A)


@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel = viewModel { ChatsViewModel() },
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                tint = NolePink,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Bandeja de entrada",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }


        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(
                items = state.conversations,
                key = { _, conversation -> conversation.productId }
            ) { index, conversation ->
                ConversationRow(
                    conversation = conversation,
                    onClick = { onChatClick(conversation.productId) }
                )
                if (index < state.conversations.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 76.dp)
                            .height(1.dp)
                            .background(NoleLine)
                    )
                }
            }
        }


        ChatsBottomBar(
            onBackClick = onBackClick,
            onHomeClick = onHomeClick
        )
    }
}


@Composable
private fun ConversationRow(
    conversation: ChatConversation,
    onClick: () -> Unit,
) {
    val initial = conversation.sellerUsername.firstOrNull()?.uppercase().orEmpty()


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NolePink),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = conversation.sellerUsername,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@Composable
private fun ChatsBottomBar(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF0D0D0D))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NolePink.copy(alpha = 0.45f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text(
                    text = "❮ Volver",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            TextButton(onClick = onHomeClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_home),
                    contentDescription = "Inicio",
                    tint = NolePink,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Inicio",
                    color = NolePink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
