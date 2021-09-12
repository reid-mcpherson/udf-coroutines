package com.coroutines.udf

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Test
import kotlin.time.ExperimentalTime


public class FlowViewModelImplTest {

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
    public fun `when emit effect is called effect is emitted`(): Unit =
        runBlocking {
            testFlow { scope, _ ->
                val subject = FlowViewModelTest(scope)
                subject.uiEffect.test {
                    subject.emitEffectBackdoor(Effect.EffectB)
                    assertThat(awaitItem()).isEqualTo(Effect.EffectB)
                    subject.emitEffectBackdoor(Effect.EffectA)
                    assertThat(awaitItem()).isEqualTo(Effect.EffectA)
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
        suspend fun emitEffectBackdoor(effect: Effect) = emitEffect(effect)

        override suspend fun handleResult(previous: State, result: Result): State =
            when (result) {
                Result.ResultA -> State.StateA
                Result.ResultB -> State.StateB
            }
    }

    private companion object {
        private val defaultEventToActionInteractor: Interactor<Event, Action> = {
            it.map { event ->
                when (event) {
                    Event.EventA -> Action.ActionA
                    Event.EventB -> Action.ActionB
                }
            }
        }
        private val defaultActionToResultInteractor: Interactor<Action, Result> = {
            it.map { action ->
                when (action) {
                    Action.ActionA -> Result.ResultA
                    Action.ActionB -> Result.ResultB
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
    }

    public sealed class Action {
        public object ActionA : Action()
        public object ActionB : Action()
    }

    public sealed class Result {
        public object ResultA : Result()
        public object ResultB : Result()
    }

    public sealed class Effect {
        public object EffectA : Effect()
        public object EffectB : Effect()
    }
}