package com.soneso.stellargate.networking.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.soneso.stellargate.domain.data.Country

class GetCountryListResponse {

    @JsonProperty("countries")
    var countries: List<Country> = emptyList()
}