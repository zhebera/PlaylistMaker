package com.example.playlistmaker.utils

import kotlinx.coroutines.*

fun <T> debounce(
    timeDebounce: Long,
    coroutineScope: CoroutineScope,
    useLastparam: Boolean,
    action: (T) -> Unit
): (T) -> Unit{

    var debounceJob: Job? = null
    return{ param ->
        if(useLastparam)
            debounceJob?.cancel()

        if(debounceJob?.isCompleted != false || useLastparam){
            debounceJob = coroutineScope.launch {
                delay(timeDebounce)
                action(param)
            }
        }
    }
}