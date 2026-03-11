import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sufibra.network.ui.navigation.AppNavigation
import com.sufibra.network.ui.theme.SufibraNetworkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SufibraNetworkTheme {
                AppNavigation()
            }
        }
    }
}
