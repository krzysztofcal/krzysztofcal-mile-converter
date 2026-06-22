package com.krzysztofcal.mileconverter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private const val RECENTS_PREFS_NAME = "mile_converter_recents"
private const val RECENTS_KEY = "recent_values"
private const val MAX_RECENT_VALUES = 5

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MileConverterApp()
                }
            }
        }
    }
}

private enum class Screen(val title: String) {
    Converter("Converter"),
    About("About"),
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
private fun MileConverterApp() {
    val context = LocalContext.current.applicationContext
    val recentValuesStore = remember(context) { RecentValuesStore(context) }
    var selectedScreenName by rememberSaveable { mutableStateOf(Screen.Converter.name) }
    val selectedScreen = remember(selectedScreenName) { Screen.valueOf(selectedScreenName) }
    var inputValue by rememberSaveable { mutableStateOf("") }
    var inputUnitName by rememberSaveable { mutableStateOf(DistanceUnit.Miles.name) }
    var recentValues by rememberSaveable { mutableStateOf(recentValuesStore.loadRecentValues()) }

    LaunchedEffect(recentValuesStore) {
        snapshotFlow { inputValue }
            .debounce(700)
            .collect { currentValue ->
                val normalizedValue = currentValue.replace(',', '.').trim()
                if (normalizedValue.toDoubleOrNull() != null) {
                    recentValues = recentValuesStore.rememberValue(normalizedValue)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(selectedTabIndex = selectedScreen.ordinal) {
                Screen.entries.forEach { screen ->
                    Tab(
                        selected = selectedScreen == screen,
                        onClick = { selectedScreenName = screen.name },
                        text = { Text(screen.title) },
                    )
                }
            }

            when (selectedScreen) {
                Screen.Converter -> ConverterScreen(
                    inputValue = inputValue,
                    onInputValueChange = { inputValue = it },
                    inputUnitName = inputUnitName,
                    onInputUnitNameChange = { inputUnitName = it },
                    recentValues = recentValues,
                    onRecentValueClick = { inputValue = it },
                    onClearRecentValues = {
                        recentValues = recentValuesStore.clearRecentValues()
                    },
                )
                Screen.About -> AboutScreen()
            }
        }
    }
}

@Composable
private fun ConverterScreen(
    inputValue: String,
    onInputValueChange: (String) -> Unit,
    inputUnitName: String,
    onInputUnitNameChange: (String) -> Unit,
    recentValues: List<String>,
    onRecentValueClick: (String) -> Unit,
    onClearRecentValues: () -> Unit,
) {
    val inputUnit = remember(inputUnitName) { DistanceUnit.valueOf(inputUnitName) }
    val normalizedInput = inputValue.replace(',', '.')
    val isInvalidInput = inputValue.isNotEmpty() && normalizedInput.toDoubleOrNull() == null
    val parsedValue = normalizedInput.toDoubleOrNull()
    val conversions = remember(parsedValue, inputUnit) {
        parsedValue?.let { DistanceConverter.convertAll(it, inputUnit) }.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (parsedValue == null) {
            Text(
                text = if (isInvalidInput) "" else stringResource(R.string.enter_valid_number),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DistanceUnit.entries.forEach { unit ->
                        ConversionRow(
                            label = unit.displayName,
                            value = formatDistance(conversions.getValue(unit)),
                        )
                    }
                }
            }
        }

        if (recentValues.isNotEmpty()) {
            RecentValuesSection(
                values = recentValues,
                onValueClick = onRecentValueClick,
                onClear = onClearRecentValues,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputValueChange,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.value_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isInvalidInput,
                supportingText = if (isInvalidInput) {
                    { Text(stringResource(R.string.invalid_number)) }
                } else {
                    null
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { onInputValueChange("") }) {
                Text(stringResource(R.string.clear))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DistanceUnit.entries.forEach { unit ->
                val selected = unit == inputUnit
                if (selected) {
                    Button(
                        onClick = { onInputUnitNameChange(unit.name) },
                        modifier = Modifier.weight(1f),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    ) {
                        Text(
                            text = unit.displayName,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { onInputUnitNameChange(unit.name) },
                        modifier = Modifier.weight(1f),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    ) {
                        Text(
                            text = unit.displayName,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentValuesSection(
    values: List<String>,
    onValueClick: (String) -> Unit,
    onClear: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.last_used_values),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.tap_to_restore),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TextButton(onClick = onClear) {
                    Text(stringResource(R.string.clear_history))
                }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                values.forEach { value ->
                    AssistChip(
                        onClick = { onValueClick(value) },
                        label = { Text(text = value) },
                    )
                }
            }
        }
    }
}

private class RecentValuesStore(context: Context) {
    private val preferences = context.getSharedPreferences(RECENTS_PREFS_NAME, Context.MODE_PRIVATE)

    fun loadRecentValues(): List<String> =
        preferences.getString(RECENTS_KEY, null)
            ?.split('\n')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.distinct()
            .orEmpty()

    fun rememberValue(value: String): List<String> {
        val normalized = value.trim()
        val updatedValues = loadRecentValues()
            .filterNot { it == normalized }
            .toMutableList()
            .apply {
                add(0, normalized)
            }
            .take(MAX_RECENT_VALUES)

        preferences.edit()
            .putString(RECENTS_KEY, updatedValues.joinToString("\n"))
            .apply()

        return updatedValues
    }

    fun clearRecentValues(): List<String> {
        preferences.edit()
            .remove(RECENTS_KEY)
            .apply()
        return emptyList()
    }
}

@Composable
private fun ConversionRow(label: String, value: String) {
    val clipboardManager = LocalClipboardManager.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        TextButton(onClick = { clipboardManager.setText(AnnotatedString(value)) }) {
            Text(stringResource(R.string.copy))
        }
    }
}

@Composable
private fun AboutScreen() {
    val uriHandler = LocalUriHandler.current
    val githubUrl = stringResource(R.string.github_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.app_about_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.app_about_description),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.github_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = githubUrl,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { uriHandler.openUri(githubUrl) },
        )
    }
}

private fun formatDistance(value: Double): String =
    DecimalFormat("0.######", DecimalFormatSymbols(Locale.US)).format(value)
