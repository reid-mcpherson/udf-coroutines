package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.example.compose.repository.DownloadUpdate
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventToActionsInteractorTest {

    private val subject = EventToActionsInteractor()

    @Test
    fun `when OnClick event is received and state is Idle, Start action is emitted`() {
        runTest {
            val startFlow = flowOf(DownloadScreen.Event.OnClick(DownloadScreen.State.Idle))
            subject(startFlow)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadFeature.Action.Start)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `when OnClick event is received and state is Downloading, Cancel action is emitted`() {
        runTest {
            val startFlow =
                flowOf(DownloadScreen.Event.OnClick(DownloadScreen.State.Downloading(40)))
            subject(startFlow)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadFeature.Action.Cancel)
                    awaitComplete()
                }
        }
    }
}

class ActionToResultsInteractorTest {
    private val scope = TestScope()

    private lateinit var callbackFlow: MutableSharedFlow<DownloadFeature.Result>

    private val createJob: (MutableSharedFlow<DownloadFeature.Result>) -> Job = {
        callbackFlow = it
        mockk()
    }

    private val subject = ActionToResultsInteractor(scope, createJob)

    private val actions = MutableSharedFlow<DownloadFeature.Action>()

    private val downloadFlow = MutableSharedFlow<Int>()

    @Before
    fun setup() {
        mockkObject(DownloadUpdate)
        every { DownloadUpdate() } returns downloadFlow
    }

    @After
    fun teardown() {
        unmockkObject(DownloadUpdate)
    }

    @Test
    fun `when cancel action is received, idle status is emitted`() {
        runTest {
            subject(actions).test {
                awaitItem() //Initial event
                actions.emit(DownloadFeature.Action.Cancel)
                assertThat(awaitItem()).isEqualTo(DownloadFeature.Result.Idle)
            }
        }
    }

    @Test
    fun `when start action is received, downloading result is emitted`() {
        runTest {
            subject(actions).test {
                awaitItem()
                actions.emit(DownloadFeature.Action.Start)
                callbackFlow.emit(DownloadFeature.Result.Downloading(0, false))
                assertThat(awaitItem()).isEqualTo(DownloadFeature.Result.Downloading(0, false))
            }
        }
    }

    @Test
    fun `when download result is received, it is emitted`() {
        runTest {
            subject(actions).test {
                awaitItem()
                actions.emit(DownloadFeature.Action.Start)
                callbackFlow.emit(DownloadFeature.Result.Downloading(40, false))
                assertThat(awaitItem()).isEqualTo(
                    DownloadFeature.Result.Downloading(
                        40,
                        false
                    )
                )
            }
        }
    }

    @Test
    fun `when download percent is received, download result is emitted`() {
        runTest {
            val results = MutableSharedFlow<DownloadFeature.Result>()
            val job = ActionToResultsInteractor.createDownloadJob(results, this)
            results.asSharedFlow().test {
                launch {
                    downloadFlow.emit(34)
                }
                advanceUntilIdle()
                assertThat(awaitItem()).isEqualTo(DownloadFeature.Result.Downloading(34, false))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download percent is at 50 percent, show toast should be true`() {
        runTest {
            val results = MutableSharedFlow<DownloadFeature.Result>()
            val job = ActionToResultsInteractor.createDownloadJob(results, this)
            results.asSharedFlow().test {
                launch {
                    downloadFlow.emit(50)
                }
                advanceUntilIdle()
                assertThat(awaitItem()).isEqualTo(DownloadFeature.Result.Downloading(50, true))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download completes, Completed and then Idle are emitted`() {
        every { DownloadUpdate.invoke() } returns flowOf(100)
        runTest {
            val results = MutableSharedFlow<DownloadFeature.Result>()
            results.test {
                val job = ActionToResultsInteractor.createDownloadJob(results, this@runTest)
                awaitItem()
                assertThat(awaitItem()).isEqualTo(DownloadFeature.Result.Completed)
                assertThat(awaitItem()).isEqualTo(DownloadFeature.Result.Idle)
                job.cancel()
            }
        }
    }
}