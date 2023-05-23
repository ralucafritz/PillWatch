package com.example.pillwatch.data.repository

import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.utils.extensions.Extensions.asDeferredCustom
import com.example.pillwatch.utils.extensions.FirebaseUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AlarmFirestoreRepository {
    private val alarmsRef = FirebaseUtils.firestoreDb.collection("alarms")

    companion object {
        const val TAG = "AlarmFirestore"
    }

    fun addAlarm(alarmEntity: AlarmEntity) {
        alarmsRef.add(alarmEntity)
            .addOnSuccessListener { documentReference ->
                Timber.tag(TAG).d("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Timber.tag(TAG).d("Error adding document $e")
            }
    }

    fun getAlarms(): Task<QuerySnapshot> {
        return alarmsRef.get()
    }

    fun updateAlarm(alarmEntity: AlarmEntity) {
        val data = mapOf(
            "id" to alarmEntity.id,
            "medId" to alarmEntity.medId,
            "timeInMillis" to alarmEntity.timeInMillis,
            "alarmTiming" to alarmEntity.alarmTiming,
            "isEnabled" to alarmEntity.isEnabled,
            "everyXHours" to alarmEntity.everyXHours
        )
        alarmsRef.whereEqualTo("id", alarmEntity.id).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    alarmsRef.document(document.id).update(data)
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

    fun deleteAlarm(medId: String) {
        alarmsRef.whereEqualTo("medId", medId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    alarmsRef.document(document.id).delete()
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

    suspend fun getAlarmsByMedId(medId: String): List<AlarmEntity?> {
        return withContext(Dispatchers.IO) {
            val querySnapshot = alarmsRef.whereEqualTo("medId", medId).get().asDeferredCustom().await()
            querySnapshot.documents.mapNotNull { doc -> doc.toObject(AlarmEntity::class.java) }
        }
    }

    fun cleanAlarms() {
        alarmsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    alarmsRef.document(document.id).delete()
                }
            } else {
                Timber.tag(TAG).e(task.exception, "Error getting documents")
            }
        }
    }
}
