package com.krzysztofcal.mileconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

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
@OptIn(ExperimentalMaterial3Api::class)
private fun MileConverterApp() {
    var selectedScreenName by rememberSaveable { mutableStateOf(Screen.Converter.name) }
    val selectedScreen = remember(selectedScreenName) { Screen.valueOf(selectedScreenName) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Mile Converter") })
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
                Screen.Converter -> ConverterScreen()
                Screen.About -> AboutScreen()
            }
        }
    }
}

@Composable
private fun ConverterScreen() {
    var inputValue by rememberSaveable { mutableStateOf("") }
    var inputUnitName by rememberSaveable { mutableStateOf(DistanceUnit.Miles.name) }
    val inputUnit = remember(inputUnitName) { DistanceUnit.valueOf(inputUnitName) }
    val parsedValue = inputValue.replace(',', '.').toDoubleOrNull()
    val conversions = remember(parsedValue, inputUnit) {
        parsedValue?.let { DistanceConverter.convertAll(it, inputUnit) }.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Enter a value and choose the source unit.",
            style = MaterialTheme.typography.bodyLarge,
        )

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Value") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )

        Text(
            text = "Input unit",
            style = MaterialTheme.typography.labelLarge,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DistanceUnit.entries.forEach { unit ->
                val selected = unit == inputUnit
                if (selected) {
                    Button(
                        onClick = { inputUnitName = unit.name },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = unit.displayName)
                    }
                } else {
                    OutlinedButton(
                        onClick = { inputUnitName = unit.name },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = unit.displayName)
                    }
                }
            }
        }

        if (parsedValue == null) {
            Text(
                text = "Enter a valid number to see conversions.",
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
    }
}

@Composable
private fun ConversionRow(label: String, value: String) {
    Column {
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
}

@Composable
private fun AboutScreen() {
    val uriHandler = LocalUriHandler.current
    val githubUrl = "https://github.com/krzysztofcal"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Mile Converter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "A minimal converter for miles, kilometers, and nautical miles.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "GitHub",
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
