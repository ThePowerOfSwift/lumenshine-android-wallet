package com.soneso.lumenshine.presentation.general

import com.soneso.lumenshine.domain.data.SgError

class SgViewState<T>(val state: State, val data: T?, val error: SgError?) {

    constructor(error: SgError) : this(State.ERROR, null, error)

    constructor(data: T) : this(State.READY, data, null)

    constructor(state: State) : this(state, null, null)
}

enum class State {
    READY, LOADING, ERROR
}