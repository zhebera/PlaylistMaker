package com.example.playlistmaker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    timeDebounce: Long,
    coroutineScope: CoroutineScope,
    useLastparam: Boolean,
    action: (T) -> Unit
): (T) -> Unit {

    var debounceJob: Job? = null
    return { param ->
        if (useLastparam)
            debounceJob?.cancel()

        if (debounceJob?.isCompleted != false || useLastparam) {
            debounceJob = coroutineScope.launch {
                delay(timeDebounce)
                action(param)
            }
        }
    }
}