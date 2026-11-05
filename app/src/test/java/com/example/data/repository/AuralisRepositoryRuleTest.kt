package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.InspectionRecord
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class AuralisRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createInspection_validPayload_createsDocumentAndReturnsId() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = FirestoreAuralisRepository(firestore, auth)

        val record = InspectionRecord(
            id = "INSP_${UUID.randomUUID().toString().replace("-", "").take(10)}",
            targetType = "Denim Inseam Stitch",
            hasDefect = true,
            defectLabel = "Skipped Stitch",
            banglaDefectLabel = "বাদ পড়া সেলাই",
            confidenceScore = 0.96f,
            severity = "HIGH",
            correctiveAction = "Reroute to Line 3",
            banglaCorrectiveAction = "লাইন ৩-এ স্থানান্তর",
            affectedStation = "Station 2B"
        )

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.createInspection(record) }
        assertTrue(createResult.isSuccess)
        val docId = createResult.getOrThrow()
        assertTrue(docId.isNotEmpty())
    }

    @Test
    fun getUserInspections_authenticatedOwner_returnsMatchingInspections() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = FirestoreAuralisRepository(firestore, auth)
        val record = InspectionRecord(
            id = "INSP_${UUID.randomUUID().toString().replace("-", "").take(10)}",
            targetType = "Cotton Collar",
            hasDefect = false,
            defectLabel = "Verified",
            banglaDefectLabel = "ভেরিফাইড",
            confidenceScore = 0.99f,
            severity = "NORMAL",
            correctiveAction = "Pass",
            banglaCorrectiveAction = "পাস",
            affectedStation = "Station 1A"
        )
        val docId = withTimeout(DEFAULT_TIMEOUT_MS) { repository.createInspection(record).getOrThrow() }

        val listResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getUserInspections() }
        assertTrue(listResult.isSuccess)
        assertTrue(listResult.getOrThrow().any { it.id == docId })
    }

    @Test
    fun getInspectionById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = FirestoreAuralisRepository(firestore, auth)
        val record = InspectionRecord(
            id = "INSP_${UUID.randomUUID().toString().replace("-", "").take(10)}",
            targetType = "Denim Inseam",
            hasDefect = true,
            defectLabel = "Skipped Stitch",
            banglaDefectLabel = "বাদ পড়া সেলাই",
            confidenceScore = 0.95f,
            severity = "HIGH",
            correctiveAction = "Reroute",
            banglaCorrectiveAction = "রি-রুট",
            affectedStation = "Station 2B"
        )
        val docId = withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.createInspection(record).getOrThrow() }

        signInTestUser(BOB_EMAIL)
        val bobRepo = FirestoreAuralisRepository(firestore, auth)
        val bobResult = withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.getInspectionById(docId) }
        assertTrue(bobResult.isFailure)
        val fsEx = bobResult.exceptionOrNull()?.unwrapFirestoreException()
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    @Test
    fun observeInspections_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = FirestoreAuralisRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) { repository.observeInspections().first() }
            fail("Expected PERMISSION_DENIED exception for unauthenticated user")
        } catch (e: Exception) {
            val fsEx = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@auralis.io"
        const val BOB_EMAIL = "bob@auralis.io"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
