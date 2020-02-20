package news.agoda.com.sample.repository

import Api.Companion.FEED_URL
import Api.Companion.FEED_URL_MOCK
import news.agoda.com.sample.entity.News
import news.agoda.com.sample.entity.NewsEntity
import retrofit2.http.GET
import retrofit2.http.Url
import java.lang.Exception

interface NewsApi {
    @GET
    suspend fun getNews(@Url url: String): News
}

interface Repository {
    suspend fun getNews(): List<NewsEntity>
}

class NewsRepository(private val api: NewsApi) :
    Repository {
    override suspend fun getNews(): List<NewsEntity> {
        var news: News? = null
        try {
            news = api.getNews(FEED_URL)
        } catch (e: Exception) {
            news = api.getNews(FEED_URL_MOCK)
        }
        return news?.results.orEmpty()
    }
}

