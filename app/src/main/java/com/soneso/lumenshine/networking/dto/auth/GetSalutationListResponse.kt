package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty

class GetSalutationListResponse {

    @JsonProperty("salutations")
    var salutations: List<String> = emptyList()
}