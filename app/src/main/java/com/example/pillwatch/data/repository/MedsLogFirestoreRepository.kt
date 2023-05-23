package com.example.pillwatch.data.repository

import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.utils.extensions.Extensions.asDeferredCustom
import com.example.pillwatch.utils.extensions.FirebaseUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class MedsLogFirestoreRepository {
    private val medsLogRef = FirebaseUtils.firestoreDb.collection("medsLog")

    companion object {
        const val TAG = "MedsLogFirestore"
    }

    fun addMedsLog(medsLog: MedsLogEntity) {
        medsLogRef.add(medsLog)
            .addOnSuccessListener { documentReference ->
                Timber.tag(TAG).d("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Timber.tag(TAG).d("Error adding document $e")
            }
    }

    fun getMedsLogs(): Task<QuerySnapshot> {
        return medsLogRef.get()
    }

    fun updateMedsLog(medsLog: MedsLogEntity) {
        val data = mapOf(
            "id" to medsLog.id,
            "medId" to medsLog.medId,
            "status" to medsLog.status,
            "timestamp" to medsLog.timestamp,
        )

        medsLogRef.whereEqualTo("id", medsLog.id).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    medsLogRef.document(document.id).update(data)
                        .addOnSuccessListener {
                            Timber.tag(UserMedsFirestoreRepository.TAG).d("DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Timber.tag(UserMedsFirestoreRepository.TAG).d("Error updating document $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Timber.tag(UserMedsFirestoreRepository.TAG).d("Error getting documents: $e")
            }
    }

    fun deleteMedsLog(medsLogId: String) {
        medsLogRef.whereEqualTo("id", medsLogId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    medsLogRef.document(document.id).delete()
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

    suspend fun getMedsLogsByMedId(medId: String): List<MedsLogEntity?>{
        return withContext(Dispatchers.IO) {
            val querySnapshot = medsLogRef.whereEqualTo("medId", medId).get().asDeferredCustom().await()
            querySnapshot.documents.mapNotNull { doc -> doc.toObject(MedsLogEntity::class.java) }
        }
    }

    fun cleanMedsLogs() {
        medsLogRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    medsLogRef.document(document.id).delete()
                }
            } else {
                Timber.tag(AlarmFirestoreRepository.TAG).e(task.exception, "Error getting documents")
            }
        }
    }
}