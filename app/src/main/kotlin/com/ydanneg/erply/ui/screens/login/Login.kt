package com.ydanneg.erply.ui.screens.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ydanneg.erply.BuildConfig
import com.ydanneg.erply.R
import com.ydanneg.erply.api.model.ErplyApiError
import com.ydanneg.erply.ui.components.ErplyTextField
import com.ydanneg.erply.ui.components.ExitConfirmation
import com.ydanneg.erply.ui.components.FadedProgressIndicator
import com.ydanneg.erply.ui.theme.ErplyThemePreviewSurface
import com.ydanneg.erply.ui.theme.PreviewThemes
import kotlinx.coroutines.delay

@PreviewThemes
@Composable
private fun LoginScreenPreview() {
    ErplyThemePreviewSurface {
        LoginScreenContent()
    }
}

@PreviewThemes
@Composable
@Suppress("HardCodedStringLiteral")
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

    ExitConfirmation()

    LoginScreenContent(
        isLoading = uiState.isLoading(),
        error = uiState.getError()?.message(),
        onLoginClicked = { client, user, pass ->
            viewModel.doLogin(client, user, pass)
        }
    )
}

private typealias OnLoginClicked = (String, String, String) -> Unit

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoginScreenContent(
    isLoading: Boolean = false,
    error: String? = null,
    onLoginClicked: OnLoginClicked = { _, _, _ -> },
) {
    var clientCode by rememberSaveable { mutableStateOf(BuildConfig.ERPLY_CLIENT_CODE) }
    var username by rememberSaveable { mutableStateOf(BuildConfig.ERPLY_USERNAME) }
    var password by rememberSaveable { mutableStateOf(BuildConfig.ERPLY_PASSWORD) }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(Unit) {
        delay(200)
        // request focus on the first field
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState(0)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.account_login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                ErplyTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .bringIntoViewRequester(bringIntoViewRequester),
                    value = clientCode,
                    enabled = !isLoading,
                    singleLine = true,
                    onValueChange = { clientCode = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    labelText = stringResource(R.string.login_client_code_label),
                    maxLength = 6
                )
                ErplyTextField(
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester),
                    value = username,
                    enabled = !isLoading,
                    singleLine = true,
                    onValueChange = { username = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    labelText = stringResource(R.string.login_username_label),
                    maxLength = 50
                )
                ErplyTextField(
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester),
                    value = password,
                    singleLine = true,
                    enabled = !isLoading,
                    onValueChange = { password = it },
                    labelText = stringResource(R.string.login_password_label),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onLoginClicked(clientCode, username, password)
                    }),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible)
                            stringResource(R.string.login_hint_hide_password)
                        else
                            stringResource(R.string.login_hint_show_password)

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    maxLength = 50
                )
                Button(
                    enabled = !isLoading,
                    onClick = { onLoginClicked(clientCode, username, password) }
                ) {
                    Text(text = stringResource(R.string.button_sign_in))
                }
                if (!error.isNullOrBlank()) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
        FadedProgressIndicator(visible = isLoading)
    }

}

@Composable
private fun ErplyApiError.message(): String = when (this) {
    ErplyApiError.ConnectionError -> stringResource(R.string.screen_login_error_connection)
    ErplyApiError.WrongCredentials -> stringResource(R.string.screen_login_error_wrong_credentials)
    ErplyApiError.Unauthorized -> stringResource(R.string.screen_login_error_session_expired)
    ErplyApiError.RequestLimitReached -> stringResource(R.string.screen_login_error_limit_reached)
    ErplyApiError.AccountNotFound -> stringResource(R.string.screen_login_error_account_not_found)
    ErplyApiError.AccessDenied -> stringResource(R.string.screen_login_error_access_denied)
    ErplyApiError.Unknown -> stringResource(R.string.screen_login_error_unknown)
}
