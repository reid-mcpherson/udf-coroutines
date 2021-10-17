package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.example.compose.repository.DownloadUpdate
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
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
            flowOf(DownloadScreen.Event.OnClick(DownloadScreen.State.Idle))
                .let(subject)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadViewModel.Action.Start)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `when OnClick event is received and state is Downloading, Cancel action is emitted`() {
        runBlockingTest {
            flowOf(DownloadScreen.Event.OnClick(DownloadScreen.State.Downloading(40, false)))
                .let(subject)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadViewModel.Action.Cancel)
                    awaitComplete()
                }
        }
    }
}

class ActionToResultsInteractorTest {
    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    private val controlFlow = MutableSharedFlow<CancelableFlow.JobStatus>()

    private val resultsFlow = MutableSharedFlow<DownloadViewModel.Result>()

    private val cancelableDownloadFlow: CancelableFlow<DownloadViewModel.Result> =
        object : CancelableFlowImpl<DownloadViewModel.Result>() {

            override val results: SharedFlow<DownloadViewModel.Result> = resultsFlow.asSharedFlow()

            override fun createJob(): Job = mockk()

            override fun createControlFlow(upstream: Flow<CancelableFlow.Action>): Flow<CancelableFlow.JobStatus> =
                controlFlow
        }

    private val subject = ActionToResultsInteractor(scope, cancelableDownloadFlow)

    private val actions = MutableSharedFlow<DownloadViewModel.Action>()

    @Test
    fun `when cancel action is received idle status is emitted`() {
        scope.runBlockingTest {
            subject.invoke(actions).test {
                controlFlow.emit(CancelableFlow.JobStatus.Idle)
                awaitItem() //Initial event
                actions.emit(DownloadViewModel.Action.Cancel)
                controlFlow.emit(CancelableFlow.JobStatus.Idle)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
            }
        }
    }

    @Test
    fun `when start action is received starts emits nothing`() {
        scope.runBlockingTest {
            subject.invoke(actions).test {
                controlFlow.emit(CancelableFlow.JobStatus.Idle)
                awaitItem()
                actions.emit(DownloadViewModel.Action.Start)
                controlFlow.emit(CancelableFlow.JobStatus.Working(mockk()))
            }
        }
    }

    @Test
    fun `when download result is received, it is emitted`() {
        scope.runBlockingTest {
            subject.invoke(actions).test {
                controlFlow.emit(CancelableFlow.JobStatus.Idle)
                awaitItem()
                resultsFlow.emit(DownloadViewModel.Result.Downloading(40, false))
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(40, false))
            }
        }
    }
}

class DownloadCancelableFlowTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    private val subject = DownloadCancelableFlow(scope)

    private val downloadFlow = MutableSharedFlow<Int>()

    @Before
    fun setup() {
        mockkObject(DownloadUpdate)
        every { DownloadUpdate.invoke() } returns downloadFlow
    }

    @After
    fun teardown() {
        unmockkObject(DownloadUpdate)
    }

    @Test
    fun `when download percent is received, download result is emitted`() {
        scope.runBlockingTest {
            val job = subject.createJob()
            subject.results.test {
                downloadFlow.emit(34)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(34, false))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download percent is at 50 percent, show toast should be true`() {
        scope.runBlockingTest {
            val job = subject.createJob()
            subject.results.test {
                downloadFlow.emit(50)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(50, true))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download completes, Completed and then Idle are emitted`() {
        every { DownloadUpdate.invoke() } returns flowOf(100)
        scope.runBlockingTest {
            subject.results.test {
                val job = subject.createJob()
                awaitItem()
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Completed)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
                job.cancel()
            }
        }
    }
}