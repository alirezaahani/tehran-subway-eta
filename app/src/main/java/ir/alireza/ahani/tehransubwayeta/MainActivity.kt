package ir.alireza.ahani.tehransubwayeta


import SubwayETATracker
import SubwayETAViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import ir.alireza.ahani.tehransubwayeta.ui.theme.TehranSubwayETATheme
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.alireza.ahani.tehransubwayeta.ui.theme.AppFont

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TehranSubwayETATheme {
                CompositionLocalProvider(
                    LocalTextStyle provides LocalTextStyle.current.merge(
                        TextStyle(fontFamily = AppFont.Samim)
                    ),
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    Surface {
                        val viewModel: SubwayETAViewModel = viewModel()
                        SubwayETATracker(viewModel)
                    }
                }
            }
        }
    }
}