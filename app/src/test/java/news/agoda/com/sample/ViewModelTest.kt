package news.agoda.com.sample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import news.agoda.com.sample.entity.*
import news.agoda.com.sample.repository.Repository
import news.agoda.com.sample.viewmodel.NewsViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.lang.Exception

@UseExperimental(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ViewModelTest {
    @Mock
    private lateinit var repository: Repository

    private lateinit var viewModel: NewsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    lateinit var lifecycleOwner: LifecycleOwner
    @Mock
    private lateinit var observer: NewsState.() -> (NewsState)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        lifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)
        viewModel = NewsViewModel(repository, TestContextProvider())

    }

    @Test
    fun test_loading() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val news = NewsEntity(title = "Title", articleUrl = "https://url", byline = "")

        `when`(repository.getNews()).thenReturn(listOf(news))


        viewModel.observeState(lifecycleOwner, observer)

        viewModel.loadData()


        assertTrue(repository.getNews().size == 1)

        val firstNewsState = NewsState(data = listOf(), init = false, loading = true, error = false)

        verify(observer).invoke(firstNewsState)

        val secondNewsState =
            NewsState(data = listOf(news), init = false, loading = false, error = false)

        verify(observer).invoke(secondNewsState)

    }

    @Test
    fun test_no_loading() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val news = NewsEntity(title = "Title", articleUrl = "https://url", byline = "")

        `when`(repository.getNews()).thenReturn(listOf(news))

        viewModel.observeState(lifecycleOwner, observer)

        val initNewsState =
            NewsState(data= emptyList(), error=false, loading=false, init=true)

        verify(observer).invoke(initNewsState)
    }

    @Test
    fun test_loadingSuccessState() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val news = NewsEntity(title = "Title2", articleUrl = "https://url2", byline = "")

        `when`(repository.getNews()).thenReturn(listOf(news))

        val observer: NewsState.() -> (NewsState) = {
            onSuccess {
                println("onSuccess")
                assertTrue(data[0] == news)
                assertTrue(data.size == 1)
            }
            onStart {
                println("onStart")
                assertTrue(loading)
            }
            onFinish {
                println("onFinish")
                assertFalse(loading)
            }
        }

        viewModel.observeState(lifecycleOwner, observer)

        viewModel.loadData()

    }

    @Test
    fun test_loadingFailState() = coroutinesTestRule.testDispatcher.runBlockingTest {

        `when`(repository.getNews()).thenAnswer { Exception("Some error") }

        val observer: NewsState.() -> (NewsState) = {
            onSuccess {
                println("onSuccess")
                assertFalse(true)
            }
            onFailure {
                println("onFailure")
                assertTrue(error)
            }
            onStart {
                println("onStart")
                assertTrue(loading)
            }
            onFinish {
                println("onFinish")
                assertFalse(loading)
            }
        }

        viewModel.observeState(lifecycleOwner, observer)

        viewModel.loadData()

    }

}
