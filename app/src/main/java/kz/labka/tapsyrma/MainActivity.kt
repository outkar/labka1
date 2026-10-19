package kz.labka.tapsyrma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kz.labka.tapsyrma.ui.navigation.AppNavHost
import kz.labka.tapsyrma.ui.theme.TapsyrmaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapsyrmaTheme {
                AppNavHost()
            }
        }
    }
}
