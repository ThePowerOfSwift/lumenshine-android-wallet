package com.soneso.lumenshine.domain.data

import com.fasterxml.jackson.annotation.JsonProperty

class Country {

    @JsonProperty("code")
    var code = ""

    @JsonProperty("name")
    var name = ""
}