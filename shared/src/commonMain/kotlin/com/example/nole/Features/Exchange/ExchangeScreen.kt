package com.example.nole.Features.Exchange


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nole.model.ChatMessage
import com.example.nole.model.MarketplaceSection
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.ic_arrow_left
import nole.shared.generated.resources.ic_arrow_right
import nole.shared.generated.resources.ic_bag
import nole.shared.generated.resources.ic_chat
import nole.shared.generated.resources.ic_person
import nole.shared.generated.resources.ic_send
import nole.shared.generated.resources.nole_negro
import nole.shared.generated.resources.nole_rosa
import org.jetbrains.compose.resources.painterResource


private val NolePink = Color(0xFFFF1493)
private val NoleCard = Color(0xFF101010)
private val NoleLine = Color(0xFF2A2A2A)


@Composable
fun ExchangeScreen(
    productId: String,
    viewModel: ExchangeViewModel = viewModel(key = productId) { ExchangeViewModel(productId) },
    onBackClick: () -> Unit = {},
    onNavigateToChats: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(state.purchaseMessage) {
        val message = state.purchaseMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onPurchaseMessageConsumed()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeContentPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ExchangeHeader(
                onBackClick = onBackClick,
                onNavigateToChats = onNavigateToChats,
                onNavigateToLogin = onNavigateToLogin,
            )


            if (state.isMissing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Artículo no disponible",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    ProductPane(
                        state = state,
                        onPreviousImage = viewModel::onPreviousImage,
                        onNextImage = viewModel::onNextImage,
                        onBuyClick = viewModel::onBuyClick,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(NoleLine)
                    )
                    ChatPane(
                        sellerUsername = state.sellerUsername,
                        messages = state.messages,
                        draft = state.draftMessage,
                        onDraftChanged = viewModel::onDraftChanged,
                        onSend = viewModel::onSendMessage,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }


        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF161616),
                contentColor = NolePink,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }


    if (state.showPurchaseDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissPurchase,
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmPurchase) {
                    Text(
                        text = "Aceptar",
                        color = NolePink,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissPurchase) {
                    Text(text = "Cancelar", color = Color.White)
                }
            },
            title = {
                Text(text = "Confirmar compra", color = Color.White)
            },
            text = {
                Text(
                    text = "¿Quieres comprar \"${state.title}\" por ${state.priceLabel}?",
                    color = Color.LightGray
                )
            },
            containerColor = Color(0xFF121212)
        )
    }
}


@Composable
private fun ExchangeHeader(
    onBackClick: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBackClick) {
            Text(
                text = "❮ Volver",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onNavigateToChats,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            border = BorderStroke(1.dp, NolePink),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NolePink)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_chat),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Chats", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        IconButton(onClick = onNavigateToLogin) {
            Icon(
                painter = painterResource(Res.drawable.ic_person),
                contentDescription = "Iniciar sesión",
                tint = NolePink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
private fun ProductPane(
    state: ExchangeUiState,
    onPreviousImage: () -> Unit,
    onNextImage: () -> Unit,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val productImage = when {
        state.imageIndex % 2 == 0 && state.section == MarketplaceSection.VEHICULOS -> Res.drawable.nole_negro
        state.imageIndex % 2 == 0 -> Res.drawable.nole_rosa
        state.section == MarketplaceSection.VEHICULOS -> Res.drawable.nole_rosa
        else -> Res.drawable.nole_negro
    }


    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousImage) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = "Imagen anterior",
                    tint = NolePink,
                    modifier = Modifier.size(28.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NoleCard)
                    .border(1.dp, NolePink, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(productImage),
                    contentDescription = state.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
            IconButton(onClick = onNextImage) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_right),
                    contentDescription = "Imagen siguiente",
                    tint = NolePink,
                    modifier = Modifier.size(28.dp)
                )
            }
        }


        Text(
            text = state.title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = state.priceLabel,
            color = NolePink,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = state.location,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = state.size,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = state.description,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )


        if (state.isSold) {
            Text(
                text = "Vendido",
                color = NolePink,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Button(
                onClick = onBuyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NolePink,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_bag),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Comprar",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Composable
private fun ChatPane(
    sellerUsername: String,
    messages: List<ChatMessage>,
    draft: String,
    onDraftChanged: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fieldColors = noleFieldColors()
    val initial = sellerUsername.firstOrNull()?.uppercase().orEmpty()


    Column(modifier = modifier.background(Color.Black)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NolePink),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = sellerUsername,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NolePink.copy(alpha = 0.45f))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            messages.forEach { message ->
                MessageBubble(message)
            }
        }
        MessageComposer(
            draft = draft,
            fieldColors = fieldColors,
            onDraftChanged = onDraftChanged,
            onSend = onSend
        )
    }
}


@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromSeller) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (message.fromSeller) NoleCard else NolePink)
                .border(
                    width = 1.dp,
                    color = if (message.fromSeller) NoleLine else NolePink,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
private fun MessageComposer(
    draft: String,
    fieldColors: TextFieldColors,
    onDraftChanged: (String) -> Unit,
    onSend: () -> Unit,
) {
    val canSend = draft.isNotBlank()


    Column(modifier = Modifier.fillMaxWidth().background(Color.Black)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(NoleLine)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChanged,
                placeholder = { Text("Envía un mensaje...") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (canSend) NolePink else Color.DarkGray)
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "Enviar mensaje",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
private fun noleFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NolePink,
    unfocusedBorderColor = Color.DarkGray,
    focusedLabelColor = NolePink,
    unfocusedLabelColor = Color.LightGray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = NolePink,
    focusedPlaceholderColor = Color.Gray,
    unfocusedPlaceholderColor = Color.Gray,
)

