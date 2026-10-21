package com.shareticket.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shareticket.app.navigation.AppNavigation
import com.shareticket.app.ui.theme.ShareTicketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShareTicketTheme {
                AppNavigation()
            }
        }
    }
}
