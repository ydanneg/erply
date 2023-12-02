package erply.ui.screens.groups

import androidx.compose.foundation.clickable
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
import com.ydanneg.erply.model.ErplyProductGroup
import com.ydanneg.erply.model.LocalizedValue
import erply.ui.theme.ErplyThemePreviewSurface
import erply.ui.theme.PreviewThemes


@PreviewThemes
@Composable
fun ProductGroupPreview() {
    val groups = (1L..15L).mapIndexed { index, item ->
        ErplyProductGroup(
            id = item.toString(),
            name = LocalizedValue("name$item"),
            parentId = "0",
            order = index,
            description = LocalizedValue("description$item")
        )
    }

    ErplyThemePreviewSurface {
        ProductGroupsScreenContent(groups = groups)
    }
}

@Composable
fun ProductGroupsScreen(
    viewModel: ProductGroupsScreenViewModel,
    onGroupSelected: (String) -> Unit = {}
) {
    val groups by viewModel.groups.collectAsState()
    ProductGroupsScreenContent(
        groups = groups,
        onLogOut = { viewModel.logOut() },
        onGroupClicked = onGroupSelected
    )
}

@Composable
fun ProductGroupsScreenContent(
    groups: List<ErplyProductGroup>,
    isLoading: Boolean = false,
    onLogOut: () -> Unit = {},
    onGroupClicked: (String) -> Unit = {}
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
            items(groups, key = { it.id }) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clickable { onGroupClicked(it.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.name.en, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
