package com.coroutines.udf

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import kotlin.time.ExperimentalTime

public class FlowViewModelImplTest {

    private companion object {
        private val defaultEventToActionInteractor: Interactor<Event, Action> = {
            it.map { event ->
                when (event) {
                    Event.EventA -> Action.ActionA
                    Event.EventB -> Action.ActionB
                    is Event.EventC -> Action.ActionC
                }
            }
        }
        private val defaultActionToResultInteractor: Interactor<Action, Result> = {
            it.map { action ->
                when (action) {
                    Action.ActionA -> Result.ResultA
                    Action.ActionB -> Result.ResultB
                    Action.ActionC -> Result.ResultC
                }
            }
        }
    }

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    public fun `when an action is received the state is changed`() {
        runBlocking {
            testFlow { scope, _ ->
                val subject = FlowViewModelTest(scope)
                subject.uiState.test {
                    subject.processUiEvent(Event.EventA)
                    assertThat(awaitItem()).isEqualTo(State.StateA)
                    subject.processUiEvent(Event.EventB)
                    assertThat(awaitItem()).isEqualTo(State.StateB)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    public fun `when event C occurs effect B is emitted`(): Unit =
        runBlocking {
            testFlow { scope, _ ->
                val subject = FlowViewModelTest(scope)
                subject.uiEffect.test {
                    subject.processUiEvent(Event.EventC("Test"))
                    assertThat(awaitItem()).isEqualTo(Effect.EffectB)
                }
            }
        }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    public fun `state can be received asynchronously`() {
        runBlocking {
            val eventsToActionsInteractor: Interactor<Event, Action> = {
                val actionAInteractor = it.filterIsInstance<Event.EventA>()
                    .map {
                        delay(5000)
                        Action.ActionA
                    }

                val actionBInteractor = it.filterIsInstance<Event.EventB>()
                    .map {
                        delay(2000)
                        Action.ActionB
                    }
                flowOf(actionAInteractor, actionBInteractor).flattenMerge()
            }

            testFlow { scope, dispatcher ->
                val subject =
                    FlowViewModelTest(
                        scope,
                        dispatcher,
                        eventToActionInteractor = eventsToActionsInteractor
                    )

                subject.uiState.test {
                    //Initial state is immediately received
                    assertThat(awaitItem()).isEqualTo(State.StateA)

                    subject.processUiEvent(Event.EventA)
                    subject.processUiEvent(Event.EventB)

                    dispatcher.advanceTimeBy(2500)
                    assertThat(awaitItem()).isEqualTo(State.StateB)

                    dispatcher.advanceTimeBy(2500)
                    assertThat(awaitItem()).isEqualTo(State.StateA)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private inline fun testFlow(
        testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
        testCoroutineScope: CoroutineScope = CoroutineScope(Job() + testCoroutineDispatcher),
        test: (scope: CoroutineScope, dispatcher: TestCoroutineDispatcher) -> Unit
    ) {
        test(testCoroutineScope, testCoroutineDispatcher)
        testCoroutineScope.cancel()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    private open class FlowViewModelTest(
        coroutineScope: CoroutineScope,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
        override val initialState: State = State.StateA,
        override val eventToActionInteractor: Interactor<Event, Action> = defaultEventToActionInteractor,
        override val actionToResultInteractor: Interactor<Action, Result> = defaultActionToResultInteractor
    ) : FlowViewModelImpl<State, Event, Action, Result, Effect>(
        coroutineDispatcher,
        coroutineScope
    ) {

        override suspend fun handleResult(previous: State, result: Result): State =
            when (result) {
                Result.ResultA -> State.StateA
                Result.ResultB -> State.StateB
                Result.ResultC -> {
                    emitEffect(Effect.EffectB)
                    previous
                }
            }
    }
}

public class InteractorTest {

    private var subject: Interactor<Event, Action> = {
        it.map { event ->
            when (event) {
                Event.EventA -> Action.ActionA
                Event.EventB -> Action.ActionB
                is Event.EventC -> Action.ActionC
            }
        }
    }

    @Test
    public fun `when upstream value is supplied interactor transforms to downstream value`() {
        runBlocking {
            flowOf(Event.EventA, Event.EventB)
                .let(subject)
                .test {
                    val a = awaitItem()
                    val b = awaitItem()
                    assertThat(a).isInstanceOf(Action.ActionA::class.java)
                    assertThat(b).isInstanceOf(Action.ActionB::class.java)
                    awaitComplete()
                }
        }
    }

    @FlowPreview
    @Test
    public fun `when events are asynchronous stream is not blocked`() {
        runBlocking {
            subject = { upstream ->
                val eventCEmptyFlow = upstream.filterIsInstance<Event.EventC>()
                    .filter {
                        it.value == null
                    }.map {
                        delay(6000) //Mimic network call
                        Action.ActionC
                    }
                val eventCFlow = upstream.filterIsInstance<Event.EventC>()
                    .filter {
                        it.value != null
                    }.map {
                        Action.ActionC
                    }

                val eventAFlow = upstream.filterIsInstance<Event.EventA>()
                    .map {
                        delay(3000) // Mimic network call
                        Action.ActionA
                    }

                val eventBFlow = upstream.filterIsInstance<Event.EventB>()
                    .map {
                        delay(1000)
                        Action.ActionB
                    }

                flowOf(eventCEmptyFlow, eventCFlow, eventAFlow, eventBFlow).flattenMerge()
            }

            val expectedItems =
                listOf(Action.ActionC, Action.ActionB, Action.ActionA, Action.ActionC)

            listOf(Event.EventC(null), Event.EventC("test"), Event.EventA, Event.EventB)
                .asFlow()
                .let(subject)
                .toList()
                .let { result ->
                    assertThat(result).containsExactlyElementsIn(expectedItems).inOrder()
                }
        }
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