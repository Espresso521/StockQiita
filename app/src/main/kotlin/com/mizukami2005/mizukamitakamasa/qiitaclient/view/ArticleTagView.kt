package com.mizukami2005.mizukamitakamasa.qiitaclient.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mizukami2005.mizukamitakamasa.qiitaclient.R
import com.mizukami2005.mizukamitakamasa.qiitaclient.model.ArticleTag

/**
 * Created by mizukamitakamasa on 2016/11/19.
 */
class ArticleTagView : FrameLayout {

    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_article_tag, this)
    }

    fun setArticleTag(articleTag: ArticleTag) {
        (findViewById(R.id.tag_name_text_view) as TextView).text = articleTag.id
        //tag_name_text_view.text = articleTag.id
        //followers_count_text_view.text = articleTag.followers_count.toString() + " Followers"
        (findViewById(R.id.followers_count_text_view) as TextView).text =
            articleTag.followers_count.toString() + " Followers"
        //Glide.with(context).load(articleTag.icon_url).into(tag_image_view)
        Glide.with(context).load(articleTag.icon_url)
            .into(findViewById(R.id.tag_image_view) as ImageView)
    }
}