package news.agoda.com.sample.entity

inline fun <reified T : ViewState> T.onSuccess(onSuccess: () -> Unit): T {
    if (!this.error && !this.init && !this.loading) onSuccess()
    return this
}

inline fun <reified T : ViewState> T.onFailure(onFailure: () -> Unit): T {
    if (this.error && !this.init) onFailure()
    return this
}

inline fun <reified T : ViewState> T.onStart(onStart: () -> Unit): T {
    if (this.loading && !this.init) onStart()
    return this
}

inline fun <reified T : ViewState> T.onFinish(onFinish: () -> Unit): T {
    if (!this.loading && !this.init) onFinish()
    return this
}

inline fun <reified T : ViewState> T.onInit(onInit: () -> Unit): T {
    if (this.init) onInit()
    return this
}

/**
 * Interface for data classes that represent state.
 * Base interface for all states in ViewModel
 */
interface ViewState {
    val error: Boolean
    val loading: Boolean
    val init: Boolean
}

data class NewsState(
    val data: List<NewsEntity>,
    override val error: Boolean = false,
    override val loading: Boolean = false,
    override val init: Boolean = true
) : ViewState
