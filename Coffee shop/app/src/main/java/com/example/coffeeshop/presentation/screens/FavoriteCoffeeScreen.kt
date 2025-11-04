import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorLightGrey

@Composable
fun FavoriteCoffeeScreen(navController: NavController) {

    val favoriteCoffees = remember { emptyList<CoffeeResponse>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .padding(top = 68.dp)
                .wrapContentSize()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.coffeeshop.R.drawable.leftarrow),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Избранное",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 19.2.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(44.dp))
            }
        }

        EmptyFavoritesState(navController = navController)
    }
}

@Composable
fun EmptyFavoritesState(navController: NavController) {
    Spacer(modifier = Modifier.size(100.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(colorLightGrey.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = com.example.coffeeshop.R.drawable.heart),
                contentDescription = "Favorite",
                modifier = Modifier.size(48.dp),

            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Пока ничего нет",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Добавляйте понравившиеся напитки в избранное",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = colorLightGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorDarkOrange)
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Перейти в каталог",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color.White,
                fontFamily = SoraFontFamily
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Favorites - Empty")
@Composable
fun FavoriteCoffeeScreenPreview() {
    CoffeeShopTheme {
        FavoriteCoffeeScreen(navController = rememberNavController())
    }
}

