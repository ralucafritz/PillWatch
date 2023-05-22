package com.example.pillwatch.data.repository

import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.utils.extensions.Extensions.asDeferredCustom
import com.example.pillwatch.utils.extensions.FirebaseUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber


class UserMedsFirestoreRepository {
    private val userMedsRef = FirebaseUtils.firestoreDb.collection("userMeds")

    companion object {
        const val TAG = "UserMedsFirestore"
    }

    fun addUserMed(userMeds: UserMedsEntity) {
        userMedsRef.add(userMeds)
            .addOnSuccessListener { documentReference ->
                Timber.tag(TAG).d("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Timber.tag(TAG).d("Error adding document $e")
            }
    }

    fun getUserMeds(): Task<QuerySnapshot> {
        return userMedsRef.get()
    }

    fun updateUserMeds(userMedsEntity: UserMedsEntity) {
        val data = mapOf(
            "id" to userMedsEntity.id,
            "tradeName" to userMedsEntity.tradeName,
            "userId" to userMedsEntity.userId,
            "medId" to userMedsEntity.medId,
            "concentration" to userMedsEntity.concentration,
            "isArchived" to userMedsEntity.isArchived
        )

        userMedsRef.whereEqualTo("id", userMedsEntity.id).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userMedsRef.document(document.id).update(data)
                        .addOnSuccessListener {
                            Timber.tag(TAG).d("DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Timber.tag(TAG).d("Error updating document $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Timber.tag(TAG).d("Error getting documents: $e")
            }
    }

    fun deleteUserMeds(userMedId: String) {
        userMedsRef.whereEqualTo("id", userMedId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userMedsRef.document(document.id).delete()
                        .addOnSuccessListener {
                            Timber.tag(TAG).d("DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            Timber.tag(TAG).e("Error deleting document $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Timber.tag(TAG).e("Error getting documents: $e")
            }
    }

    suspend fun getUserMedsByUserId(userId: String): List<UserMedsEntity> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = userMedsRef.whereEqualTo("userId", userId).get().asDeferredCustom().await()
            querySnapshot.documents.mapNotNull { doc -> doc.toObject(UserMedsEntity::class.java) }
        }
    }

    fun cleanUserMeds() {
        userMedsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    userMedsRef.document(document.id).delete()
                }
            } else {
                Timber.tag(AlarmFirestoreRepository.TAG).e(task.exception, "Error getting documents")
            }
        }
    }
}