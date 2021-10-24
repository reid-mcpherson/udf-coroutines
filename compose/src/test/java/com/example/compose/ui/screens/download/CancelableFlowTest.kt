package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class CancelableFlowTest {

    private val job: Job = mockk(relaxUnitFun = true)

    private val subject: CancelableFlow<Int> = spyk(object : CancelableFlowImpl<Int>() {
        override fun createJob(scope: CoroutineScope): Job = job
    })

    @Test
    fun `on start, emits idle`() {
        runBlockingTest {
            val emptyFlow = emptyFlow<Action>()
            subject.createControlFlow(emptyFlow, this)
                .test {
                    Truth.assertThat(awaitItem()).isEqualTo(JobStatus.Idle)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `on start command, when job is idle and start is called, emits working`() {
        runBlockingTest {
            val startFlow = flowOf(Action.Start)
            subject.createControlFlow(startFlow, this)
                .test {
                    awaitItem()
                    Truth.assertThat(awaitItem())
                        .isInstanceOf(JobStatus.Working::class.java)
                    awaitComplete()
                }
        }

        verify(exactly = 1) {
            subject.createJob(any())
        }
    }

    @Test
    fun `on start command, when jobStatus is working, and job is active, createJob is not called`() {
        every { job.isActive } returns true
        runBlockingTest {
            val startFlow = flowOf(Action.Start, Action.Start)
            subject.createControlFlow(startFlow, this)
                .test {
                    awaitItem()
                    val previousJobStatus = awaitItem()
                    Truth.assertThat(awaitItem()).isSameInstanceAs(previousJobStatus)
                    awaitComplete()
                }
        }

        verify(exactly = 1) {
            subject.createJob(any())
        }
    }

    @Test
    fun `on start command, when jobStatus is working and job is not active, creates a new job`() {
        every { job.isActive } returns false
        runBlockingTest {
            val startFlow = flowOf(Action.Start, Action.Start)
            subject.createControlFlow(startFlow, this)
                .test {
                    awaitItem()
                    val previousJobStatus = awaitItem()
                    Truth.assertThat(awaitItem()).isNotSameInstanceAs(previousJobStatus)
                    awaitComplete()
                }
        }
        verify(exactly = 2) {
            subject.createJob(any())
        }
    }

    @Test
    fun `on cancel command, when jobStatus is Idle returns Idle`() {
        runBlockingTest {
            val startFlow = flowOf(Action.Cancel)
            subject.createControlFlow(startFlow, this)
                .test {
                    awaitItem()
                    Truth.assertThat(awaitItem()).isEqualTo(JobStatus.Idle)
                    awaitComplete()
                }
        }
    }

    @Test
    fun `on cancel command and jobStatus is Working, cancels job`() {
        runBlockingTest {
            every { job.isActive } returns true
            val startFlow = flowOf(Action.Start, Action.Cancel)
            subject.createControlFlow(startFlow, this)
                .test {
                    awaitItem()
                    val toBeCanceled = awaitItem() as JobStatus.Working
                    Truth.assertThat(awaitItem()).isEqualTo(JobStatus.Idle)
                    awaitComplete()

                    verify { toBeCanceled.job.cancel() }
                }
        }
    }
}

class CancelableFlowExtensionTest {

    private val job: Job = mockk(relaxUnitFun = true)

    @Test
    fun `on start, emits idle`() {
        runBlockingTest {
            val emptyFlow = emptyFlow<Action>()
            val (controlFlow, _) = emptyFlow.toCancelableFlow<Int> { job }
            controlFlow.test {
                assertThat(awaitItem()).isEqualTo(JobStatus.Idle)
                awaitComplete()
            }
        }
    }

    @Test
    fun `on start command, when job is idle and start is called, emits working`() {
        runBlockingTest {
            val startFlow = flowOf(Action.Start)
            val createJob: ((MutableSharedFlow<Int>) -> Job) = spyk({ job })
            val (controlFlow, _) = startFlow.toCancelableFlow(createJob)
            controlFlow.test {
                awaitItem()
                assertThat(awaitItem()).isInstanceOf(JobStatus.Working::class.java)
                awaitComplete()
            }
            verify(exactly = 1) {
                createJob(any())
            }
        }
    }

    @Test
    fun `on start command, when jobStatus is working, and job is active, createJob is not called`() {
        every { job.isActive } returns true
        runBlockingTest {
            val startFlow = flowOf(Action.Start, Action.Start)
            val createJob: ((MutableSharedFlow<Int>) -> Job) = spyk({ job })
            val (controlFlow, _) = startFlow.toCancelableFlow(createJob)
            controlFlow.test {
                awaitItem()
                val previousJobStatus = awaitItem()
                assertThat(awaitItem()).isSameInstanceAs(previousJobStatus)
                awaitComplete()
            }
            verify(exactly = 1) {
                createJob(any())
            }
        }
    }

    @Test
    fun `on start command, when jobStatus is working and job is not active, creates a new job`() {
        every { job.isActive } returns false
        runBlockingTest {
            val startFlow = flowOf(Action.Start, Action.Start)
            val createJob: ((MutableSharedFlow<Int>) -> Job) = spyk({ job })
            val (controlFlow, _) = startFlow.toCancelableFlow(createJob)
            controlFlow.test {
                awaitItem()
                val previousJobStatus = awaitItem()
                assertThat(awaitItem()).isNotSameInstanceAs(previousJobStatus)
                awaitComplete()
            }
            verify(exactly = 2) {
                createJob(any())
            }

        }
    }

    @Test
    fun `on cancel command, when jobStatus is Idle returns Idle`() {
        runBlockingTest {
            val startFlow = flowOf(Action.Cancel)
            val (controlFlow, _) = startFlow.toCancelableFlow<Int> { job }
            controlFlow.test {
                awaitItem()
                assertThat(awaitItem()).isEqualTo(JobStatus.Idle)
                awaitComplete()
            }
        }
    }

    @Test
    fun `on cancel command and jobStatus is Working, cancels job`() {
        every { job.isActive } returns true
        runBlockingTest {
            val startFlow = flowOf(Action.Start, Action.Cancel)
            val (controlFlow, _) = startFlow.toCancelableFlow<Int> { job }
            controlFlow.test {
                awaitItem()
                val toBeCanceled = awaitItem() as JobStatus.Working
                assertThat(awaitItem()).isEqualTo(JobStatus.Idle)
                awaitComplete()
                verify { toBeCanceled.job.cancel() }
            }

        }
    }
}