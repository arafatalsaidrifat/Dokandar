package com.example

import com.example.ai.GeminiAgentEngine
import com.example.payments.DualMarketPaymentEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGeminiAgentMetrologyAnalysis() = runBlocking {
        val inspection = GeminiAgentEngine.analyzeImageMetrology("Denim Inseam", simulatedPreset = 0)
        assertNotNull(inspection.id)
        assertTrue(inspection.hasDefect)
        assertEquals("HIGH", inspection.severity)
        assertTrue(inspection.confidenceScore > 0.9f)
    }

    @Test
    fun testBilingualVoiceCommandInvoiceTool() = runBlocking {
        val voiceAction = GeminiAgentEngine.executeVoiceCommand("মিরপুর দোকানের জন্য ১০০ পিস টি-শার্ট ইনভয়েস তৈরি করো", isBangla = true)
        assertEquals("CREATE_INVOICE", voiceAction.intent)
        assertEquals("EXECUTED", voiceAction.status)
        assertTrue(voiceAction.structuredJson.contains("CreateInvoice"))
    }

    @Test
    fun testScope3SummaryCalculation() {
        val summary = GeminiAgentEngine.generateScope3Summary(activeLines = 4, defectReductionPercent = 34.2)
        assertTrue(summary.contains("Scope-3 Verified ESG Card"))
        assertTrue(summary.contains("Carbon Footprint Abatement"))
    }

    @Test
    fun testDualMarketBkashAgreementBinding() = runBlocking {
        val engine = DualMarketPaymentEngine()
        val agreement = engine.executeAgreement("01723-994120", "12345", "Apex Garments Ltd", 25000.0)
        assertEquals("0011", agreement.mode)
        assertEquals("BOUND", agreement.status)
        assertEquals("THIRTY_DAYS", agreement.recurringCycle)
        assertEquals(25000.0, agreement.amountBdt, 0.01)
    }
}
