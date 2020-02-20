package news.agoda.com.sample.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import news.agoda.com.sample.*
import news.agoda.com.sample.entity.*
import news.agoda.com.sample.ui.DetailViewActivity.*
import news.agoda.com.sample.viewmodel.NewsViewModel


class MainActivity : FragmentActivity() {
    private var newsItemList: MutableList<NewsEntity>? = null

    private val model by viewModel<NewsViewModel>()

    private val newsObserver: NewsState.() -> NewsState = {
        onSuccess {
            println(data)
            onNewsReceived(data)
        }
        onFailure {
            // show error
            Toast.makeText(this@MainActivity, "Sorry, I am broken", Toast.LENGTH_SHORT).show()
        }
        onStart {
            // show progress bar
        }
        onFinish {
            // hide progress bar
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fresco.initialize(this)

        newsItemList = ArrayList()

        model.observeState(this, newsObserver)

        model.loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun onNewsReceived(newsEntityList: List<NewsEntity>) {
        newsItemList?.clear()
        newsItemList?.addAll(newsEntityList)
        val adapter = NewsListAdapter(
            this,
            R.layout.list_item_news,
            newsItemList
        )
        list.adapter = adapter

        list.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val newsEntity = newsEntityList[position]
                Intent(this@MainActivity, DetailViewActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(TITLE, newsEntity.title)
                        putString(STORY_URL, newsEntity.articleUrl)
                        putString(SUMMARY, newsEntity.summary)
                        if (newsEntity.mediaEntityList?.isNotEmpty() == true)
                            putString(IMAGE_URL, newsEntity.mediaEntityList[0].url)
                    })
                    startActivity(this)
                }

            }

    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}
