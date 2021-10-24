package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.example.compose.repository.DownloadUpdate
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.*
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
            subject(startFlow, this)
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
            subject(startFlow, this)
                .test {
                    assertThat(awaitItem()).isEqualTo(DownloadViewModel.Action.Cancel)
                    awaitComplete()
                }
        }
    }
}

class ActionToResultsInteractorTest {
    private val controlFlow = MutableSharedFlow<JobStatus>()

    private val resultsFlow = MutableSharedFlow<DownloadViewModel.Result>()

    private val subject = ActionToResultsInteractor()

    private val actions = MutableSharedFlow<DownloadViewModel.Action>()

    @Test
    fun `when cancel action is received idle status is emitted`() {
        runBlockingTest {
            subject(actions, this).test {
                awaitItem() //Initial event
                actions.emit(DownloadViewModel.Action.Cancel)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
            }
        }
    }

    @Test
    fun `when start action is received job status `() {
        runBlockingTest {
            subject(actions, this).test {
                awaitItem()
                actions.emit(DownloadViewModel.Action.Start)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
            }
        }
    }

    //FIXME: Mocking extension functions can be difficult.
//    @Test
//    fun `when download result is received, it is emitted`() {
//        mockkStatic("com.example.compose.ui.screens.download.CancelableFlowKt") {
//            every {
//                actions.map { action ->
//                    when (action) {
//                        DownloadViewModel.Action.Start -> Action.Start
//                        DownloadViewModel.Action.Cancel -> Action.Cancel
//                    }
//                }.toCancelableFlow<DownloadViewModel.Result>(any())
//            } answers {
//                Pair(controlFlow.asSharedFlow(), resultsFlow.asSharedFlow())
//            }
//
//            runBlockingTest {
//                subject(actions, this).test {
//                    awaitItem()
//                    resultsFlow.emit(DownloadViewModel.Result.Downloading(40, false))
//                    assertThat(awaitItem()).isEqualTo(
//                        DownloadViewModel.Result.Downloading(
//                            40,
//                            false
//                        )
//                    )
//                }
//            }
//        }
//    }
}

class DownloadCancelableFlowTest {
    private val subject = DownloadCancelableFlow()

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
    fun `when download percent is received, download result is emitted`() {
        runBlockingTest {
            val job = subject.createJob(this)
            subject.results.test {
                downloadFlow.emit(34)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Downloading(34, false))
            }
            job.cancel()
        }
    }

    @Test
    fun `when download percent is at 50 percent, show toast should be true`() {
        runBlockingTest {
            val job = subject.createJob(this)
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
        runBlockingTest {
            subject.results.test {
                val job = subject.createJob(this@runBlockingTest)
                awaitItem()
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Completed)
                assertThat(awaitItem()).isEqualTo(DownloadViewModel.Result.Idle)
                job.cancel()
            }
        }
    }
}