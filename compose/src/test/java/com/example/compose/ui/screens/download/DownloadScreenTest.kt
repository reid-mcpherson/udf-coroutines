package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.example.compose.ui.screens.download.DownloadScreen.State.Downloading
import com.example.compose.ui.screens.download.DownloadScreen.State.Idle
import com.example.compose.ui.screens.download.DownloadViewModel.Result.Completed
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import com.example.compose.ui.screens.download.DownloadViewModel.Result.Downloading as DownloadingResult
import com.example.compose.ui.screens.download.DownloadViewModel.Result.Idle as IdleResult


class DownloadViewModelTest {
    private val subject = DownloadViewModel()

    @Test
    fun `initial state is idle`() {
        assertThat(subject.initialState).isEqualTo(Idle)
    }

    @Test
    fun `when result idle is received, state idle is emitted`() {
        runBlockingTest {
            val state = subject.handleResult(
                Downloading(20, false),
                IdleResult
            )
            assertThat(state).isEqualTo(Idle)
        }
    }

    @Test
    fun `when previous state is idle and result is Downloading, emits download state`() {
        runBlockingTest {
            val state = subject.handleResult(
                Idle,
                DownloadingResult(42, true)
            )
            assertThat(state).isEqualTo(Downloading(42, false))
        }
    }

    @Test
    fun `when previous state is downloading and result is downloading, emits download state`() {
        runBlockingTest {
            val state = subject.handleResult(
                Downloading(49, false),
                DownloadingResult(50, true)
            )
            assertThat(state).isEqualTo(Downloading(50, true))
        }
    }

    @Test
    fun `when downloading is completed, emits CompletedEffect`() {
        runBlockingTest {
            subject.effect.test {
                val state = subject.handleResult(Downloading(100, false), Completed)
                assertThat(state).isEqualTo(Downloading(100, false))
                assertThat(awaitItem()).isEqualTo(DownloadScreen.CompletedEffect)
            }
        }
    }
}

