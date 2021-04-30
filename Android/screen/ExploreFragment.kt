package io.dzain.dzain.presentation.screen.explore

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import io.dzain.dzain.R
import io.dzain.dzain.data.model.clean.AppNavigationType
import io.dzain.dzain.data.model.clean.poll.AppActivePoll
import io.dzain.dzain.data.model.clean.poll.AppOptionType
import io.dzain.dzain.data.model.clean.story.AppUserStory
import io.dzain.dzain.data.model.toAnimatedWrapper
import io.dzain.dzain.databinding.ExploreFragmentBinding
import io.dzain.dzain.presentation.adapters.TopCategoriesAdapter
import io.dzain.dzain.presentation.adapters.TopCreatorsAdapter
import io.dzain.dzain.presentation.adapters.poll.top_poll.TopPollActionListener
import io.dzain.dzain.presentation.adapters.poll.top_poll.TopPollsAdapter
import io.dzain.dzain.presentation.screen.UserProfileBottomSheet
import io.dzain.dzain.presentation.screen.base.BaseFragment
import io.dzain.dzain.presentation.screen.explore.explore_polls.ExplorePollsContainerActivity
import io.dzain.dzain.presentation.screen.home.HomeActivity
import io.dzain.dzain.presentation.screen.poll.dialog.PollDetailDialogFragment
import io.dzain.dzain.presentation.screen.story.view.single_story_container.SingleStoryContainerActivity
import io.dzain.dzain.presentation.widget.dialog.showErrorDialog
import io.dzain.dzain.util.color
import io.dzain.dzain.util.onApplyWindowInsets
import io.dzain.dzain.util.onClick
import io.dzain.dzain.util.view_binding.viewBinding
import io.dzain.shared.domain.model.clean.Category
import io.dzain.shared.domain.model.clean.poll.DirectionType
import io.dzain.shared.domain.model.clean.poll.MetaType

class ExploreFragment : BaseFragment<ExploreContract.View, ExploreContract.Presenter>(),
        ExploreContract.View, TopPollActionListener {

    private val binding: ExploreFragmentBinding by viewBinding()
    private val pollAdapter = TopPollsAdapter(this)
    private val topCreatorsAdapter = TopCreatorsAdapter()
    private val topCategoriesAdapter = TopCategoriesAdapter()

    override val layoutResId = R.layout.explore_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTopPolls()
        setUpTopCreators()
        setUpTopCategories()
        setUpViews()
    }

    override fun onTopPollsReady(polls: List<AppActivePoll>) {
        pollAdapter.submitList(polls)
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onTopCreatorsReady(users: List<AppUserStory>) {
        topCreatorsAdapter.submitList(users)
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onTopCategoriesReady(categories: List<Category>) {
        val hasItems = topCategoriesAdapter.currentList.isNotEmpty()
        val wrappedCategories = categories.toAnimatedWrapper(isAnimated = hasItems)
        topCategoriesAdapter.submitList(wrappedCategories)
        binding.topCategoriesRecyclerView.scrollToPosition(0)
        binding.swipeRefresh.isRefreshing = false
    }

    override fun changeTopPollsLoadingStatus(shouldShow: Boolean) {
        if (shouldShow) {
            binding.topPollsRecyclerView.showShimmerAdapter()
        } else {
            binding.topPollsRecyclerView.hideShimmerAdapter()
        }
    }

    override fun changeTopCreatorsLoadingStatus(shouldShow: Boolean) {
        if (shouldShow) {
            binding.topCreatorsRecyclerView.showShimmerAdapter()
        } else {
            binding.topCreatorsRecyclerView.hideShimmerAdapter()
        }
    }

    override fun changeTopCategoriesLoadingStatus(shouldShow: Boolean) {
        if (shouldShow) {
            binding.topCategoriesRecyclerView.showShimmerAdapter()
        } else {
            binding.topCategoriesRecyclerView.hideShimmerAdapter()
        }
    }

    override fun onPollLongClick(optionType: AppOptionType, directionType: DirectionType, metadata: Map<MetaType, String>) {
        val pollDialogFragment = PollDetailDialogFragment.newInstance(optionType, directionType, metadata)

        requireActivity().supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            addToBackStack(PollDetailDialogFragment::class.java.name)
            add(android.R.id.content, pollDialogFragment)
        }
    }

    override fun onPollClick(pollId: Int) {
        checkAuthentication(AppNavigationType.TOP_POLLS(pollId))
    }

    override fun showError(message: String) {
        showErrorDialog(message)
    }

    private fun setUpTopPolls() {
        binding.topPollsRecyclerView.adapter = pollAdapter
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.topPollsRecyclerView.layoutManager = manager
        binding.topPollsRecyclerView.setHasFixedSize(true)
    }

    private fun setUpTopCreators() {
        binding.topCreatorsRecyclerView.adapter = topCreatorsAdapter
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.topCreatorsRecyclerView.layoutManager = manager
        binding.topCreatorsRecyclerView.setHasFixedSize(true)
        topCreatorsAdapter.onAvatarClick {
            if (stories.isNotEmpty()) {
                if (isCurrentUser) {
                    presenter.navigateToUserProfile()
                } else {
                    SingleStoryContainerActivity.start(requireActivity(), this)
                }
            } else {
                if (isCurrentUser) {
                    presenter.navigateToUserProfile()
                } else {
                    UserProfileBottomSheet.show(id, parentFragmentManager)
                }
            }
        }
        topCreatorsAdapter.onUserNameClickClick {
            if (isCurrentUser) {
                presenter.navigateToUserProfile()
            } else {
                UserProfileBottomSheet.show(id, parentFragmentManager)
            }
        }
    }

    private fun setUpTopCategories() {
        binding.topCategoriesRecyclerView.adapter = topCategoriesAdapter
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.topCategoriesRecyclerView.layoutManager = manager
        binding.topCategoriesRecyclerView.setHasFixedSize(true)
        topCategoriesAdapter.onItemClick {
            checkAuthentication(AppNavigationType.TOP_CATEGORIES(this))
        }
    }

    private fun setUpViews() {
        binding.swipeRefresh.setColorSchemeColors(color(R.color.color3))
        binding.swipeRefresh.setOnRefreshListener {
            presenter.refreshContent()
        }
        binding.showMoreButton.onClick {
            checkAuthentication(AppNavigationType.TOP_POLLS(ExplorePollsContainerActivity.NO_POLL_ID))
        }
        binding.containerView.onApplyWindowInsets {
            val top = getInsets(WindowInsetsCompat.Type.statusBars()).top
            binding.containerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = top
            }
        }
    }


    private fun checkAuthentication(appNavigationType: AppNavigationType) {
        val parent = requireActivity()
        if (parent is HomeActivity) {
            parent.checkAuthentication(appNavigationType)
        }
    }
}

