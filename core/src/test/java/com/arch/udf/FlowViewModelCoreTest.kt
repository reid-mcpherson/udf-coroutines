package com.arch.udf

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal abstract class FlowViewModelCoreTest(
    private val createSubject: (scope: CoroutineScope, eventToActionInteractor: Interactor<Event, Action>) -> FlowFeatureCore<State, Event, Action, Result, Effect>,
) {

    @Test
    fun `when an action is received the state is changed`() =
        runTest {
            val dispatcher = StandardTestDispatcher()
            val scope = TestScope(dispatcher)
            val subject =
                createSubject(scope, Interactors.defaultEventToActionInteractor)
            subject.state.test {
                assertThat(awaitItem()).isEqualTo(State.StateA)
                subject.processUiEvent(Event.EventB)
                scope.advanceUntilIdle()
                assertThat(awaitItem()).isEqualTo(State.StateB)
            }
            scope.cancel()
        }

    @Test
    fun `when event C occurs effect B is emitted`(): Unit =
        runTest {
            val dispatcher = StandardTestDispatcher()
            val scope = TestScope(dispatcher)
            val subject =
                createSubject(scope, Interactors.defaultEventToActionInteractor)
            subject.state.test {
                subject.effect.test {
                    subject.processUiEvent(Event.EventC("Test"))
                    scope.advanceUntilIdle()
                    assertThat(awaitItem()).isEqualTo(Effect.EffectB)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }


    @Test
    fun `state can be received asynchronously`() = runTest {
        val scope = TestScope()
        val eventsToActionsInteractor: Interactor<Event, Action> = { upstream ->

            val actionAInteractor = upstream.filterIsInstance<Event.EventA>()
                .map {
                    delay(5000)
                    Action.ActionA
                }

            val actionBInteractor = upstream.filterIsInstance<Event.EventB>()
                .map {
                    delay(2000)
                    Action.ActionB
                }
            flowOf(actionAInteractor, actionBInteractor).flattenMerge()
        }
        val subject = createSubject(scope, eventsToActionsInteractor)
        subject.state.test {
            //Initial state is immediately received
            assertThat(awaitItem()).isEqualTo(State.StateA)
            // Process EventA immediately followed by EventB
            subject.processUiEvent(Event.EventA)
            scope.runCurrent()
            subject.processUiEvent(Event.EventB)

            // Advance time by 2500 seconds so EventB can be emitted
            scope.advanceTimeBy(2500)
            assertThat(awaitItem()).isEqualTo(State.StateB)

            // Advance time by an additional 2501 seconds (elapsed time 5001 ms) so EventA can complete
            scope.advanceTimeBy(2501)
            assertThat(awaitItem()).isEqualTo(State.StateA)
            println(1)
        }
        scope.cancel()
    }
}

private object Interactors {
    val defaultEventToActionInteractor: Interactor<Event, Action> = { upstream ->
        upstream.map { event ->
            when (event) {
                Event.EventA -> Action.ActionA
                Event.EventB -> Action.ActionB
                is Event.EventC -> Action.ActionC
            }
        }
    }
    val defaultActionToResultInteractor: Interactor<Action, Result> = { upstream ->
        upstream.map { action ->
            when (action) {
                Action.ActionA -> Result.ResultA
                Action.ActionB -> Result.ResultB
                Action.ActionC -> Result.ResultC
            }
        }
    }
}

internal class FlowViewModelImplTest :
    FlowViewModelCoreTest(createSubject = { scope, eventToActionInteractor ->
        FlowFeatureImplSubject(
            scope,
            eventToActionInteractor = eventToActionInteractor
        )
    })


internal class FlowViewModelAndroidTest :
    FlowViewModelCoreTest(createSubject = { scope, eventToActionInteractor ->
        FlowFeatureAndroidSubject(scope, eventToActionInteractor = eventToActionInteractor)
    })

private class FlowFeatureImplSubject(
    coroutineScope: CoroutineScope,
    override val initialState: State = State.StateA,
    override val eventToActionInteractor: Interactor<Event, Action> = Interactors.defaultEventToActionInteractor,
    override val actionToResultInteractor: Interactor<Action, Result> = Interactors.defaultActionToResultInteractor
) : FlowFeatureImpl<State, Event, Action, Result, Effect>(
    coroutineScope
) {
    override suspend fun handleResult(previous: State, result: Result): State =
        handleResult(previous, result, ::emit)
}

private class FlowFeatureAndroidSubject(
    coroutineScope: CoroutineScope,
    override val initialState: State = State.StateA,
    override val eventToActionInteractor: Interactor<Event, Action> = Interactors.defaultEventToActionInteractor,
    override val actionToResultInteractor: Interactor<Action, Result> = Interactors.defaultActionToResultInteractor
) : FlowFeatureAndroid<State, Event, Action, Result, Effect>(
    coroutineScope
) {
    override suspend fun handleResult(previous: State, result: Result): State =
        handleResult(previous, result, ::emit)
}

private suspend fun handleResult(
    previous: State,
    result: Result,
    emitEffect: suspend (effect: Effect) -> Unit
): State =
    when (result) {
        Result.ResultA -> State.StateA
        Result.ResultB -> State.StateB
        Result.ResultC -> {
            emitEffect(Effect.EffectB)
            previous
        }
    }

public sealed class State {
    public object StateA : State()
    public object StateB : State()
}

public sealed class Event {
    public object EventA : Event()
    public object EventB : Event()
    public data class EventC(val value: String?) : Event()
}

public sealed class Action {
    public object ActionA : Action()
    public object ActionB : Action()
    public object ActionC : Action()
}

public sealed class Result {
    public object ResultA : Result()
    public object ResultB : Result()
    public object ResultC : Result()
}

public sealed class Effect {
    public object EffectA : Effect()
    public object EffectB : Effect()
}