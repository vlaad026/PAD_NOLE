package com.example.nole.Features.Dashboard


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nole.model.CatalogItem
import com.example.nole.model.MarketplaceSection
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.ic_chat
import nole.shared.generated.resources.ic_person
import nole.shared.generated.resources.ic_search
import nole.shared.generated.resources.nole_azul
import com.example.nole.core.ui.noleTextFieldColors
import com.example.nole.core.ui.resolveDrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel { DashboardViewModel() },
    onNavigateToLogin: () -> Unit = {},
    onNavigateToChats: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmLogout by remember { mutableStateOf(false) }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmLogout = false
                        viewModel.logout()
                    },
                ) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
    ) {
        MarketplaceHeader(
            state = state,
            onSectionSelected = viewModel::onSectionSelected,
            onSearchChanged = viewModel::onSearchChanged,
            onNavigateToChats = onNavigateToChats,
            onNavigateToLogin = onNavigateToLogin,
            onLogoutClick = { confirmLogout = true },
        )


        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (maxWidth >= 720.dp) {
                Row(modifier = Modifier.fillMaxSize()) {
                    FilterPanel(
                        state = state,
                        onCategoryChecked = viewModel::onCategoryChecked,
                        onConditionChecked = viewModel::onConditionChecked,
                        onPriceFromChanged = viewModel::onPriceFromChanged,
                        onPriceToChanged = viewModel::onPriceToChanged,
                        onApplyFilters = viewModel::onApplyFilters,
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    ProductGrid(
                        products = state.visibleProducts,
                        onProductClick = onProductClick,
                        modifier = Modifier.weight(1f).fillMaxSize()
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    FilterPanel(
                        state = state,
                        onCategoryChecked = viewModel::onCategoryChecked,
                        onConditionChecked = viewModel::onConditionChecked,
                        onPriceFromChanged = viewModel::onPriceFromChanged,
                        onPriceToChanged = viewModel::onPriceToChanged,
                        onApplyFilters = viewModel::onApplyFilters,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    ProductGrid(
                        products = state.visibleProducts,
                        onProductClick = onProductClick,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
private fun MarketplaceHeader(
    state: DashboardUiState,
    onSectionSelected: (MarketplaceSection) -> Unit,
    onSearchChanged: (String) -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.nole_azul),
                contentDescription = "NOLE",
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            SectionButton(
                text = MarketplaceSection.FUTBOL.title,
                selected = state.section == MarketplaceSection.FUTBOL,
                onClick = { onSectionSelected(MarketplaceSection.FUTBOL) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            SectionButton(
                text = MarketplaceSection.COCHES.title,
                selected = state.section == MarketplaceSection.COCHES,
                onClick = { onSectionSelected(MarketplaceSection.COCHES) }
            )
            Spacer(modifier = Modifier.weight(1f))
            HeaderActionButton(
                text = "Chats",
                icon = painterResource(Res.drawable.ic_chat),
                onClick = onNavigateToChats
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (state.currentUsername != null) {
                HeaderActionButton(
                    text = "Cerrar sesión",
                    icon = rememberVectorPainter(Icons.AutoMirrored.Filled.ExitToApp),
                    onClick = onLogoutClick,
                    contentColor = MaterialTheme.colorScheme.error,
                )
            } else {
                HeaderActionButton(
                    text = "Iniciar sesión",
                    icon = painterResource(Res.drawable.ic_person),
                    onClick = onNavigateToLogin
                )
            }
        }


        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text(state.section.searchHint) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            colors = noleFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
private fun HeaderActionButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
        border = BorderStroke(1.dp, contentColor),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}


@Composable
private fun SectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val label = @Composable {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            softWrap = false,
        )
    }
    if (selected) {
        Button(
            onClick = onClick,
            modifier = Modifier.height(40.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            label()
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.height(40.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        ) {
            label()
        }
    }
}


@Composable
private fun FilterPanel(
    state: DashboardUiState,
    onCategoryChecked: (String, Boolean) -> Unit,
    onConditionChecked: (String, Boolean) -> Unit,
    onPriceFromChanged: (String) -> Unit,
    onPriceToChanged: (String) -> Unit,
    onApplyFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = MaterialTheme.colorScheme.primary,
        uncheckedColor = MaterialTheme.colorScheme.outline,
        checkmarkColor = MaterialTheme.colorScheme.onPrimary
    )


    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Filtros",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.primary)
        )


        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Categorías",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        state.section.categories.forEach { category ->
            FilterCheckRow(
                label = category,
                checked = category in state.selectedCategories,
                checkboxColors = checkboxColors,
                onCheckedChange = { checked -> onCategoryChecked(category, checked) }
            )
        }


        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Estado",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        ProductConditions.forEach { condition ->
            FilterCheckRow(
                label = condition,
                checked = condition in state.selectedConditions,
                checkboxColors = checkboxColors,
                onCheckedChange = { checked -> onConditionChecked(condition, checked) }
            )
        }


        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Precio",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.priceFromInput,
                onValueChange = onPriceFromChanged,
                label = { Text("Desde") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = noleFieldColors(),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.priceToInput,
                onValueChange = onPriceToChanged,
                label = { Text("Hasta") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = noleFieldColors(),
                modifier = Modifier.weight(1f)
            )
        }


        state.filterError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }


        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onApplyFilters,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Aplicar filtros", fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
private fun FilterCheckRow(
    label: String,
    checked: Boolean,
    checkboxColors: CheckboxColors,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = checkboxColors
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun ProductGrid(
    products: List<CatalogItem>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (products.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "No hay artículos con esos filtros",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }


    LazyVerticalGrid(
        columns = GridCells.Adaptive(168.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}


@Composable
private fun ProductCard(
    product: CatalogItem,
    onClick: () -> Unit,
) {
    val image = resolveDrawableResource(product.gallery.firstOrNull() ?: product.imageName)


    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
                .background(Color(0xFFF3F5F8)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = product.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = product.priceLabel,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@Composable
private fun noleFieldColors() = noleTextFieldColors()
