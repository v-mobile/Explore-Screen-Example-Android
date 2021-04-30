package io.dzain.shared.data.api.services

import io.dzain.shared.data.api.AppResponse
import io.dzain.shared.data.api.getOrThrow
import io.dzain.shared.data.api.manager.ApiManager
import io.dzain.shared.data.mapper.toCreatePollRequestBody
import io.dzain.shared.domain.model.clean.poll.poll_details.DetailsFeedBack
import io.dzain.shared.domain.model.request.DeadlineRequestBody
import io.dzain.shared.domain.model.request.FeedbackRequestBody
import io.dzain.shared.domain.model.request.create_poll.CreatePollRequestData
import io.dzain.shared.domain.model.request.url.*
import io.dzain.shared.domain.model.response.CategoryResponse
import io.dzain.shared.domain.model.response.poll.*
import io.dzain.shared.domain.model.response.story.StoryVoterResponse
import io.dzain.shared.domain.model.response.story.UserStoryResponse
import io.dzain.shared.domain.repo.*
import io.ktor.client.request.*

class ExploreApiService(private val apiManager: ApiManager) {

    private val feedPagingCounter = FeedPagingCounter()
    private val pagingIdHolder = PagingIdHolder()

    suspend fun getTopPolls(pollRequestData: TopPollRequestData): List<ActivePollResponse> {
        val path = RequestPath.Polls.TOP_POLLS
        return feedPagingCounter.withOffset(path, pollRequestData.forceRefresh) {
            apiManager.get<AppResponse<List<ActivePollResponse>>>(path) {
                parameter(key = "limit", value = pollRequestData.limit)
            }.getOrThrow()
        }
    }

    suspend fun getPollsByCategory(pollRequestData: CategoryTopPollRequestData): List<ActivePollResponse> {
        val path = RequestPath.Polls.CATEGORY_TOP_POLLS(pollRequestData.categoryId)
        return pagingIdHolder.withId(path, pollRequestData.forceRefresh) {
            apiManager.get<AppResponse<List<ActivePollResponse>>>(path) {
                parameter(key = "id__lt", value = it)
                parameter(key = "limit", value = pollRequestData.limit)
            }.getOrThrow()
        }
    }

    suspend fun getTopCreators(): List<UserStoryResponse> {
        val path = RequestPath.Polls.TOP_CREATORS
        return apiManager.get<AppResponse<List<UserStoryResponse>>>(path).getOrThrow()
    }
}