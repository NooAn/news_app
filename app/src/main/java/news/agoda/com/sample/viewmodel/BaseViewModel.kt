package news.agoda.com.sample.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import news.agoda.com.sample.entity.ViewState
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<T : ViewState>(
    private val contextProvider: CoroutineContextProvider
) : ViewModel() {

    private val currentState
        get() = state.value
            ?: throw Exception("Please initialize state.value in the init block")

    abstract val state: MutableLiveData<T>

    /**
     * Network Request Execution Block DSL
     * Special for often async simple requests
     * param[block] Suspend lambda expression that is called from viewModel
     * For instance, this code do async request in repository for get some items with page
     * execution {
     *     val data = repository.getItems(page)
     *     post {
     *          copy(
     *              data = data.list,
     *              more = isMore,
     *              error = false,
     *              loading = false
     *           )
     *     }
     * }
     *
     */
    fun execution(block: suspend () -> Unit) =
        viewModelScope.launch {
            emitStart()
            withContext(contextProvider.IO) {
                try {
                    block()
                } catch (e: Exception) {
                    println("LOG Execution was failed: $e")
                    emitError()
                }
            }
        }

    fun post(block: T.() -> T) {
        println("LOG VIEW STATE: ${currentState.block()}")
        state.postValue(
            currentState.block()
        )
    }

    abstract fun emitStart()

    abstract fun emitError()

    /**
     * Used to observe this [BaseViewModel]'s view state from a [LifecycleOwner] like [FragmentActivity] or [Fragment].
     *
     * @param[owner] [LifecycleOwner] that controls observation.
     * @param[onChanged] Lambda expression that is called with a newly emitted state.
     */
    inline fun <R> observeState(owner: LifecycleOwner, crossinline onChanged: T.() -> R) {
        state.observe(owner, Observer { it.onChanged() })
    }
}


open class CoroutineContextProvider {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val IO: CoroutineContext by lazy { Dispatchers.IO }
}