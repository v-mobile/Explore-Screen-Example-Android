package io.dzain.dzain.presentation.adapters

import android.view.View
import coil.load
import io.dzain.dzain.R
import io.dzain.dzain.data.model.AnimatedWrapper
import io.dzain.dzain.presentation.adapters.base.BaseListAdapter
import io.dzain.shared.domain.model.clean.Category
import kotlinx.android.synthetic.main.top_categories_list_item_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TopCategoriesAdapter : BaseListAdapter<AnimatedWrapper<Category>>() {

    private var clickBody: Category.() -> Unit = {}

    override val layoutResId = R.layout.top_categories_list_item_view

    override fun onBind(itemView: View, item: AnimatedWrapper<Category>, position: Int) {
        itemView.categoryTextView.text = item.data.label
        itemView.postCountTextView.text = "${item.data.pollsCount} Posts"

        GlobalScope.launch(Dispatchers.Main) {
            itemView.categoryImageView.cancelIntroAnim()
            itemView.categoryImageView.isLoaderVisible = !item.isAnimated
            itemView.categoryImageView.load(item.data.image).await()
            if (item.isAnimated)
                return@launch
            itemView.categoryImageView.introAnimate()
            item.isAnimated = true
        }
    }

    override fun onItemClick(item: AnimatedWrapper<Category>) {
        clickBody(item.data)
    }

    fun onItemClick(clickBody: Category.() -> Unit) {
        this.clickBody = clickBody
    }
}