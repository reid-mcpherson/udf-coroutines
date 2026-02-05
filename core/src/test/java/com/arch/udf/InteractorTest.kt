package com.arch.udf

import app.cash.turbine.test
import com.google.common.truth.Truth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Test

public class InteractorTest {

    private var subject: Interactor<Event, Action> = { upstream ->
        upstream.map { event ->
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
            val startFlow = flowOf(Event.EventA, Event.EventB)
            subject(startFlow)
                .test {
                    val a = awaitItem()
                    val b = awaitItem()
                    Truth.assertThat(a).isInstanceOf(Action.ActionA::class.java)
                    Truth.assertThat(b).isInstanceOf(Action.ActionB::class.java)
                    awaitComplete()
                }
        }
    }

    @Test
    public fun `when events are asynchronous stream is not blocked`() {
        runTest {
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

            val startFlow =
                flowOf(Event.EventC(null), Event.EventC("test"), Event.EventA, Event.EventB)
            subject(startFlow)
                .toList()
                .let { result ->
                    Truth.assertThat(result).containsExactlyElementsIn(expectedItems).inOrder()
                }
        }
    }
}