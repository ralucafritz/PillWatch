package com.example.pillwatch.utils.extensions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseUtils {
    /**
     *
     *     FirebaseAuth variable  that can be used across the app
     *
     */
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    /**
     *
     *      FirebaseUser variable that can be used across the app
     *
     */
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
}
