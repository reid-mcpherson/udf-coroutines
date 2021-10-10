package com.arch.udf

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

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

    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    private val subject = FlowViewModelTest(scope, dispatcher)

    @Test
    public fun `when an action is received the state is changed`() {
        scope.runBlockingTest {
            subject.state.test {
                subject.processUiEvent(Event.EventA)
                assertThat(awaitItem()).isEqualTo(State.StateA)
                subject.processUiEvent(Event.EventB)
                assertThat(awaitItem()).isEqualTo(State.StateB)
            }
        }
    }

    @Test
    public fun `when event C occurs effect B is emitted`(): Unit =
        scope.runBlockingTest {
            subject.state.test {
                subject.effect.test {
                    subject.processUiEvent(Event.EventC("Test"))
                    assertThat(awaitItem()).isEqualTo(Effect.EffectB)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    public fun `state can be received asynchronously`() {
        scope.runBlockingTest {
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

            val subject =
                FlowViewModelTest(
                    scope,
                    dispatcher,
                    eventToActionInteractor = eventsToActionsInteractor
                )

            subject.state.test {
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

    private open class FlowViewModelTest(
        coroutineScope: CoroutineScope,
        coroutineDispatcher: CoroutineDispatcher,
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

    @Test
    public fun `when events are asynchronous stream is not blocked`() {
        runBlockingTest {
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