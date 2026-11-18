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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nole.model.ChatMessage
import com.example.nole.core.ui.noleTextFieldColors
import com.example.nole.core.ui.resolveDrawableResource
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.ic_arrow_left
import nole.shared.generated.resources.ic_arrow_right
import nole.shared.generated.resources.ic_bag
import nole.shared.generated.resources.ic_chat
import nole.shared.generated.resources.ic_person
import nole.shared.generated.resources.ic_send
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource




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
            .background(MaterialTheme.colorScheme.background)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    ProductPane(
                        state = state,
                        onPreviousImage = viewModel::onPreviousImage,
                        onNextImage = viewModel::onNextImage,
                        onSelectImage = viewModel::onSelectImage,
                        onBuyClick = viewModel::onBuyClick,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline)
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary,
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
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissPurchase) {
                    Text(text = "Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            title = {
                Text(text = "Confirmar compra", color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Text(
                    text = "¿Quieres comprar \"${state.title}\" por ${state.priceLabel}?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
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
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.height(40.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        ) {
            Text(
                text = "❮ Volver",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onNavigateToChats,
            modifier = Modifier.height(40.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_chat),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Chats",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_person),
                contentDescription = "Iniciar sesión",
                tint = MaterialTheme.colorScheme.primary,
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
    onSelectImage: (Int) -> Unit,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val images = state.images.ifEmpty { listOf(state.imageName).filter { it.isNotBlank() } }
    val index = if (images.isEmpty()) 0 else state.imageIndex.mod(images.size)
    val hasGallery = images.size > 1
    val pagerState = rememberPagerState(initialPage = index, pageCount = { images.size.coerceAtLeast(1) })

    LaunchedEffect(index, images.size) {
        if (images.isNotEmpty() && pagerState.currentPage != index) {
            pagerState.scrollToPage(index)
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if (hasGallery && pagerState.currentPage != index) {
            onSelectImage(pagerState.currentPage)
        }
    }


    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F5F8))
        ) {
            if (images.isEmpty()) {
                Box(Modifier.fillMaxSize())
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = hasGallery,
                ) { page ->
                    Image(
                        painter = painterResource(resolveDrawableResource(images[page])),
                        contentDescription = state.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            if (hasGallery) {
                GalleryArrow(
                    icon = Res.drawable.ic_arrow_left,
                    description = "Imagen anterior",
                    onClick = onPreviousImage,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp),
                )
                GalleryArrow(
                    icon = Res.drawable.ic_arrow_right,
                    description = "Imagen siguiente",
                    onClick = onNextImage,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                )
                Text(
                    text = "${index + 1} / ${images.size}",
                    color = Color(0xFFF1F5F9),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC0F172A))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
        if (hasGallery) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                images.forEachIndexed { imagePosition, _ ->
                    val selected = imagePosition == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                            .clickable { onSelectImage(imagePosition) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                images.forEachIndexed { imagePosition, name ->
                    val selected = imagePosition == index
                    Image(
                        painter = painterResource(resolveDrawableResource(name)),
                        contentDescription = "Foto ${imagePosition + 1}",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F5F8))
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable { onSelectImage(imagePosition) }
                            .padding(4.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }


        Text(
            text = state.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = state.priceLabel,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = listOf(state.subCategory, state.condition).filter { it.isNotBlank() }.joinToString(" · "),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = state.description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )


        if (state.isSold) {
            Text(
                text = "Vendido",
                color = MaterialTheme.colorScheme.secondary,
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
private fun GalleryArrow(
    icon: DrawableResource,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xE6F3F5F8)),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
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


    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = sellerUsername,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
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
    val colors = MaterialTheme.colorScheme
    val ownMessage = !message.fromSeller
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromSeller) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (ownMessage) colors.tertiaryContainer else colors.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = if (ownMessage) colors.tertiary else colors.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (ownMessage) colors.onTertiaryContainer else colors.onSurface,
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
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChanged,
                placeholder = { Text("Envía un mensaje...") },
                singleLine = true,
                colors = fieldColors,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { if (canSend) onSend() },
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (canSend) colors.primary else colors.outline)
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "Enviar mensaje",
                    tint = if (canSend) colors.onPrimary else colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
private fun noleFieldColors() = noleTextFieldColors()

