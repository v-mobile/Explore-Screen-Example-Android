package io.dzain.dzain.presentation.screen.explore

import io.dzain.dzain.data.BlockCode
import io.dzain.dzain.data.interactor.*
import io.dzain.dzain.data.model.clean.AppNavigationType
import io.dzain.dzain.presentation.manager.NavigationManager
import io.dzain.dzain.presentation.screen.base.BasePresenter
import io.dzain.shared.data.exception.AppError
import kotlinx.coroutines.launch

class ExplorePresenter(private val navigationManager: NavigationManager,
                       private val exploreInteractor: ExploreInteractor,
                       private val storyInteractor: StoryInteractor) : BasePresenter<ExploreContract.View>(), ExploreContract.Presenter {

    override fun onPresenterCreate() {
        launch {
            onView {
                changeTopPollsLoadingStatus(shouldShow = true)
            }
            withBgContext {
                exploreInteractor.fetchTopPolsPreview()
            }
            onView {
                changeTopPollsLoadingStatus(shouldShow = false)
            }
        }

        launch {
            onView {
                changeTopCreatorsLoadingStatus(shouldShow = true)
            }
            withBgContext {
                storyInteractor.fetchTopCreators()
            }
            onView {
                changeTopCreatorsLoadingStatus(shouldShow = false)
            }
        }

        launch {
            onView {
                changeTopCategoriesLoadingStatus(shouldShow = true)
            }
            withBgContext {
                val categories = exploreInteractor.getTopCategories()
                onView {
                    onTopCategoriesReady(categories)
                }
            }
            onView {
                changeTopCategoriesLoadingStatus(shouldShow = false)
            }
        }

        exploreInteractor.topPolsPreviewFlow.collect(scope = this) {
            onView {
                onTopPollsReady(it)
            }
        }

        storyInteractor.topCreatorsFlow.collect(scope = this) {
            onView {
                onTopCreatorsReady(it)
            }
        }
    }

    override fun navigateToUserProfile() {
        navigationManager.executeNavigationType(AppNavigationType.PROFILE)
    }

    override fun refreshContent() {
        launchInBackground {
            val categories = exploreInteractor.getTopCategories()
            onView {
                onTopCategoriesReady(categories)
            }
        }
        launchInBackground {
            storyInteractor.fetchTopCreators()
        }
        launchInBackground {
            exploreInteractor.fetchTopPolsPreview()
        }
    }

    override fun onError(blockCode: BlockCode, appError: AppError) {
        onView {
            showError(appError.message)
        }
    }
}