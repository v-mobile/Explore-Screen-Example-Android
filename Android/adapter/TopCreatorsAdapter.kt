package io.dzain.dzain.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import io.dzain.dzain.R
import io.dzain.dzain.data.model.clean.story.AppUserStory
import io.dzain.dzain.presentation.adapters.base.BaseListAdapter
import io.dzain.dzain.presentation.adapters.base.BaseViewHolder
import io.dzain.dzain.util.color
import io.dzain.dzain.util.log
import io.dzain.dzain.util.onClick
import kotlinx.android.synthetic.main.top_creators_list_item_view.view.*

class TopCreatorsAdapter : BaseListAdapter<AppUserStory>() {

    private var avatarClickBody: AppUserStory.() -> Unit = {}
    private var userNameClickBody: AppUserStory.() -> Unit = {}

    override val layoutResId = R.layout.top_creators_list_item_view

    override fun onBind(itemView: View, item: AppUserStory, position: Int) {
        itemView.storyImageView.load(item.avatar) {
            error(R.drawable.ic_user_profile_avatar)
            placeholder(R.drawable.ic_user_profile_avatar)
        }
        itemView.userNameTextView.text = item.username
        itemView.userPostCount.text = "${item.pollsCount} Polls"
        itemView.storyImageView.setGapPaintColor(color(R.color.color10))
        val hasStory = item.stories.isNotEmpty() && !item.isCurrentUser
        itemView.storyImageView.setHasStory(hasStory)
        itemView.storyImageView.setSeen(item.isAllSeen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        holder.itemView.setOnClickListener(null)
        holder.itemView.storyImageView.onClick {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                avatarClickBody(currentList[adapterPosition])
            }
        }
        val userNameClickBody = View.OnClickListener {
            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                userNameClickBody(currentList[adapterPosition])
            }
        }
        holder.itemView.userNameTextView.setOnClickListener(userNameClickBody)
        holder.itemView.userPostCount.setOnClickListener(userNameClickBody)
        return holder
    }

    fun onAvatarClick(clickBody: AppUserStory.() -> Unit) {
        avatarClickBody = clickBody
    }

    fun onUserNameClickClick(clickBody: AppUserStory.() -> Unit) {
        userNameClickBody = clickBody
    }
}