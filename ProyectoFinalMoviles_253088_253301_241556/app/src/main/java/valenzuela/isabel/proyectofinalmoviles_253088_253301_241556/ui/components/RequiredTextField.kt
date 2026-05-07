package valenzuela.isabel.proyectofinalmoviles_253088_253301_241556.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun RequiredTextField(
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholder: String,
    error: String? = null,
    helperText: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = error != null,
        placeholder = { Text(placeholder) },
        supportingText = {
            when {
                error != null -> {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                helperText != null -> {
                    Text(helperText)
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}