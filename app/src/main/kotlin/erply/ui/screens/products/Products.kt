package erply.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ydanneg.erply.model.ErplyProduct
import com.ydanneg.erply.model.ProductName
import erply.ui.theme.ErplyThemePreviewSurface
import erply.ui.theme.PreviewThemes

@PreviewThemes
@Composable
fun ProductsScreenContentPreview() {
    val products = (1L..15L).map {
        ErplyProduct(
            id = it,
            name = ProductName("name$it"),
            groupId = it,
            price = "19.99",
            type = "PRODUCT"
        )
    }
    ErplyThemePreviewSurface {
        ProductsScreenContent(false, products)
    }
}

@Composable
fun ProductsScreen(viewModel: ProductsScreenViewModel) {
    val products by viewModel.products.collectAsState()
    ProductsScreenContent(
        isLoading = false,
        products = products,
        onLogOut = { viewModel.logOut() }
    )
}

@Composable
fun ProductsScreenContent(
    isLoading: Boolean = false,
    products: List<ErplyProduct>,
    onLogOut: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = onLogOut) {
            Text(text = "Log out")
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) {
                Box(modifier = Modifier.size(128.dp), contentAlignment = Alignment.Center) {
                    Text(text = it.name.en, textAlign = TextAlign.Center)
                }
            }
        }
    }
}