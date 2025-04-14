package io.github.kroune.superfinancer.ui.financeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.kroune.superfinancer.R
import io.github.kroune.superfinancer.domain.models.database.FinanceGoalEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOperationDialog(
    onDismiss: () -> Unit,
    addOperation: (amount: Float, selectedGoalId: Int, comment: String?) -> Unit,
    goals: SnapshotStateList<Pair<FinanceGoalEntity, Float>>
) {
    Dialog(
        onDismiss,
    ) {
        Card(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                var number: String by remember { mutableStateOf("") }
                var selectedGoalBalancer by remember { mutableStateOf<Float?>(null) }
                // if we can subtract such money from the goal
                val numberPossible = selectedGoalBalancer?.let {
                    when (val numberAsFloat = number.toFloatOrNull()) {
                        null -> {
                            false
                        }

                        0f -> {
                            false
                        }

                        in 0f..Float.POSITIVE_INFINITY -> {
                            true
                        }

                        else -> {
                            (it + numberAsFloat >= 0f)
                        }
                    }
                } ?: false
                var numberInvalid by remember { mutableStateOf(true) }
                OutlinedTextField(
                    number,
                    {
                        val isValid = run {
                            val input = it.toFloatOrNull() ?: return@run true
                            // pretty sure it is enough for most people
                            if (input > 1_000_000_000_000_000)
                                return@run true
                            number =
                                it.filter { char -> char != '\n' && !char.isWhitespace() }
                            if (input == 0f) {
                                numberInvalid = true
                                return@run true
                            }
                            return@run false
                        }
                        numberInvalid = isValid
                    },
                    placeholder = {
                        Text(stringResource(R.string.amount))
                    },
                    isError = (numberInvalid || (!numberPossible && selectedGoalBalancer != null))
                            && number.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )


                var selectedOptionId by remember { mutableStateOf<Int?>(null) }
                run {
                    val options: SnapshotStateList<Pair<FinanceGoalEntity, Float>> = goals
                    val textFieldState = rememberTextFieldState()

                    val filteredOptions =
                        options.filter { it.first.name.contains(textFieldState.text) }

                    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
                    val expanded = allowExpanded && filteredOptions.isNotEmpty()

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = setExpanded,
                    ) {
                        OutlinedTextField(
                            // The `menuAnchor` modifier must be passed to the text field to handle
                            // expanding/collapsing the menu on click. An editable text field has
                            // the anchor type `PrimaryEditable`.
                            modifier =
                            Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                            state = textFieldState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            label = { Text(stringResource(R.string.goal_name)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded,
                                    // If the text field is editable, it is recommended to make the
                                    // trailing icon a `menuAnchor` of type `SecondaryEditable`. This
                                    // provides a better experience for certain accessibility services
                                    // to choose a menu option without typing.
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            modifier = Modifier.heightIn(max = 280.dp),
                            expanded = expanded,
                            onDismissRequest = { setExpanded(false) },
                        ) {
                            filteredOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option.first.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        textFieldState.setTextAndPlaceCursorAtEnd(option.first.name)
                                        selectedOptionId = option.first.id
                                        selectedGoalBalancer = option.second
                                        setExpanded(false)
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }

                var comment by remember { mutableStateOf("") }
                OutlinedTextField(
                    comment, {
                        comment = it
                    },
                    placeholder = {
                        Text(stringResource(R.string.comment))
                    }
                )
                Button(
                    {
                        addOperation(
                            number.toFloat(),
                            selectedOptionId!!,
                            comment.takeIf { it.isNotEmpty() })
                        onDismiss()
                    },
                    enabled = !numberInvalid && numberPossible && selectedOptionId != null
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}
