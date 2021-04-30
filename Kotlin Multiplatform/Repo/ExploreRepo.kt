package io.dzain.shared.domain.repo

import io.dzain.shared.data.api.services.CategoryApiService
import io.dzain.shared.data.api.services.ExploreApiService
import io.dzain.shared.data.api.services.PollApiService
import io.dzain.shared.data.mapper.*
import io.dzain.shared.domain.model.clean.Category
import io.dzain.shared.domain.model.clean.poll.ActivePoll
import io.dzain.shared.domain.model.clean.poll.CreatedPoll
import io.dzain.shared.domain.model.clean.poll.poll_details.PollDetails
import io.dzain.shared.domain.model.clean.poll.VotedPoll
import io.dzain.shared.domain.model.clean.poll.poll_details.DetailsFeedBack
import io.dzain.shared.domain.model.clean.story.UserStory
import io.dzain.shared.domain.model.request.DeadlineRequestBody
import io.dzain.shared.domain.model.request.FeedbackRequestBody
import io.dzain.shared.domain.model.request.create_poll.CreatePollRequestData
import io.dzain.shared.domain.model.request.url.*

class ExploreRepo(private val exploreApiService: ExploreApiService,
                  private val categoryApiService: CategoryApiService) {

    @Throws(Throwable::class)
    suspend fun getTopPolls(data: TopPollRequestData): List<ActivePoll> {
        val response = exploreApiService.getTopPolls(data)
        return response.mapNotNull { it.toActivePoll() }
    }

    @Throws(Throwable::class)
    suspend fun getPollsByCategory(data: CategoryTopPollRequestData): List<ActivePoll> {
        val response = exploreApiService.getPollsByCategory(data)
        return response.mapNotNull { it.toActivePoll() }
    }

    @Throws(Throwable::class)
    suspend fun getTopCategories(): List<Category> {
        val response = categoryApiService.getTopCategories()
        return response.map { it.toCategory() }
    }

    @Throws(Throwable::class)
    suspend fun getTopCreators(): List<UserStory> {
        val response = exploreApiService.getTopCreators()
        return response.map { it.toUserStory() }
    }
}