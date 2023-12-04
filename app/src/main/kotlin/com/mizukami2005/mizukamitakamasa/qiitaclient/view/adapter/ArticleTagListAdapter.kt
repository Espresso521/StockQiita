package com.mizukami2005.mizukamitakamasa.qiitaclient.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.mizukami2005.mizukamitakamasa.qiitaclient.R
import com.mizukami2005.mizukamitakamasa.qiitaclient.model.ArticleTag
import com.mizukami2005.mizukamitakamasa.qiitaclient.util.TagUtils
import com.mizukami2005.mizukamitakamasa.qiitaclient.view.ArticleTagView

/**
 * Created by mizukamitakamasa on 2016/11/20.
 */
class ArticleTagListAdapter(private val context: Context) : BaseAdapter() {

  internal var articleTags = emptyArray<ArticleTag>()

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
      ((convertView as? ArticleTagView) ?: ArticleTagView(context)).apply {
        setArticleTag(articleTags[position])
        val saveTags = TagUtils().loadName(context, "TAG")
        val tag_check_box = findViewById(R.id.tag_check_box) as CheckBox
        tag_check_box.isChecked = false
        for (tag in saveTags) {
          if (tag == articleTags[position].id) {
            tag_check_box.isChecked = true
          }
        }
      }

  override fun getItem(position: Int): Any? = articleTags[position]

  override fun getItemId(position: Int): Long = 0

  override fun getCount(): Int = articleTags.size

  fun addList(list: Array<ArticleTag>) {
    articleTags += list
  }
}