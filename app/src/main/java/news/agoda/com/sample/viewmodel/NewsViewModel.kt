package news.agoda.com.sample.viewmodel

import androidx.lifecycle.MutableLiveData
import news.agoda.com.sample.entity.NewsState
import news.agoda.com.sample.repository.Repository


class NewsViewModel(
    private val repository: Repository,
    coroutineContext: CoroutineContextProvider
) : BaseViewModel<NewsState>(contextProvider = coroutineContext) {
    override val state = MutableLiveData<NewsState>()

    init {
        state.value = NewsState(data = listOf(), init = true)
    }

    override fun emitStart() {
        post { copy(error = false, loading = true, init = false) }
    }

    override fun emitError() {
        post { copy(error = true, loading = false) }
    }

    fun loadData() {
        execution {
            repository.getNews().let {
                post {
                    copy(
                        data = it,
                        error = false,
                        loading = false
                    )
                }
            }
        }
    }

}