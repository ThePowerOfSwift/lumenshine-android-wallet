package com.soneso.lumenshine.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.soneso.lumenshine.domain.data.Country

class GetCountryListResponse {

    @JsonProperty("countries")
    var countries: List<Country> = emptyList()
}