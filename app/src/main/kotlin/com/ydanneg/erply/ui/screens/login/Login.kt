package com.ydanneg.erply.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ydanneg.erply.ui.components.ErplyTextField
import com.ydanneg.erply.ui.components.ExitConfirmation
import com.ydanneg.erply.ui.components.FadedProgressIndicator
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes

@PreviewThemes
@Composable
private fun LoginScreenPreview() {
    ErplyThemePreviewSurface {
        LoginScreenContent()
    }
}

@PreviewThemes
@Composable
private fun LoginScreenWithErrorPreview() {
    ErplyThemePreviewSurface {
        LoginScreenContent(isLoading = false, error = "Bad credentials! Try again!")
    }
}

@PreviewThemes
@Composable
private fun LoginScreenLoadingPreview() {
    ErplyThemePreviewSurface {
        LoginScreenContent(isLoading = true)
    }
}


@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keepMeSignedIn by viewModel.keepMeSignedIn.collectAsStateWithLifecycle()

    ExitConfirmation()

    LoginScreenContent(
        isLoading = uiState.isLoading(),
        error = uiState.getError(),
        onLoginClicked = { client, user, pass ->
            viewModel.doLogin(client, user, pass)
        },
        keepMeSignedIn = keepMeSignedIn,
        onKeepMeSignedInChanged = viewModel::setKeepMeLoggedIn
    )
}

private typealias OnLoginClicked = (String, String, String) -> Unit

@Composable
private fun LoginScreenContent(
    isLoading: Boolean = false,
    error: String? = null,
    onLoginClicked: OnLoginClicked? = null,
    keepMeSignedIn: Boolean = false,
    onKeepMeSignedInChanged: (Boolean) -> Unit = {}
) {
    var clientCode by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Account Login",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                ErplyTextField(
                    value = clientCode,
                    enabled = !isLoading,
                    singleLine = true,
                    onValueChange = { value: String -> clientCode = value },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    labelText = "Client Code",
                    maxLength = 10
                )
                ErplyTextField(
                    value = username,
                    enabled = !isLoading,
                    singleLine = true,
                    onValueChange = { value: String -> username = value },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    labelText = "Username",
                    maxLength = 50
                )
                ErplyTextField(
                    value = password,
                    singleLine = true,
                    enabled = !isLoading,
                    onValueChange = { value: String -> password = value },
                    labelText = "Password",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    maxLength = 50
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = keepMeSignedIn, onCheckedChange = onKeepMeSignedInChanged)
                    Text(text = "Keep me signed in", modifier = Modifier.wrapContentWidth())
                }
                Button(
                    enabled = !isLoading,
                    onClick = { onLoginClicked?.invoke(clientCode, username, password) }
                ) {
                    Text(text = "Sign in")
                }
                if (!error.isNullOrBlank()) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
        FadedProgressIndicator(isLoading)
    }

}