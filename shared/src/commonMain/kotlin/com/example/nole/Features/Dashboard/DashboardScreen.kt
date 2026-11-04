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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import nole.shared.generated.resources.nole_negro
import nole.shared.generated.resources.nole_rosa
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


val NolePink = Color(0xFFFF1493)


private val NoleCard = Color(0xFF101010)
private val NoleLine = Color(0xFF2A2A2A)


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel { DashboardViewModel() },
    onNavigateToLogin: () -> Unit = {},
    onNavigateToChats: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeContentPadding()
    ) {
        MarketplaceHeader(
            state = state,
            onSectionSelected = viewModel::onSectionSelected,
            onSearchChanged = viewModel::onSearchChanged,
            onNavigateToChats = onNavigateToChats,
            onNavigateToLogin = onNavigateToLogin,
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
                            .background(NoleLine)
                    )
                    ProductGrid(
                        products = state.visibleProducts,
                        section = state.section,
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
                            .background(NoleLine)
                    )
                    ProductGrid(
                        products = state.visibleProducts,
                        section = state.section,
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
                painter = painterResource(Res.drawable.nole_rosa),
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
                text = MarketplaceSection.VEHICULOS.title,
                selected = state.section == MarketplaceSection.VEHICULOS,
                onClick = { onSectionSelected(MarketplaceSection.VEHICULOS) }
            )
            Spacer(modifier = Modifier.weight(1f))
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderActionButton(
                text = "Chats",
                icon = Res.drawable.ic_chat,
                onClick = onNavigateToChats
            )
            Spacer(modifier = Modifier.width(8.dp))
            HeaderActionButton(
                text = "Iniciar sesión",
                icon = Res.drawable.ic_person,
                onClick = onNavigateToLogin
            )
        }


        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text(state.section.searchHint) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    tint = Color.Gray
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
    icon: DrawableResource,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        border = BorderStroke(1.dp, NolePink),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = NolePink)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}


@Composable
private fun SectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NolePink,
                contentColor = Color.White
            )
        ) {
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, NolePink),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NolePink)
        ) {
            Text(text = text, fontWeight = FontWeight.Bold)
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
        checkedColor = NolePink,
        uncheckedColor = Color.Gray,
        checkmarkColor = Color.White
    )


    Column(
        modifier = modifier
            .background(Color(0xFF0A0A0A))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Filtros",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(2.dp)
                .background(NolePink)
        )


        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Categorías",
            color = Color.White,
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
            color = Color.White,
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
            color = Color.White,
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
                color = Color(0xFFFF8A80),
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
                containerColor = NolePink,
                contentColor = Color.White
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
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun ProductGrid(
    products: List<CatalogItem>,
    section: MarketplaceSection,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (products.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "No hay artículos con esos filtros",
                color = Color.LightGray,
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
                section = section,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}


@Composable
private fun ProductCard(
    product: CatalogItem,
    section: MarketplaceSection,
    onClick: () -> Unit,
) {
    val image = if (section == MarketplaceSection.FUTBOL) {
        Res.drawable.nole_rosa
    } else {
        Res.drawable.nole_negro
    }


    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NoleCard),
        border = BorderStroke(width = 1.dp, color = Color(0xFF3A3A3A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF161616))
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = product.title,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(84.dp),
                contentScale = ContentScale.Fit
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = product.title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = product.priceLabel,
                color = NolePink,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
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
