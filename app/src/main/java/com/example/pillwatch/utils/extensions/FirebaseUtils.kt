package com.example.pillwatch.utils.extensions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
    /**
     *
     * Firestore variable that can be used across the app
     *
     */
    val firestoreDb: FirebaseFirestore = Firebase.firestore
}
