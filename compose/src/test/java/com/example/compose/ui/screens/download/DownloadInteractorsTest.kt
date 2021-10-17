package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.example.compose.repository.DownloadUpdate
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test

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


class CancelableFlowTest {

    private val job: Job = mockk(relaxUnitFun = true)

    private val subject: CancelableFlow<Int> = spyk(object : CancelableFlowImpl<Int>() {
        override fun createJob(): Job = job
    })

    @Test
    fun `on start, emits idle`() {
        runBlockingTest {
            emptyFlow<CancelableFlow.Action>()
                .let(subject::createControlFlow)
                .test {
                    assertThat(awaitItem()).isEqualTo(CancelableFlow.JobStatus.Idle)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `on start command, when job is idle and start is called, emits working`() {
        runBlockingTest {
            flowOf(CancelableFlow.Action.Start)
                .let(subject::createControlFlow)
                .test {
                    awaitItem()
                    assertThat(awaitItem()).isInstanceOf(CancelableFlow.JobStatus.Working::class.java)
                    awaitComplete()
                }
        }

        verify(exactly = 1) {
            subject.createJob()
        }
    }

    @Test
    fun `on start command, when jobStatus is working, and job is active, createJob is not called`() {
        every { job.isActive } returns true
        runBlockingTest {
            flowOf(CancelableFlow.Action.Start, CancelableFlow.Action.Start)
                .let(subject::createControlFlow)
                .test {
                    awaitItem()
                    val previousJobStatus = awaitItem()
                    assertThat(awaitItem()).isSameInstanceAs(previousJobStatus)
                    awaitComplete()
                }
        }

        verify(exactly = 1) {
            subject.createJob()
        }
    }

    @Test
    fun `on start command, when jobStatus is working and job is not active, creates a new job`() {
        every { job.isActive } returns false
        runBlockingTest {
            flowOf(CancelableFlow.Action.Start, CancelableFlow.Action.Start)
                .let(subject::createControlFlow)
                .test {
                    awaitItem()
                    val previousJobStatus = awaitItem()
                    assertThat(awaitItem()).isNotSameInstanceAs(previousJobStatus)
                    awaitComplete()
                }
        }
        verify(exactly = 2) {
            subject.createJob()
        }
    }

    @Test
    fun `on cancel command, when jobStatus is Idle returns Idle`() {
        runBlockingTest {
            flowOf(CancelableFlow.Action.Cancel)
                .let(subject::createControlFlow)
                .test {
                    awaitItem()
                    assertThat(awaitItem()).isEqualTo(CancelableFlow.JobStatus.Idle)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `on cancel command and jobStatus is Working, cancels job`() {
        runBlockingTest {
            every { job.isActive } returns true
            flowOf(CancelableFlow.Action.Start, CancelableFlow.Action.Cancel)
                .let(subject::createControlFlow)
                .test {
                    awaitItem()
                    val toBeCanceled = awaitItem() as CancelableFlow.JobStatus.Working
                    assertThat(awaitItem()).isEqualTo(CancelableFlow.JobStatus.Idle)
                    awaitComplete()

                    verify { toBeCanceled.job.cancel() }
                }
        }
    }
}
