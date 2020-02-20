package news.agoda.com.sample.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import news.agoda.com.sample.entity.MediaEntity
import news.agoda.com.sample.repository.NewsApi
import news.agoda.com.sample.repository.NewsRepository
import news.agoda.com.sample.repository.Repository
import news.agoda.com.sample.viewmodel.CoroutineContextProvider
import news.agoda.com.sample.viewmodel.NewsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


val coreModule = module {
    single<Repository> {
        NewsRepository(get())
    }
    single<Gson> {
        GsonBuilder().registerTypeAdapterFactory(MediaListTypeAdapter()).create()
    }
    single<NewsApi> {
        val retrofitBuilder: Retrofit.Builder = get(TypeQualifier(Retrofit.Builder::class))
        retrofitBuilder
            .client(get())
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(NewsApi::class.java)
    }
    single {
        CoroutineContextProvider()
    }
    viewModel { NewsViewModel(get(), get()) }
}


class MediaListTypeAdapter : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        if (type?.rawType != List::class.java) {
            return null
        }
        val typeOfT = TypeToken.getParameterized(List::class.java, MediaEntity::class.java)
        if (type != typeOfT) return null
        val delegate = gson?.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            @Throws(IOException::class)

            override fun write(out: JsonWriter, value: T) {

                delegate?.write(out, value)
            }

            @Throws(IOException::class)
            override fun read(input: JsonReader): T? {
                if (input.peek() == JsonToken.STRING) {
                    input.skipValue()
                    return null
                }
                return delegate?.read(input)
            }
        }

    }
}