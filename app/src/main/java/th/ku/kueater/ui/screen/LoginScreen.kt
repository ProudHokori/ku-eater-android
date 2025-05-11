package th.ku.kueater.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import th.ku.kueater.R
import androidx.compose.material3.TextFieldDefaults
import th.ku.kueater.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    // Observing state from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Outer Box with teal background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A5A5A)) // Teal background
            .padding(16.dp), // Consistent padding on all sides between screen edges and white container
        contentAlignment = Alignment.Center
    ) {
        // White container with rounded corners
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight() // Fill the height of the available space (minus the padding)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp) // Rounded corners for the white container
                )
                .padding(32.dp) // Consistent padding on all sides inside the white container
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(), // Ensure the Column fills the container's height
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Center the content vertically
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ku_eater_landing_logo),
                    contentDescription = "KU Eater Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // Adjust size to 70% of screen width
                        .padding(bottom = 32.dp)
                )

                // Username or Email field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Username or Email",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Type your username or Email", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF1A5A5A),
                            unfocusedIndicatorColor = Color.Black
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Password",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Type your password", fontSize = 14.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF1A5A5A),
                            unfocusedIndicatorColor = Color.Black
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        viewModel.login(email, password) {
                            email = ""
                            password = ""
                            navController.navigate("overview") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(24.dp), // Rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A5A5A), // Teal button color
                        contentColor = Color.White // White text
                    )
                ) {
                    Text("Login", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // New Eater? Create Account
                TextButton(onClick = {
                    navController.navigate("register")
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New Eater? ",
                            color = Color(0xFF1A5A5A), // Teal text to match the mockup
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Create Account",
                            color = Color(0xFF1A5A5A), // Teal text to match the mockup
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline // Underline only on "Create Account"
                        )
                    }
                }

                // Error message (if any)
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // Loading indicator
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = Color(0xFF1A5A5A) // Teal to match the mockup's theme
                    )
                }
            }
        }
    }
}