package com.mizukami2005.mizukamitakamasa.qiitaclient.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.MaterialIcons
import com.mizukami2005.mizukamitakamasa.qiitaclient.MainActivity
import com.mizukami2005.mizukamitakamasa.qiitaclient.QiitaClientApp
import com.mizukami2005.mizukamitakamasa.qiitaclient.R
import com.mizukami2005.mizukamitakamasa.qiitaclient.client.ArticleClient
import com.mizukami2005.mizukamitakamasa.qiitaclient.databinding.ActivityArticleBinding
import com.mizukami2005.mizukamitakamasa.qiitaclient.databinding.ActivityMainBinding
import com.mizukami2005.mizukamitakamasa.qiitaclient.model.Article
import com.mizukami2005.mizukamitakamasa.qiitaclient.toast
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class ArticleActivity : AppCompatActivity() {

  @Inject
  lateinit var articleClient: ArticleClient

  lateinit var article: Article

  var checkStock = false

  var isMenu = false

  lateinit var data: SharedPreferences
  lateinit var token: String

  val TOKEN_PREFERENCES_NAME = "DataToken"
  val TOKEN = "token"

  companion object {

    private const val ARTICLE_EXTRA: String = "article"

    fun intent(context: Context, article: Article): Intent =
        Intent(context, ArticleActivity::class.java)
            .putExtra(ARTICLE_EXTRA, article)
  }
  private lateinit var binding: ActivityArticleBinding

//  var toolbar = binding.toolbar
//  var article_view = binding.articleView
//  var markdown_view = binding.markdownView
//
//  var app_bar = binding.appBar
//  var collapsing_toolbar = binding.collapsingToolbar
//  var toolbar_text_view = binding.toolbarTextView
//  var stock_button = binding.stockButton
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    (application as QiitaClientApp).component.inject(this)
    binding = ActivityArticleBinding.inflate(layoutInflater)
    setContentView(binding.root)
  var article_view = binding.articleView
  var markdown_view = binding.markdownView

  var app_bar = binding.appBar
  var collapsing_toolbar = binding.collapsingToolbar
  var toolbar_text_view = binding.toolbarTextView
  var stock_button = binding.stockButton
  var toolbar = binding.toolbar
    article = intent.getParcelableExtra(ARTICLE_EXTRA)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    article_view.setArticle(article, true)
    markdown_view.setMarkDownText(article.body)

    app_bar.addOnOffsetChangedListener { appBarLayout, offset ->
      collapsing_toolbar.title = ""
      toolbar_text_view.text = ""
      isMenu = false
      invalidateOptionsMenu()
      if (-offset + toolbar.height == collapsing_toolbar.height) {
        isMenu = true
        toolbar_text_view.text = article.title
        invalidateOptionsMenu()
      }
    }

    data = getSharedPreferences(TOKEN_PREFERENCES_NAME, Context.MODE_PRIVATE)
    token = data.getString(TOKEN, "")

    toolbar.setOnMenuItemClickListener(object: Toolbar.OnMenuItemClickListener {
      override fun onMenuItemClick(item: MenuItem?): Boolean {
        changeStockUnstock()
        return true
      }
    })

    processCheck(articleClient.checkStock("Bearer $token", article.id))
    stock_button.setImageDrawable(IconDrawable(this, MaterialIcons.md_folder).colorRes(R.color.colorQiita))
    stock_button.setOnClickListener {
      changeStockUnstock()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    if (isMenu) {
      menuInflater.inflate(R.menu.menu_main, menu)
      menu.findItem(R.id.action_stock).setIcon(IconDrawable(this, MaterialIcons.md_folder).colorRes(R.color.fab_background).actionBarSize())
      if (checkStock) {
        menu.findItem(R.id.action_stock).setIcon(IconDrawable(this, MaterialIcons.md_folder).colorRes(R.color.stocked_article).actionBarSize())
      }
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      android.R.id.home -> finish()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun processCheck(observable: Observable<String>) {
    observable
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .bindToLifecycle(MainActivity())
    .subscribe({
      val stateList = ColorStateList(
          arrayOf<IntArray>(intArrayOf()), intArrayOf(Color.parseColor("#C9302C"))
      )
      var article_view = binding.articleView
      var markdown_view = binding.markdownView

      var app_bar = binding.appBar
      var collapsing_toolbar = binding.collapsingToolbar
      var toolbar_text_view = binding.toolbarTextView
      var stock_button = binding.stockButton
      stock_button.backgroundTintList = stateList
      stock_button.setImageDrawable(IconDrawable(this, MaterialIcons.md_folder).colorRes(R.color.fab_background))
      checkStock = true
    }, {
      checkStock = false
    })
  }

  private fun processStock(observable: Observable<String>, isStock: Boolean) {
    observable
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doAfterTerminate { invalidateOptionsMenu() }
    .bindToLifecycle(MainActivity())
    .subscribe({
      if (isStock) {
        checkStock = true
        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(100)
      } else {
        checkStock = false
      }
    }, {
      if (isStock) {
        checkStock = false
      } else {
        checkStock = true
      }
    })
  }

  private fun changeStockUnstock() {
    var article_view = binding.articleView
    var markdown_view = binding.markdownView

    var app_bar = binding.appBar
    var collapsing_toolbar = binding.collapsingToolbar
    var toolbar_text_view = binding.toolbarTextView
    var stock_button = binding.stockButton
    if (token.length != 0 && !checkStock) {
      val stateList = ColorStateList(
          arrayOf<IntArray>(intArrayOf()), intArrayOf(Color.parseColor("#C9302C"))
      )
      stock_button.backgroundTintList = stateList
      stock_button.setImageDrawable(IconDrawable(this, MaterialIcons.md_folder).colorRes(R.color.fab_background))
      processStock(articleClient.stock("Bearer $token", article.id), true)
    } else if (token.length != 0 && checkStock) {
      val stateList = ColorStateList(
          arrayOf<IntArray>(intArrayOf()), intArrayOf(Color.parseColor("#FFFFFF"))
      )
      stock_button.backgroundTintList = stateList
      stock_button.setImageDrawable(IconDrawable(this, MaterialIcons.md_folder).colorRes(R.color.colorQiita))
      processStock(articleClient.unStock("Bearer $token", article.id), false)
    } else {
      toast(getString(R.string.not_login_message))
    }
  }
}
