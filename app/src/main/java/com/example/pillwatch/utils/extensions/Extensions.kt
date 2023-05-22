package com.example.pillwatch.utils.extensions


import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import timber.log.Timber

object Extensions {
    /**
     *
     *      Extension function that enables Timber logs in that activity
     *
     */
    fun timber() {
        Timber.plant(Timber.DebugTree())
    }

    fun <T> Task<T>.asDeferredCustom(): Deferred<T> {
        val deferred = CompletableDeferred<T>()

        this.addOnSuccessListener { result ->
            deferred.complete(result)
        }
        this.addOnFailureListener { exception ->
            deferred.completeExceptionally(exception)
        }

        return deferred
    }

}