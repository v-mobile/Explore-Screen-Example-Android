package io.dzain.dzain.data.interactor

import io.dzain.dzain.data.flow_data.FlowData
import io.dzain.dzain.data.mapper.toAppActivePoll
import io.dzain.dzain.data.mapper.toAppUserPoll
import io.dzain.dzain.data.mapper.toAppUserStory
import io.dzain.dzain.data.model.PagingData
import io.dzain.dzain.data.model.clean.poll.AppActivePoll
import io.dzain.dzain.data.model.clean.poll.AppPoll
import io.dzain.dzain.data.model.clean.poll.AppUserPoll
import io.dzain.dzain.data.model.clean.story.AppUserStory
import io.dzain.dzain.util.convertToString
import io.dzain.dzain.util.log
import io.dzain.shared.data.local.PreferenceKeys
import io.dzain.shared.data.local.Preferences
import io.dzain.shared.domain.model.clean.Category
import io.dzain.shared.domain.model.request.FeedbackRequestBody
import io.dzain.shared.domain.model.request.url.*
import io.dzain.shared.domain.repo.AuthRepo
import io.dzain.shared.domain.repo.ExploreRepo
import io.dzain.shared.domain.repo.PollRepo
import java.util.*

class ExploreInteractor(private val exploreRepo: ExploreRepo,
                        private val authRepo: AuthRepo) {

    private val topPollsPreviewData = mutableListOf<AppActivePoll>()
    private val pollsPagingData = PagingData<AppActivePoll>()

    val topPolsPreviewFlow = FlowData<List<AppActivePoll>>()
    val pollsFlow = FlowData<PagingData<AppActivePoll>>()

    suspend fun fetchTopPolls(forceRefresh: Boolean = true) {
        if (forceRefresh) {
            pollsPagingData.clear()
        }
        val body = TopPollRequestData(limit = 10, forceRefresh = forceRefresh)
        val topPolls = exploreRepo.getTopPolls(body)
        val polls = topPolls.map { it.toAppActivePoll(authRepo.isSignIn()) }

        pollsPagingData.shouldLoad = polls.isNotEmpty()
        pollsPagingData.addAll(polls)
        pollsFlow.postValue(pollsPagingData)
    }

    suspend fun fetchTopPolsPreview() {
        val body = TopPollRequestData(limit = 3, forceRefresh = true)
        val topPolls = exploreRepo.getTopPolls(body)
        val polls = topPolls.map { it.toAppActivePoll(authRepo.isSignIn()) }
        topPollsPreviewData.clear()
        topPollsPreviewData.addAll(polls)
        topPolsPreviewFlow.postValue(polls)
    }

    suspend fun fetchTopPollsByCategory(categoryId: Int, forceRefresh: Boolean = true) {
        if (forceRefresh) {
            pollsPagingData.clear()
        }
        val body = CategoryTopPollRequestData(limit = 10, forceRefresh = forceRefresh, categoryId = categoryId)
        val topPolls = exploreRepo.getPollsByCategory(body)
        val polls = topPolls.map { it.toAppActivePoll(authRepo.isSignIn()) }

        pollsPagingData.shouldLoad = polls.isNotEmpty()
        pollsPagingData.addAll(polls)
        pollsFlow.postValue(pollsPagingData)
    }

    suspend fun getTopCategories(): List<Category> {
        return exploreRepo.getTopCategories()
    }

    suspend fun refreshExploreData(pollId: Int, optionId: Int) {
        if (topPollsPreviewData.any { it.id == pollId }) {
            fetchTopPolsPreview()
        }

        val originalPoll = pollsPagingData.data.find { it.id == pollId } ?: return
        val originalIndex = pollsPagingData.data.indexOf(originalPoll)
        pollsPagingData.data.removeAt(originalIndex)
        pollsFlow.postValue(pollsPagingData)
    }

    fun clear() {
        pollsPagingData.clear()
    }

    fun removePoll(pollId: Int) {
        pollsPagingData.data.removeAll { it.id == pollId }
        val index = pollsPagingData.data.indexOfFirst { it.relevancePosition > 0 }
        if (index >= 0) {
            val appActivePoll = pollsPagingData.data[index]
            pollsPagingData.data[index] = appActivePoll.copy(relevancePosition = appActivePoll.relevancePosition - 1)
        }
        pollsFlow.postValue(pollsPagingData)
    }
}