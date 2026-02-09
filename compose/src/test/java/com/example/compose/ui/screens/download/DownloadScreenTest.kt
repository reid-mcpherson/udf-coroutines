package com.example.compose.ui.screens.download

import app.cash.turbine.test
import com.example.compose.ui.screens.download.DownloadScreen.State.Downloading
import com.example.compose.ui.screens.download.DownloadScreen.State.Idle
import com.example.compose.ui.screens.download.DownloadFeature.Result.Completed
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.example.compose.ui.screens.download.DownloadFeature.Result.Downloading as DownloadingResult
import com.example.compose.ui.screens.download.DownloadFeature.Result.Idle as IdleResult


class DownloadFeatureTest {
    private val subject = DownloadFeature()

    @Test
    fun `initial state is idle`() {
        assertThat(subject.initial).isEqualTo(Idle)
    }

    @Test
    fun `when result idle is received, state idle is emitted`() {
        runTest {
            val state = subject.handleResult(
                Downloading(20),
                IdleResult
            )
            assertThat(state).isEqualTo(Idle)
        }
    }

    @Test
    fun `when previous state is idle and result is Downloading, emits download state`() {
        runTest {
            val state = subject.handleResult(
                Idle,
                DownloadingResult(42, true)
            )
            assertThat(state).isEqualTo(Downloading(42))
        }
    }

    @Test
    fun `when previous state is downloading and result is downloading, emits download state`() {
        runTest {
            val state = subject.handleResult(
                Downloading(49),
                DownloadingResult(50, true)
            )
            assertThat(state).isEqualTo(Downloading(50))
        }
    }

    @Test
    fun `when download is at 50 percent, emits HalfwayEffect`() = runTest {
        subject.effects.test {
            subject.handleResult(Downloading(50), DownloadingResult(50, true))
            assertThat(awaitItem()).isEqualTo(DownloadScreen.Effect.HalfwayEffect)
            }
        }

    @Test
    fun `when downloading is completed, emits CompletedEffect`() {
        runTest {
            subject.effects.test {
                val state = subject.handleResult(Downloading(100), Completed)
                assertThat(state).isEqualTo(Downloading(100))
                assertThat(awaitItem()).isEqualTo(DownloadScreen.Effect.CompletedEffect)
            }
        }
    }
}

