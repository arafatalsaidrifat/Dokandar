package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.InspectionRecord
import com.example.data.model.PaymentAgreement
import com.example.data.model.ProductionNode
import com.example.data.model.VoiceAction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductionNodeDao {
    @Query("SELECT * FROM production_nodes ORDER BY id ASC")
    fun getAllNodes(): Flow<List<ProductionNode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<ProductionNode>)

    @Update
    suspend fun updateNode(node: ProductionNode)

    @Query("UPDATE production_nodes SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspection_records ORDER BY timestamp DESC LIMIT 50")
    fun getRecentInspections(): Flow<List<InspectionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(record: InspectionRecord)
}

@Dao
interface VoiceActionDao {
    @Query("SELECT * FROM voice_actions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentActions(): Flow<List<VoiceAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: VoiceAction)
}

@Dao
interface PaymentAgreementDao {
    @Query("SELECT * FROM payment_agreements ORDER BY boundTimestamp DESC")
    fun getAllAgreements(): Flow<List<PaymentAgreement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgreement(agreement: PaymentAgreement)

    @Query("DELETE FROM payment_agreements WHERE agreementId = :id")
    suspend fun deleteAgreement(id: String)
}

@Database(
    entities = [
        ProductionNode::class,
        InspectionRecord::class,
        VoiceAction::class,
        PaymentAgreement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productionNodeDao(): ProductionNodeDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun voiceActionDao(): VoiceActionDao
    abstract fun paymentAgreementDao(): PaymentAgreementDao
}
