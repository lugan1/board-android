package com.example.navigation

import kotlinx.serialization.Serializable

@Serializable
data object AuthGraph

sealed interface AuthRoute {
    // 다음과 같이 Serializable 어노테이션을 추가하여 직렬화해 navigation() 함수로 보내면 자동으로 path variable로 변환된다.
    // 수작업으로 path variable을 조합하는거보다 훨씬 편리하다.

    @Serializable
    data object Splash: AuthRoute

    @Serializable
    data object Login: AuthRoute

    @Serializable
    data object Signup: AuthRoute
}