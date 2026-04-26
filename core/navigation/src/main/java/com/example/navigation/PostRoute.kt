package com.example.navigation

import kotlinx.serialization.Serializable

@Serializable
data object PostGraph

sealed interface PostRoute {
    @Serializable
    data object List: PostRoute

    @Serializable
    data object Create: PostRoute

    @Serializable
    data class Detail(val postId: Long): PostRoute

    @Serializable
    data class Edit(val postId: Long): PostRoute
}