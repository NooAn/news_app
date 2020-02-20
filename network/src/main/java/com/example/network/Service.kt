import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import retrofit2.Retrofit.Builder
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class Api {

    companion object {
        const val BASE_URL = "https://api.myjson.com"
        const val FEED_URL = "https://api.myjson.com/bins/nl6jh"
        const val FEED_URL_MOCK = "http://www.mocky.io/v2/573c89f31100004a1daa8adb"
    }

}

val networkModule = module {

    factory(TypeQualifier(Builder::class)) {
        Builder()
    }

    factory { createOkHttpBuilder(get(), get()) }

    single { createSSLSocketFactory() }

    single { createTrustManagers() }

    factory {
        val okHttpClientBuilder: OkHttpClient.Builder = get()
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder
            .addInterceptor(logInterceptor)

        okHttpClientBuilder.build()
    }

}

fun createSSLSocketFactory(): SSLSocketFactory {
    val sslContext: SSLContext
    try {
        sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, createTrustManagers(), null)
    } catch (e: KeyManagementException) {
        throw IllegalArgumentException(" Can't create SSLSocketFactory!", e)
    } catch (e: NoSuchAlgorithmException) {
        throw IllegalArgumentException(" Can't create SSLSocketFactory!", e)
    }

    return sslContext.socketFactory

}

fun createTrustManagers(): Array<TrustManager> {
    var trustManagerFactory: TrustManagerFactory? = null
    try {
        trustManagerFactory = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory!!.init(null as KeyStore?)
    } catch (e: NoSuchAlgorithmException) {
    } catch (e: KeyStoreException) {
    }

    return if (trustManagerFactory != null)
        trustManagerFactory.trustManagers
    else
        emptyArray()
}

fun createOkHttpBuilder(
    sslSocketFactory: SSLSocketFactory,
    trustManagers: Array<TrustManager>
): OkHttpClient.Builder {
    val builder = OkHttpClient.Builder()
    builder.connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
    val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_1, TlsVersion.TLS_1_2)
        .build()
    val specs = listOf(cs, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)
    builder.connectionSpecs(specs)
    builder.sslSocketFactory(sslSocketFactory, trustManagers[0] as X509TrustManager)
    return builder
}

