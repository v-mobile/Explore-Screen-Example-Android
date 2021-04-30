package io.dzain.dzain.presentation.screen.explore

import io.dzain.dzain.data.model.clean.poll.AppActivePoll
import io.dzain.dzain.data.model.clean.story.AppUserStory
import io.dzain.dzain.presentation.screen.base.BaseContract
import io.dzain.shared.domain.model.clean.Category

interface ExploreContract {

    interface View : BaseContract.View {

        fun changeTopPollsLoadingStatus(shouldShow: Boolean)

        fun changeTopCreatorsLoadingStatus(shouldShow: Boolean)

        fun changeTopCategoriesLoadingStatus(shouldShow: Boolean)

        fun onTopPollsReady(polls: List<AppActivePoll>)

        fun onTopCreatorsReady(users: List<AppUserStory>)

        fun onTopCategoriesReady(categories: List<Category>)

        fun showError(message: String)
    }

    interface Presenter : BaseContract.Presenter<View> {

        fun refreshContent()

        fun navigateToUserProfile()
    }
}