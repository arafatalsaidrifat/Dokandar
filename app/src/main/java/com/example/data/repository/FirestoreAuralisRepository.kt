package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.InspectionRecord
import com.example.data.model.PaymentAgreement
import com.example.data.model.VoiceAction
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreAuralisRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // CRITICAL: Always initialize with R.string.firestore_database_id
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- 1. INSPECTIONS (/inspections/{id}) ---

    fun observeInspections(): Flow<List<InspectionRecord>> = flow {
        val uid = auth.currentUser?.uid
            ?: throw FirebaseFirestoreException(
                "PERMISSION_DENIED: User must be authenticated",
                FirebaseFirestoreException.Code.PERMISSION_DENIED
            )
        val path = "inspections"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(InspectionRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createInspection(record: InspectionRecord): Result<String> = runCatching {
        val uid = requireUserId()
        val docId = record.id.ifBlank { "INSP_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("inspections").document(docId)
        try {
            docRef.set(record.toFirestoreMap(uid)).await()
            docId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun getUserInspections(): Result<List<InspectionRecord>> = runCatching {
        val uid = requireUserId()
        val path = "inspections"
        try {
            val snap = db.collection(path)
                .whereEqualTo("userId", uid)
                .get()
                .await()
            snap.documents.mapNotNull {
                it.toObject(InspectionRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            throw e
        }
    }

    suspend fun getInspectionById(inspectionId: String): Result<InspectionRecord?> = runCatching {
        val docRef = db.collection("inspections").document(inspectionId)
        try {
            val snap = docRef.get().await()
            snap.toObject(InspectionRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    // --- 2. VOICE ACTIONS (/voice_actions/{id}) ---

    fun observeVoiceActions(): Flow<List<VoiceAction>> = flow {
        val uid = auth.currentUser?.uid
            ?: throw FirebaseFirestoreException(
                "PERMISSION_DENIED: User must be authenticated",
                FirebaseFirestoreException.Code.PERMISSION_DENIED
            )
        val path = "voice_actions"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(VoiceAction::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createVoiceAction(action: VoiceAction): Result<String> = runCatching {
        val uid = requireUserId()
        val docId = action.id.ifBlank { "VA_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("voice_actions").document(docId)
        try {
            docRef.set(action.toFirestoreMap(uid)).await()
            docId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- 3. PAYMENT AGREEMENTS (/payment_agreements/{id}) ---

    fun observePaymentAgreements(): Flow<List<PaymentAgreement>> = flow {
        val uid = auth.currentUser?.uid
            ?: throw FirebaseFirestoreException(
                "PERMISSION_DENIED: User must be authenticated",
                FirebaseFirestoreException.Code.PERMISSION_DENIED
            )
        val path = "payment_agreements"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PaymentAgreement::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun createPaymentAgreement(agreement: PaymentAgreement): Result<String> = runCatching {
        val uid = requireUserId()
        val docId = agreement.agreementId.ifBlank { "AGR_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("payment_agreements").document(docId)
        try {
            docRef.set(agreement.copy(agreementCode = docId).toFirestoreMap(uid)).await()
            docId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }
}
