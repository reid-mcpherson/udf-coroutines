package com.arch.udf

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal abstract class FlowViewModelCoreTest(
    private val createSubject: (scope: CoroutineScope, dispatcher: CoroutineDispatcher, eventToActionInteractor: Interactor<Event, Action>) -> FlowViewModelCore<State, Event, Action, Result, Effect>,
) {

    @Test
    fun `when an action is received the state is changed`() =
        runTest {
            val dispatcher = StandardTestDispatcher()
            val scope = TestScope(dispatcher)
            val subject =
                createSubject(scope, dispatcher, Interactors.defaultEventToActionInteractor)
            subject.state.test {
                subject.processUiEvent(Event.EventA)
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
                createSubject(scope, dispatcher, Interactors.defaultEventToActionInteractor)
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
        val dispatcher = StandardTestDispatcher()
        val scope = TestScope(dispatcher)
        val eventsToActionsInteractor: Interactor<Event, Action> = { upstream ->

            val actionAInteractor = upstream.filterIsInstance<Event.EventA>()
                .map {
                    println(3)
                    delay(5000)
                    println(4)
                    Action.ActionA
                }

            val actionBInteractor = upstream.filterIsInstance<Event.EventB>()
                .map {
                    println(5)
                    delay(2000)
                    println(6)
                    Action.ActionB
                }
            flowOf(actionAInteractor, actionBInteractor).flattenMerge()
        }
        val subject = createSubject(scope, dispatcher, eventsToActionsInteractor)
        subject.state.test {
            //Initial state is immediately received
            assertThat(awaitItem()).isEqualTo(State.StateA)
            subject.processUiEvent(Event.EventA)
            dispatcher.scheduler.runCurrent()
            subject.processUiEvent(Event.EventB)
            dispatcher.scheduler.advanceTimeBy(2500)
            assertThat(awaitItem()).isEqualTo(State.StateB)
            dispatcher.scheduler.advanceTimeBy(2501)
            assertThat(awaitItem()).isEqualTo(State.StateA)
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
    FlowViewModelCoreTest(createSubject = { scope, dispatcher, eventToActionInteractor ->
        FlowViewModelImplSubject(
            scope,
            dispatcher,
            eventToActionInteractor = eventToActionInteractor
        )
    })


internal class FlowViewModelAndroidTest :
    FlowViewModelCoreTest(createSubject = { scope, dispatcher, eventToActionInteractor ->
        FlowViewModelAndroidSubject(scope, dispatcher, eventToActionInteractor = eventToActionInteractor)
    })

private class FlowViewModelImplSubject(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
    override val initialState: State = State.StateA,
    override val eventToActionInteractor: Interactor<Event, Action> = Interactors.defaultEventToActionInteractor,
    override val actionToResultInteractor: Interactor<Action, Result> = Interactors.defaultActionToResultInteractor
) : FlowViewModelImpl<State, Event, Action, Result, Effect>(
    coroutineDispatcher,
    coroutineScope
) {
    override suspend fun handleResult(previous: State, result: Result): State =
        handleResult(previous, result, ::emit)
}

private class FlowViewModelAndroidSubject(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
    override val initialState: State = State.StateA,
    override val eventToActionInteractor: Interactor<Event, Action> = Interactors.defaultEventToActionInteractor,
    override val actionToResultInteractor: Interactor<Action, Result> = Interactors.defaultActionToResultInteractor
) : FlowViewModelAndroid<State, Event, Action, Result, Effect>(
    coroutineDispatcher,
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