package com.example.compose.ui.screens.address

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.composure.arch.Feature
import com.composure.arch.Interactor
import com.composure.arch.ViewModelFeature
import com.composure.ui.StandardScreen
import com.example.compose.ui.screens.address.AddressFeature.Action
import com.example.compose.ui.screens.address.AddressFeature.Result
import com.example.compose.ui.screens.address.AddressScreen.Effect
import com.example.compose.ui.screens.address.AddressScreen.Event
import com.example.compose.ui.screens.address.AddressScreen.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

object AddressScreen : StandardScreen<State, Event, Effect, AddressFeature>() {
    data class FormFields(
        val street1: String = "",
        val street2: String = "",
        val city: String = "",
        val stateProvince: String = "",
        val zip: String = "",
    )

    data class FormErrors(
        val street1: String? = null,
        val city: String? = null,
        val stateProvince: String? = null,
        val zip: String? = null,
    )

    data class State(
        val fields: FormFields = FormFields(),
        val errors: FormErrors = FormErrors(),
    )

    sealed interface Event {
        sealed interface FieldChanged : Event {
            val value: String

            data class Street1Changed(
                override val value: String,
            ) : FieldChanged

            data class Street2Changed(
                override val value: String,
            ) : FieldChanged

            data class CityChanged(
                override val value: String,
            ) : FieldChanged

            data class StateProvinceChanged(
                override val value: String,
            ) : FieldChanged

            data class ZipChanged(
                override val value: String,
            ) : FieldChanged
        }

        data class SubmitClicked(
            val fields: FormFields,
        ) : Event

        object DialogDismissed : Event
    }

    sealed interface Effect {
        object SubmitCompleted : Effect
    }

    @Composable
    override fun Content(feature: Feature<State, Event, Effect>) {
        val state by feature.state.collectAsState()
        var showDialog by remember { mutableStateOf(false) }

        LaunchedEffect("monitor effects") {
            feature.effects
                .map { effect ->
                    when (effect) {
                        Effect.SubmitCompleted -> showDialog = true
                    }
                }.collect()
        }

        AddressForm(state, feature::process)
        SubmissionDialog(showDialog, onDismiss = {
            showDialog = false
            feature.process(Event.DialogDismissed)
        })
    }

    @Composable
    private fun AddressForm(
        state: State,
        processEvent: (Event) -> Unit,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Address", style = MaterialTheme.typography.h5)

            OutlinedTextField(
                value = state.fields.street1,
                onValueChange = { processEvent(Event.FieldChanged.Street1Changed(it)) },
                label = { Text("Street Address *") },
                isError = state.errors.street1 != null,
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.errors.street1 != null) {
                Text(
                    state.errors.street1,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                )
            }

            OutlinedTextField(
                value = state.fields.street2,
                onValueChange = { processEvent(Event.FieldChanged.Street2Changed(it)) },
                label = { Text("Street Address 2") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.fields.city,
                onValueChange = { processEvent(Event.FieldChanged.CityChanged(it)) },
                label = { Text("City *") },
                isError = state.errors.city != null,
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.errors.city != null) {
                Text(
                    state.errors.city,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                )
            }

            OutlinedTextField(
                value = state.fields.stateProvince,
                onValueChange = { processEvent(Event.FieldChanged.StateProvinceChanged(it)) },
                label = { Text("State/Province *") },
                isError = state.errors.stateProvince != null,
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.errors.stateProvince != null) {
                Text(
                    state.errors.stateProvince,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                )
            }

            OutlinedTextField(
                value = state.fields.zip,
                onValueChange = { processEvent(Event.FieldChanged.ZipChanged(it)) },
                label = { Text("ZIP Code *") },
                isError = state.errors.zip != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.errors.zip != null) {
                Text(
                    state.errors.zip,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                )
            }

            Button(
                onClick = { processEvent(Event.SubmitClicked(state.fields)) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Submit")
            }
        }
    }

    @Composable
    private fun SubmissionDialog(
        showDialog: Boolean,
        onDismiss: () -> Unit,
    ) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Address Submitted!") },
                text = { Text("Your address has been submitted successfully.") },
                confirmButton = {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                },
            )
        }
    }
}

private val ZIP_REGEX = Regex("""^\d{5}(-\d{4})?$""")

internal fun validate(fields: AddressScreen.FormFields): AddressScreen.FormErrors? {
    val e =
        AddressScreen.FormErrors(
            street1 = if (fields.street1.isBlank()) "Street address is required" else null,
            city = if (fields.city.isBlank()) "City is required" else null,
            stateProvince = if (fields.stateProvince.isBlank()) "State/Province is required" else null,
            zip =
                when {
                    fields.zip.isBlank() -> "ZIP code is required"
                    !ZIP_REGEX.matches(fields.zip) -> "Enter a valid ZIP (e.g. 12345 or 12345-6789)"
                    else -> null
                },
        )
    return if (listOf(e.street1, e.city, e.stateProvince, e.zip).all { it == null }) null else e
}

class AddressFeature :
    ViewModelFeature<
        AddressScreen.State,
        AddressScreen.Event,
        Action,
        Result,
        AddressScreen.Effect,
    >() {
    sealed class Action {
        sealed class UpdateField : Action() {
            abstract val value: String

            data class Street1(
                override val value: String,
            ) : UpdateField()

            data class Street2(
                override val value: String,
            ) : UpdateField()

            data class City(
                override val value: String,
            ) : UpdateField()

            data class StateProvince(
                override val value: String,
            ) : UpdateField()

            data class Zip(
                override val value: String,
            ) : UpdateField()
        }

        data class Submit(
            val fields: AddressScreen.FormFields,
        ) : Action()

        object Reset : Action()
    }

    sealed class Result {
        data class FieldUpdated(
            val apply: (AddressScreen.FormFields) -> AddressScreen.FormFields,
        ) : Result()

        data class ValidationFailed(
            val errors: AddressScreen.FormErrors,
        ) : Result()

        object SubmitSucceeded : Result()

        object FormReset : Result()
    }

    override val initial: AddressScreen.State = AddressScreen.State()

    override val eventToAction: Interactor<AddressScreen.Event, Action> = { upstream ->
        upstream.map { event ->
            when (event) {
                is AddressScreen.Event.FieldChanged.Street1Changed ->
                    Action.UpdateField.Street1(event.value)
                is AddressScreen.Event.FieldChanged.Street2Changed ->
                    Action.UpdateField.Street2(event.value)
                is AddressScreen.Event.FieldChanged.CityChanged ->
                    Action.UpdateField.City(event.value)
                is AddressScreen.Event.FieldChanged.StateProvinceChanged ->
                    Action.UpdateField.StateProvince(event.value)
                is AddressScreen.Event.FieldChanged.ZipChanged ->
                    Action.UpdateField.Zip(event.value)
                is AddressScreen.Event.SubmitClicked -> Action.Submit(event.fields)
                AddressScreen.Event.DialogDismissed -> Action.Reset
            }
        }
    }

    override val actionToResult: Interactor<Action, Result> = { upstream ->
        upstream.map { action ->
            when (action) {
                is Action.UpdateField.Street1 ->
                    Result.FieldUpdated { it.copy(street1 = action.value) }
                is Action.UpdateField.Street2 ->
                    Result.FieldUpdated { it.copy(street2 = action.value) }
                is Action.UpdateField.City ->
                    Result.FieldUpdated { it.copy(city = action.value) }
                is Action.UpdateField.StateProvince ->
                    Result.FieldUpdated { it.copy(stateProvince = action.value) }
                is Action.UpdateField.Zip ->
                    Result.FieldUpdated { it.copy(zip = action.value) }
                is Action.Submit -> {
                    val errors = validate(action.fields)
                    if (errors != null) Result.ValidationFailed(errors) else Result.SubmitSucceeded
                }
                Action.Reset -> Result.FormReset
            }
        }
    }

    override suspend fun handleResult(
        previous: AddressScreen.State,
        result: Result,
    ): AddressScreen.State =
        when (result) {
            is Result.FieldUpdated -> previous.copy(fields = result.apply(previous.fields))
            is Result.ValidationFailed -> previous.copy(errors = result.errors)
            Result.SubmitSucceeded -> {
                emit(AddressScreen.Effect.SubmitCompleted)
                previous
            }
            Result.FormReset -> AddressScreen.State()
        }
}
