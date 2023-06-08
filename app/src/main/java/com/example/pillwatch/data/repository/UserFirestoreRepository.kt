package com.example.pillwatch.data.repository

import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.Role
import com.example.pillwatch.utils.extensions.Extensions.asDeferredCustom
import com.example.pillwatch.utils.extensions.FirebaseUtils.firestoreDb
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserFirestoreRepository {
    private val usersRef = firestoreDb.collection("users")

    companion object{
        const val TAG = "UserFirestore"
    }

    fun addUser(user: UserEntity) {
        usersRef.add(user)
            .addOnSuccessListener { documentReference ->
                Timber.tag(TAG).d( "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Timber.tag(TAG).d( "Error adding document $e")
            }
    }

    fun getUsers(): Task<QuerySnapshot> {
        return usersRef.get()
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            val querySnapshot = usersRef.whereEqualTo("email", email).get().asDeferredCustom().await()
            querySnapshot.documents.firstOrNull()?.toObject(UserEntity::class.java)
        }
    }

    fun updateUser(userEntity: UserEntity) {
        val data = mapOf(
            "id" to userEntity.id,
            "email" to userEntity.email,
            "username" to userEntity.username,
            "idToken" to userEntity.idToken,
            "role" to userEntity.role,
        )
        usersRef.whereEqualTo("id", userEntity.id).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    usersRef.document(document.id).update(data)
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

    fun deleteUser(userId: String) {
        usersRef.whereEqualTo("id", userId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    usersRef.document(document.id).delete()
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

    fun updateUsername(userId: String, username: String) {
        usersRef.whereEqualTo("id", userId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    usersRef.document(document.id).update("username", username)
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

    fun updateRole(userId: String, role: Role) {
        usersRef.whereEqualTo("id", userId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    usersRef.document(document.id).update("role", role)
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

    fun clearUsers() {
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    usersRef.document(document.id).delete()
                }
            } else {
                Timber.tag(TAG).e(task.exception, "Error getting documents")
            }
        }
    }
}
