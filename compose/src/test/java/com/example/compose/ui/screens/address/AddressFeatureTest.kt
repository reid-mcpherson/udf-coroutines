package com.example.compose.ui.screens.address

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddressFeatureTest {
    private val subject = AddressFeature()

    @Test
    fun `initial state has blank fields and no errors`() {
        assertThat(subject.initial).isEqualTo(AddressScreen.State())
    }

    @Test
    fun `when FieldUpdated result is received, fields are updated`() =
        runTest {
            val state =
                subject.handleResult(
                    AddressScreen.State(),
                    AddressFeature.Result.FieldUpdated { it.copy(street1 = "123 Main St") },
                )
            assertThat(state.fields.street1).isEqualTo("123 Main St")
        }

    @Test
    fun `when ValidationFailed result is received, errors are set`() =
        runTest {
            val errors = AddressScreen.FormErrors(street1 = "Street address is required")
            val state = subject.handleResult(AddressScreen.State(), AddressFeature.Result.ValidationFailed(errors))
            assertThat(state.errors).isEqualTo(errors)
        }

    @Test
    fun `when SubmitSucceeded result is received, SubmitCompleted effect is emitted`() =
        runTest {
            subject.effects.test {
                subject.handleResult(AddressScreen.State(), AddressFeature.Result.SubmitSucceeded)
                assertThat(awaitItem()).isEqualTo(AddressScreen.Effect.SubmitCompleted)
            }
        }

    @Test
    fun `when SubmitSucceeded result is received, state is unchanged`() =
        runTest {
            val state = AddressScreen.State(fields = AddressScreen.FormFields(street1 = "123 Main St"))
            val result = subject.handleResult(state, AddressFeature.Result.SubmitSucceeded)
            assertThat(result).isEqualTo(state)
        }

    @Test
    fun `when FormReset result is received, state returns to blank`() =
        runTest {
            val state = AddressScreen.State(fields = AddressScreen.FormFields(street1 = "123 Main St"))
            val result = subject.handleResult(state, AddressFeature.Result.FormReset)
            assertThat(result).isEqualTo(AddressScreen.State())
        }
}

class EventToActionsInteractorTest {
    private val subject = AddressFeature()

    @Test
    fun `when Street1Changed event is received, Street1 action is emitted`() =
        runTest {
            val startFlow = flowOf(AddressScreen.Event.FieldChanged.Street1Changed("123 Main St"))
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.UpdateField.Street1("123 Main St"))
                awaitComplete()
            }
        }

    @Test
    fun `when Street2Changed event is received, Street2 action is emitted`() =
        runTest {
            val startFlow = flowOf(AddressScreen.Event.FieldChanged.Street2Changed("Apt 4"))
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.UpdateField.Street2("Apt 4"))
                awaitComplete()
            }
        }

    @Test
    fun `when CityChanged event is received, City action is emitted`() =
        runTest {
            val startFlow = flowOf(AddressScreen.Event.FieldChanged.CityChanged("Springfield"))
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.UpdateField.City("Springfield"))
                awaitComplete()
            }
        }

    @Test
    fun `when StateProvinceChanged event is received, StateProvince action is emitted`() =
        runTest {
            val startFlow = flowOf(AddressScreen.Event.FieldChanged.StateProvinceChanged("IL"))
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.UpdateField.StateProvince("IL"))
                awaitComplete()
            }
        }

    @Test
    fun `when ZipChanged event is received, Zip action is emitted`() =
        runTest {
            val startFlow = flowOf(AddressScreen.Event.FieldChanged.ZipChanged("62701"))
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.UpdateField.Zip("62701"))
                awaitComplete()
            }
        }

    @Test
    fun `when SubmitClicked event is received, Submit action is emitted`() =
        runTest {
            val fields =
                AddressScreen.FormFields(
                    street1 = "123 Main St",
                    city = "Springfield",
                    stateProvince = "IL",
                    zip = "62701",
                )
            val startFlow = flowOf(AddressScreen.Event.SubmitClicked(fields))
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.Submit(fields))
                awaitComplete()
            }
        }

    @Test
    fun `when DialogDismissed event is received, Reset action is emitted`() =
        runTest {
            val startFlow = flowOf(AddressScreen.Event.DialogDismissed)
            subject.eventToAction(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Action.Reset)
                awaitComplete()
            }
        }
}

class ActionToResultsInteractorTest {
    private val subject = AddressFeature()

    @Test
    fun `when Street1 action is received, FieldUpdated applies street1 transform`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.UpdateField.Street1("123 Main St"))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.FieldUpdated
                assertThat(result.apply(AddressScreen.FormFields()).street1).isEqualTo("123 Main St")
                awaitComplete()
            }
        }

    @Test
    fun `when Street2 action is received, FieldUpdated applies street2 transform`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.UpdateField.Street2("Apt 4"))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.FieldUpdated
                assertThat(result.apply(AddressScreen.FormFields()).street2).isEqualTo("Apt 4")
                awaitComplete()
            }
        }

    @Test
    fun `when City action is received, FieldUpdated applies city transform`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.UpdateField.City("Springfield"))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.FieldUpdated
                assertThat(result.apply(AddressScreen.FormFields()).city).isEqualTo("Springfield")
                awaitComplete()
            }
        }

    @Test
    fun `when StateProvince action is received, FieldUpdated applies stateProvince transform`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.UpdateField.StateProvince("IL"))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.FieldUpdated
                assertThat(result.apply(AddressScreen.FormFields()).stateProvince).isEqualTo("IL")
                awaitComplete()
            }
        }

    @Test
    fun `when Zip action is received, FieldUpdated applies zip transform`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.UpdateField.Zip("62701"))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.FieldUpdated
                assertThat(result.apply(AddressScreen.FormFields()).zip).isEqualTo("62701")
                awaitComplete()
            }
        }

    @Test
    fun `when Submit action with all blank fields, ValidationFailed with all errors is emitted`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.Submit(AddressScreen.FormFields()))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.ValidationFailed
                assertThat(result.errors.street1).isNotNull()
                assertThat(result.errors.city).isNotNull()
                assertThat(result.errors.stateProvince).isNotNull()
                assertThat(result.errors.zip).isNotNull()
                awaitComplete()
            }
        }

    @Test
    fun `when Submit action with invalid ZIP format, ValidationFailed with only zip error is emitted`() =
        runTest {
            val fields =
                AddressScreen.FormFields(
                    street1 = "123 Main St",
                    city = "Springfield",
                    stateProvince = "IL",
                    zip = "abc",
                )
            val startFlow = flowOf(AddressFeature.Action.Submit(fields))
            subject.actionToResult(startFlow).test {
                val result = awaitItem() as AddressFeature.Result.ValidationFailed
                assertThat(result.errors.street1).isNull()
                assertThat(result.errors.city).isNull()
                assertThat(result.errors.stateProvince).isNull()
                assertThat(result.errors.zip).isNotNull()
                awaitComplete()
            }
        }

    @Test
    fun `when Submit action with valid fields, SubmitSucceeded is emitted`() =
        runTest {
            val fields =
                AddressScreen.FormFields(
                    street1 = "123 Main St",
                    city = "Springfield",
                    stateProvince = "IL",
                    zip = "62701",
                )
            val startFlow = flowOf(AddressFeature.Action.Submit(fields))
            subject.actionToResult(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Result.SubmitSucceeded)
                awaitComplete()
            }
        }

    @Test
    fun `when Reset action is received, FormReset is emitted`() =
        runTest {
            val startFlow = flowOf(AddressFeature.Action.Reset)
            subject.actionToResult(startFlow).test {
                assertThat(awaitItem()).isEqualTo(AddressFeature.Result.FormReset)
                awaitComplete()
            }
        }
}

class AddressValidationTest {
    @Test
    fun `when street1 is blank, street1 error is set`() {
        val errors = validate(AddressScreen.FormFields(city = "Springfield", stateProvince = "IL", zip = "62701"))
        assertThat(errors?.street1).isNotNull()
    }

    @Test
    fun `when city is blank, city error is set`() {
        val errors = validate(AddressScreen.FormFields(street1 = "123 Main", stateProvince = "IL", zip = "62701"))
        assertThat(errors?.city).isNotNull()
    }

    @Test
    fun `when stateProvince is blank, stateProvince error is set`() {
        val errors = validate(AddressScreen.FormFields(street1 = "123 Main", city = "Springfield", zip = "62701"))
        assertThat(errors?.stateProvince).isNotNull()
    }

    @Test
    fun `when zip is blank, zip error is set`() {
        val errors =
            validate(AddressScreen.FormFields(street1 = "123 Main", city = "Springfield", stateProvince = "IL"))
        assertThat(errors?.zip).isNotNull()
    }

    @Test
    fun `when zip is invalid format, zip error is set`() {
        val errors =
            validate(
                AddressScreen.FormFields(street1 = "123 Main", city = "Springfield", stateProvince = "IL", zip = "abc"),
            )
        assertThat(errors?.zip).isNotNull()
    }

    @Test
    fun `when zip is 5 digits, no zip error`() {
        val errors =
            validate(
                AddressScreen.FormFields(
                    street1 = "123 Main",
                    city = "Springfield",
                    stateProvince = "IL",
                    zip = "12345",
                ),
            )
        assertThat(errors?.zip).isNull()
    }

    @Test
    fun `when zip is 5 plus 4 digits, no zip error`() {
        val errors =
            validate(
                AddressScreen.FormFields(
                    street1 = "123 Main",
                    city = "Springfield",
                    stateProvince = "IL",
                    zip = "12345-6789",
                ),
            )
        assertThat(errors?.zip).isNull()
    }

    @Test
    fun `when all fields are valid, validate returns null`() {
        val errors =
            validate(
                AddressScreen.FormFields(
                    street1 = "123 Main St",
                    city = "Springfield",
                    stateProvince = "IL",
                    zip = "62701",
                ),
            )
        assertThat(errors).isNull()
    }
}
