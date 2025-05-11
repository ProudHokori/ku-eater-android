package th.ku.kueater.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import th.ku.kueater.R
import th.ku.kueater.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val isFormValid = email.isNotBlank() &&
            password.length >= 6 &&
            password == confirmPassword &&
            acceptedTerms

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            },
            title = { Text("Terms and Conditions") },
            text = {
                Text(
                    "These are the mock terms and conditions for KU Eater. " +
                            "By using this app, you agree to nothing in particular, " +
                            "because this is a demo 😉."
                )
            }
        )
    }

    // Outer Box with teal background, matching LoginScreen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A5A5A))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // White container with rounded corners, same as LoginScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo (same as in LoginScreen)
                Image(
                    painter = painterResource(id = R.drawable.ku_eater_landing_logo),
                    contentDescription = "KU Eater Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 32.dp)
                )

                // Email field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Type your email", fontSize = 14.sp) },
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
                Column(modifier = Modifier.fillMaxWidth()) {
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

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Confirm Password",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Type your confirm password", fontSize = 14.sp) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Terms and Conditions row with checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF1A5A5A)
                        )
                    )

                    Text(text = "I accept the ")
                    Text(
                        text = "terms and conditions",
                        color =  Color(0xFF1A5A5A),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { showTermsDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Button
                Button(
                    onClick = {
                        viewModel.register(email, password) {
                            email = ""
                            password = ""
                            confirmPassword = ""
                            acceptedTerms = false
                            navController.navigate("overview") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && !isLoading,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A5A5A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Sign Up", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Already have an account? Login text button
                TextButton(onClick = {
                    navController.navigate("login")
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "I'm already an Eater? ",
                            color = Color(0xFF1A5A5A), // Teal text to match the mockup
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Login",
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Loading indicator
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(color = Color(0xFF1A5A5A))
                }
            }
        }
    }
}
