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
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventToActionsInteractorTest {

    private val subject = EventToActionsInteractor()

    @Test
    fun `when OnClick event is received and state is Idle, Start action is emitted`() {
        runBlockingTest {
            val startFlow = flowOf(DownloadScreen.Event.OnClick(DownloadScreen.State.Idle))
            subject(startFlow)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadViewModel.Action.Start)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `when OnClick event is received and state is Downloading, Cancel action is emitted`() {
        runBlockingTest {
            val startFlow =
                flowOf(DownloadScreen.Event.OnClick(DownloadScreen.State.Downloading(40, false)))
            subject(startFlow)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadViewModel.Action.Cancel)
                    awaitComplete()
                }
        }
    }
}

class ActionToResultsInteractorTest {
    private val scope = TestCoroutineScope()

    private lateinit var callbackFlow: MutableSharedFlow<DownloadViewModel.Result>

    private val createJob: (MutableSharedFlow<DownloadViewModel.Result>) -> Job = {
        callbackFlow = it
        mockk()
    }

    private val subject = ActionToResultsInteractor(scope, createJob)

    private val actions = MutableSharedFlow<DownloadViewModel.Action>()

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
        runBlockingTest {
            subject(actions).test {
                awaitItem() //Initial event
                actions.emit(DownloadViewModel.Action.Cancel)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
            }
        }
    }

    @Test
    fun `when start action is received, downloading result is emitted`() {
        runBlockingTest {
            subject(actions).test {
                awaitItem()
                actions.emit(DownloadViewModel.Action.Start)
                callbackFlow.emit(DownloadViewModel.Result.Downloading(0, false))
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(0, false))
            }
        }
    }

    @Test
    fun `when download result is received, it is emitted`() {
        runBlockingTest {
            subject(actions).test {
                awaitItem()
                actions.emit(DownloadViewModel.Action.Start)
                callbackFlow.emit(DownloadViewModel.Result.Downloading(40, false))
                assertThat(awaitItem()).isEqualTo(
                    DownloadViewModel.Result.Downloading(
                        40,
                        false
                    )
                )
            }
        }
    }

    @Test
    fun `when download percent is received, download result is emitted`() {
        runBlockingTest {
            val results = MutableSharedFlow<DownloadViewModel.Result>()
            val job = ActionToResultsInteractor.createDownloadJob(results, this)
            results.asSharedFlow().test {
                downloadFlow.emit(34)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(34, false))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download percent is at 50 percent, show toast should be true`() {
        runBlockingTest {
            val results = MutableSharedFlow<DownloadViewModel.Result>()
            val job = ActionToResultsInteractor.createDownloadJob(results, this)
            results.asSharedFlow().test {
                downloadFlow.emit(50)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(50, true))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download completes, Completed and then Idle are emitted`() {
        every { DownloadUpdate.invoke() } returns flowOf(100)
        runBlockingTest {
            val results = MutableSharedFlow<DownloadViewModel.Result>()
            results.test {
                val job = ActionToResultsInteractor.createDownloadJob(results, this@runBlockingTest)
                awaitItem()
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Completed)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
                job.cancel()
            }
        }
    }
}