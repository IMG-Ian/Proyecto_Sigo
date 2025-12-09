package com.example.loginsigo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginsigo.data.model.UserResponse
import com.example.loginsigo.di.LoginViewModelFactory
import com.example.loginsigo.ui.login.DashboardScreen
import com.example.loginsigo.ui.login.HistoryScreen 
import com.example.loginsigo.ui.login.LoginViewModel
import com.example.loginsigo.ui.login.ProfileScreen
import com.example.loginsigo.ui.theme.UtmGreenPrimary
import com.google.gson.Gson

object Routes {
    const val LOGIN = "login_screen"
    const val DASHBOARD = "dashboard_screen/{userJson}"
    const val PROFILE = "profile_screen/{userJson}"
    const val HISTORY = "history_screen"

    fun dashboard(userJson: String) = "dashboard_screen/$userJson"
    fun profile(userJson: String) = "profile_screen/$userJson"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppScreenEntry()
            }
        }
    }
}

@Composable
fun AppScreenEntry() {
    val appContext = LocalContext.current.applicationContext
    val application = appContext as SigoLoginApplication

    val authRepository = application.container.authRepository
    val factory = LoginViewModelFactory(authRepository)

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            val viewModel: LoginViewModel = viewModel(factory = factory)

            LoginScreen(
                viewModel = viewModel,
                onNavigateToDashboard = { userResponse ->
                    val userJson = Gson().toJson(userResponse)
                    navController.navigate(Routes.dashboard(userJson)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(navArgument("userJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val userJson = backStackEntry.arguments?.getString("userJson")
            val userResponse = Gson().fromJson(userJson, UserResponse::class.java)

            if (userResponse != null) {
                DashboardScreen(
                    user = userResponse,
                    onNavigateToHistory = {
                        navController.navigate(Routes.HISTORY)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.profile(userJson!!))
                    }
                )
            }
        }

        composable(
            route = Routes.PROFILE,
            arguments = listOf(navArgument("userJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val userJson = backStackEntry.arguments?.getString("userJson")
            val userResponse = Gson().fromJson(userJson, UserResponse::class.java)

            if (userResponse != null) {
                ProfileScreen(
                    user = userResponse,
                    navController = navController
                )
            }
        }

        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToDashboard: (UserResponse) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.errorMessage != null) {
        Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
    }

    if (uiState.loginSuccess && uiState.user != null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Bienvenido ${uiState.user?.personFullName}", Toast.LENGTH_SHORT).show()
            onNavigateToDashboard(uiState.user!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Logo UTM",
            tint = UtmGreenPrimary,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Universidad Tecnológica de Morelia",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = UtmGreenPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Usuario") },
            singleLine = true,
            enabled = !uiState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UtmGreenPrimary,
                focusedLabelColor = UtmGreenPrimary,
                cursorColor = UtmGreenPrimary,
                unfocusedBorderColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !uiState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UtmGreenPrimary,
                focusedLabelColor = UtmGreenPrimary,
                cursorColor = UtmGreenPrimary,
                unfocusedBorderColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { }) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = UtmGreenPrimary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = UtmGreenPrimary)
        } else {
            Button(
                onClick = viewModel::login,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UtmGreenPrimary
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}