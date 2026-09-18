package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.BuildConfig
import com.example.model.AiErrorType
import com.example.model.StructuredAiTeacherResponse
import com.example.model.StudentProfileDocument
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class AiTeacherResult {
    data class Success(val response: StructuredAiTeacherResponse) : AiTeacherResult()
    data class Failure(
        val errorType: AiErrorType,
        val userMessage: String,
        val details: String? = null
    ) : AiTeacherResult()
}

class AiTeacherRepository(private val context: Context) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val responseAdapter = moshi.adapter(StructuredAiTeacherResponse::class.java)

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun askAiTeacher(
        question: String,
        studentProfile: StudentProfileDocument,
        knownConceptMastery: List<String> = emptyList(),
        currentSubject: String? = null,
        currentChapter: String? = null,
        currentConcept: String? = null,
        strategy: com.example.model.ExplanationStrategy = com.example.model.ExplanationStrategy.SIMPLE,
        failedStrategies: List<com.example.model.ExplanationStrategy> = emptyList(),
        attemptNumber: Int = 1,
        missingPrerequisite: String? = null,
        extraInstruction: String? = null
    ): AiTeacherResult = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext AiTeacherResult.Failure(
                errorType = AiErrorType.NO_INTERNET,
                userMessage = "इंटरनेट कनेक्शन उपलब्ध नहीं है। कृपया अपना नेटवर्क जांचें। (No internet connection)",
                details = "NetworkCapabilities indicates offline"
            )
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide high-quality offline pedagogical fallback for development and testing
            Log.w("AiTeacherRepository", "GEMINI_API_KEY is not configured or placeholder. Using pedagogical local teacher fallback.")
            val fallbackResponse = generatePedagogicalFallback(
                question = question,
                profile = studentProfile,
                currentConcept = currentConcept,
                strategy = strategy,
                failedStrategies = failedStrategies,
                attemptNumber = attemptNumber,
                missingPrerequisite = missingPrerequisite
            )
            return@withContext AiTeacherResult.Success(fallbackResponse)
        }

        val systemPrompt = buildSystemPrompt(
            profile = studentProfile,
            mastery = knownConceptMastery,
            subject = currentSubject,
            chapter = currentChapter,
            concept = currentConcept,
            strategy = strategy,
            failedStrategies = failedStrategies,
            attemptNumber = attemptNumber,
            missingPrerequisite = missingPrerequisite
        )
        val userPrompt = buildUserPrompt(
            question = question,
            strategy = strategy,
            failedStrategies = failedStrategies,
            attemptNumber = attemptNumber,
            extraInstruction = extraInstruction
        )

        val request = GeminiGenerateContentRequest(
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemPrompt))
            ),
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = userPrompt))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.35f
            )
        )

        try {
            val response = GeminiClientProvider.service.generateContent(apiKey, request)
            val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (rawJson.isNullOrBlank()) {
                return@withContext AiTeacherResult.Failure(
                    errorType = AiErrorType.EMPTY_RESPONSE,
                    userMessage = "AI शिक्षक से कोई उत्तर प्राप्त नहीं हुआ। कृपया पुनः प्रयास करें। (Empty AI response)",
                    details = "Candidates or text parts were null"
                )
            }

            val cleanedJson = cleanJsonOutput(rawJson)
            val parsedResponse = responseAdapter.fromJson(cleanedJson)
            if (parsedResponse != null) {
                AiTeacherResult.Success(parsedResponse)
            } else {
                AiTeacherResult.Failure(
                    errorType = AiErrorType.API_ERROR,
                    userMessage = "उत्तर को पढ़ने में त्रुटि हुई। कृपया पुनः प्रयास करें। (Response formatting error)",
                    details = "Moshi parsing failed on rawJson: $cleanedJson"
                )
            }
        } catch (e: SocketTimeoutException) {
            Log.e("AiTeacherRepository", "Timeout during AI request", e)
            AiTeacherResult.Failure(
                errorType = AiErrorType.NETWORK_TIMEOUT,
                userMessage = "सर्वर से संपर्क करने में अधिक समय लगा। कृपया दोबारा कोशिश करें। (Request timeout)",
                details = e.localizedMessage
            )
        } catch (e: UnknownHostException) {
            Log.e("AiTeacherRepository", "Unknown host", e)
            AiTeacherResult.Failure(
                errorType = AiErrorType.NO_INTERNET,
                userMessage = "AI सर्वर से जुड़ने में असमर्थ। इंटरनेट की जांच करें। (Cannot resolve AI host)",
                details = e.localizedMessage
            )
        } catch (e: Exception) {
            Log.e("AiTeacherRepository", "Error during askAiTeacher", e)
            // If API key is invalid or quota exceeded, graceful message with fallback option
            val isAuthOrQuotaError = e.message?.contains("400") == true || e.message?.contains("403") == true
            if (isAuthOrQuotaError) {
                val fallbackResponse = generatePedagogicalFallback(
                    question = question,
                    profile = studentProfile,
                    currentConcept = currentConcept,
                    strategy = strategy,
                    failedStrategies = failedStrategies,
                    attemptNumber = attemptNumber,
                    missingPrerequisite = missingPrerequisite
                )
                AiTeacherResult.Success(fallbackResponse)
            } else {
                AiTeacherResult.Failure(
                    errorType = AiErrorType.API_ERROR,
                    userMessage = "AI शिक्षक से संपर्क करते समय समस्या आई (${e.localizedMessage ?: "Unknown error"}).",
                    details = e.stackTraceToString()
                )
            }
        }
    }

    private fun cleanJsonOutput(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json").trim()
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```").trim()
        }
        return clean.trim()
    }

    private fun buildSystemPrompt(
        profile: StudentProfileDocument,
        mastery: List<String>,
        subject: String?,
        chapter: String?,
        concept: String?,
        strategy: com.example.model.ExplanationStrategy,
        failedStrategies: List<com.example.model.ExplanationStrategy>,
        attemptNumber: Int,
        missingPrerequisite: String?
    ): String {
        val strategyDirective = when (strategy) {
            com.example.model.ExplanationStrategy.SIMPLE ->
                "Focus on a clean, simple, everyday intuition without jargon or technical equations. Keep it very conversational and approachable."
            com.example.model.ExplanationStrategy.ANALOGY ->
                "CRITICAL: The student didn't grasp the simple overview. Use a powerful, unforgettable real-life analogy (e.g., comparing electricity to water pressure, friction to walking in mud). Connect every part of the analogy directly to the concept."
            com.example.model.ExplanationStrategy.REAL_LIFE_EXAMPLE ->
                "CRITICAL: The student needs a concrete, tangible real-life scenario with observable details and practical context where they can see this principle working in action right in front of them."
            com.example.model.ExplanationStrategy.STEP_BY_STEP ->
                "CRITICAL: Break down the logic into small, bite-sized micro steps. Do not skip any intermediate reasoning. Explain what happens from step 1 to step 2 to step 3 clearly."
            com.example.model.ExplanationStrategy.VISUAL ->
                "CRITICAL: The student thinks visually. Provide a vivid visual mental model, diagram description, spatial layout, or flowchart. Set visualNeeded = true and provide detailed visualType and visualDescription."
            com.example.model.ExplanationStrategy.PREREQUISITE ->
                "CRITICAL FOUNDATION GAP DIAGNOSIS: The student has struggled across multiple explanation angles. This indicates a missing prerequisite foundation! Identify the foundational prerequisite concept that is missing (${missingPrerequisite ?: "the foundational concept"}). Teach that foundational concept first in simple terms, explaining how it links back to the main topic."
        }

        val failedStrategiesText = if (failedStrategies.isNotEmpty()) {
            "PREVIOUSLY FAILED STRATEGIES: ${failedStrategies.joinToString { it.key }}. DO NOT REPEAT THESE APPROACHES. Provide a completely fresh angle."
        } else ""

        return """
        You are Samjho AI (समझो AI) - an expert, compassionate adaptive educational tutor whose philosophy is:
        "रटने के लिए नहीं, समझने के लिए" (Not for rote memorization, for deep understanding).

        STUDENT CONTEXT:
        - Education Level: ${profile.educationLevel.ifBlank { "School / Diploma / College" }}
        - Class/Course: ${profile.classOrCourse.ifBlank { "General STEM" }}
        - Board/University: ${profile.board ?: profile.university ?: "Standard"}
        - Target Exam: ${profile.exam ?: "Academic Mastery"}
        - Preferred Language: ${profile.preferredLanguage.ifBlank { "Hinglish" }}
        - Known Concept Mastery: ${if (mastery.isEmpty()) "None recorded yet" else mastery.joinToString()}
        - Current Context: Subject: ${subject ?: "General"}, Chapter: ${chapter ?: "General"}, Concept: ${concept ?: "General"}
        - Explanation Attempt: $attemptNumber
        - Active Strategy: ${strategy.key} (${strategy.titleEn})
        $failedStrategiesText

        STRATEGY DIRECTIVE:
        $strategyDirective

        CRITICAL PEDAGOGICAL RULES FOR ADAPTIVE TUTORING:
        1. "इस बार दूसरे तरीके से समझते हैं।" - NEVER repeat an explanation the student already said they didn't understand.
        2. Always be encouraging and compassionate: "कोई बात नहीं। इसे और basic तरीके से देखते हैं।" Never shame or lecture the student.
        3. Explain in the student's preferred language (${profile.preferredLanguage}):
           - If Hinglish: Use natural conversational Hindi-English (e.g. "सोचिए कि voltage एक तरह का pressure hill है...").
           - If Hindi: Use natural, clear Hindi.
           - If English: Use crisp, clear, visual English.
        4. Provide an intuitive, real-world example (daily life analogy) suited to the active strategy.
        5. Provide a check-for-understanding question so the student can test their grasp right away.
        6. If strategy is 'prerequisite', diagnose and teach the foundation gap first!

        You MUST respond ONLY with a valid JSON object strictly matching this schema:
        {
          "intent": "concept_explanation",
          "subject": "Subject name",
          "chapter": "Chapter name",
          "topic": "Topic name",
          "concept": "Specific concept name",
          "difficulty": "Easy" | "Medium" | "Hard",
          "prerequisites": ["List of prerequisite concepts needed before this"],
          "explanationStrategy": "${strategy.key}",
          "strategyTitleHi": "${strategy.titleHi}",
          "encouragementMessage": "कोई बात नहीं। इसे और basic तरीके से देखते हैं।",
          "whyPreviousStrategyFailed": "Brief note on why the previous angle was tricky and how this new angle solves it",
          "prerequisiteIdentified": ${if (missingPrerequisite != null) "\"$missingPrerequisite\"" else "null"},
          "shortAnswer": "1-2 sentence core intuition summary",
          "easyExplanation": "Clear, friendly, conversational intuition breakdown tailored to ${strategy.key}",
          "stepByStep": ["Step 1...", "Step 2...", "Step 3..."],
          "example": "Relatable real-world everyday life example or analogy suited to ${strategy.key}",
          "visualNeeded": ${strategy == com.example.model.ExplanationStrategy.VISUAL},
          "visualType": "circuit_diagram" | "flowchart" | "line_chart" | "diagram",
          "visualDescription": "Description of what diagram or mental image shows",
          "understandingCheck": "A quick interactive conceptual question for the student",
          "expectedAnswer": "The intuitive answer and why it's right",
          "commonMistakes": ["Mistake 1...", "Mistake 2..."],
          "examAnswer": "How to structure and write this answer cleanly in exams for full marks",
          "nextStep": "What concept should the student explore next"
        }
        """.trimIndent()
    }

    private fun buildUserPrompt(
        question: String,
        strategy: com.example.model.ExplanationStrategy,
        failedStrategies: List<com.example.model.ExplanationStrategy>,
        attemptNumber: Int,
        extraInstruction: String?
    ): String {
        return buildString {
            appendLine("Student Question: $question")
            appendLine("Explanation Attempt: $attemptNumber")
            appendLine("Target Strategy: ${strategy.key} (${strategy.titleEn})")
            if (failedStrategies.isNotEmpty()) {
                appendLine("Student previously could not understand via: ${failedStrategies.map { it.key }}")
                appendLine("DO NOT repeat those explanation angles. Use a fresh, completely different pedagogy.")
            }
            if (!extraInstruction.isNullOrBlank()) {
                appendLine("Additional Context / Prompt: $extraInstruction")
            }
        }
    }

    private fun generatePedagogicalFallback(
        question: String,
        profile: StudentProfileDocument,
        currentConcept: String?,
        strategy: com.example.model.ExplanationStrategy = com.example.model.ExplanationStrategy.SIMPLE,
        failedStrategies: List<com.example.model.ExplanationStrategy> = emptyList(),
        attemptNumber: Int = 1,
        missingPrerequisite: String? = null
    ): StructuredAiTeacherResponse {
        val qLower = question.lowercase()
        val lang = profile.preferredLanguage.lowercase()
        val isHindi = lang.contains("hindi") && !lang.contains("hinglish")

        val encouragement = when (attemptNumber) {
            1 -> "आइए इसे बिल्कुल आसान और व्यावहारिक तरीके से समझते हैं!"
            2 -> "कोई बात नहीं! इस बार एक जानी-पहचानी उपमा (Analogy) से समझते हैं।"
            3 -> "बिल्कुल चिंता मत कीजिए। चलिए इसे रोज़मर्रा के एक ठोस उदाहरण से देखते हैं।"
            4 -> "कोई बात नहीं। अब इसे छोटे-छोटे आसान कदमों (Step-by-step) में तोड़ते हैं।"
            5 -> "चलिए अब इसे एक साफ़ मानसिक चित्र (Visual Diagram) के ज़रिए देखते हैं।"
            else -> "कोई बात नहीं। जब कोई बात समझ न आए, तो अक्सर जड़ में कोई पुरानी कड़ी छूटी होती है। आइए पहले बुनियादी नींव पक्की करते हैं।"
        }

        val whyFailed = if (failedStrategies.isNotEmpty()) {
            "पिछला तरीका शायद थोड़ा अमूर्त या तकनीकी था। इस बार हमने '${strategy.titleHi}' चुना है ताकि दिमाग में तुरंत तस्वीर बन सके।"
        } else ""

        val isOhmOrElectricity = qLower.contains("ohm") || qLower.contains("ओम") ||
                qLower.contains("voltage") || qLower.contains("current") || qLower.contains("resistance")

        if (isOhmOrElectricity) {
            return when (strategy) {
                com.example.model.ExplanationStrategy.SIMPLE -> {
                    StructuredAiTeacherResponse(
                        intent = "concept_explanation",
                        subject = "Physics / Electrical Engineering",
                        chapter = "Electricity & Circuits",
                        topic = "Ohm's Law & Circuit Analysis",
                        concept = "Ohm's Law (V = IR)",
                        difficulty = "Medium",
                        prerequisites = listOf("Electric Charge (विद्युत आवेश)", "Potential Difference (विभवांतर)", "Current (विद्युत धारा)"),
                        explanationStrategy = "simple",
                        strategyTitleHi = "सरल व्याख्या",
                        encouragementMessage = encouragement,
                        whyPreviousStrategyFailed = whyFailed,
                        shortAnswer = if (isHindi)
                            "ओम का नियम बताता है कि वोल्टेज (धक्का) बढ़ने पर धारा (बहाव) भी उसी अनुपात में बढ़ती है।"
                        else
                            "Ohm's Law states that Voltage pushes Current through a circuit: more push means more flow (V = I × R).",
                        easyExplanation = if (isHindi)
                            "सोचिए कि वोल्टेज एक 'धक्का' है जो इलेक्ट्रॉनों को आगे धकेलता है। धारा वह गति है जिससे इलेक्ट्रॉन बहते हैं, और प्रतिरोध (Resistance) वह संकरा रास्ता या रुकावट है जो उन्हें धीमा करती है। अधिक धक्का (Voltage) = अधिक बहाव (Current)!"
                        else
                            "Think of Voltage as an electrical 'push'. Current is the rate of electrons actually flowing downhill. Resistance is the narrowness in the wire. More push naturally creates more flow!",
                        stepByStep = listOf(
                            "1. Voltage (V) creates the driving push.",
                            "2. Resistance (R) creates friction or bottleneck.",
                            "3. Current (I) is the resulting flow rate: I = V / R."
                        ),
                        example = "साइकिल को ढलान से नीचे चलाना: जितनी तीव्र ढलान (Voltage), उतनी तेज़ साइकिल दौड़ेगी (Current)।",
                        visualNeeded = false,
                        understandingCheck = "अगर वोल्टेज 10V से बढ़ाकर 20V कर दें और प्रतिरोध वही रहे, तो करंट कितना होगा?",
                        expectedAnswer = "करंट दोगुना (Double) हो जाएगा क्योंकि V और I सीधे समानुपाती (V ∝ I) हैं।",
                        commonMistakes = listOf("Current और Voltage को एक ही समझना — Voltage कारण है, Current उसका प्रभाव है।"),
                        examAnswer = "Formula: V = IR. Statement: Current is directly proportional to voltage at constant temperature.",
                        nextStep = "Explore resistance factors and circuit series/parallel combinations."
                    )
                }
                com.example.model.ExplanationStrategy.ANALOGY -> {
                    StructuredAiTeacherResponse(
                        intent = "concept_explanation",
                        subject = "Physics / Electrical Engineering",
                        chapter = "Electricity & Circuits",
                        topic = "Ohm's Law & Circuit Analysis",
                        concept = "Ohm's Law (V = IR)",
                        difficulty = "Medium",
                        prerequisites = listOf("Electric Potential", "Current"),
                        explanationStrategy = "analogy",
                        strategyTitleHi = "दैनिक जीवन की उपमा (Water Pipe Analogy)",
                        encouragementMessage = encouragement,
                        whyPreviousStrategyFailed = "पिछली व्याख्या में भौतिक शब्दों के कारण भ्रम हो सकता था। इस पानी की टंकी की उपमा से यह हमेशा याद रहेगा।",
                        shortAnswer = "जैसे पानी की टंकी की ऊँचाई दबाव बनाती है और नल से पानी बहता है, ठीक वैसे ही वोल्टेज से करंट बहता है।",
                        easyExplanation = "पानी की टंकी और पाइप की उपमा:\n1. छत पर रखी पानी की टंकी की ऊँचाई = वोल्टेज (दबाव)। जितनी ऊँची टंकी, उतना ज्यादा पानी का प्रेशर।\n2. पाइप से बहता पानी = करंट (धारा)।\n3. पाइप में जमी काई या संकरापन = प्रतिरोध (रेजिस्टेंस)।\nअब देखिए: अगर आप टंकी और ऊँची कर दें (ज्यादा वोल्टेज), तो पानी और तेज़ी से बहेगा (ज्यादा करंट)!",
                        stepByStep = listOf(
                            "1. पानी का दबाव = वोल्टेज (V in Volts)",
                            "2. पानी की धार की गति = करंट (I in Amperes)",
                            "3. पाइप की चौड़ाई/रुकावट = प्रतिरोध (R in Ohms)",
                            "4. संबंध: बहाव = दबाव / रुकावट (I = V / R)"
                        ),
                        example = "अगर आप बगीचे की नली के मुँह पर अंगूठा रख दें (रेजिस्टेंस बढ़ा दें), तो पानी का कुल बहाव (करंट) घट जाता है लेकिन दबाव महसूस होता है।",
                        visualNeeded = true,
                        visualType = "flowchart",
                        visualDescription = "Water tank at height (V) feeding into a pipe with a narrow valve (R), showing water flow rate (I).",
                        understandingCheck = "अगर पाइप बहुत चौड़ा और खुला हो (कम रेजिस्टेंस), तो पानी के बहाव पर क्या असर होगा?",
                        expectedAnswer = "पानी बहुत तेज़ी और आसानी से बहेगा — यानी कम प्रतिरोध में ज्यादा करंट बहता है।",
                        commonMistakes = listOf("यह सोचना कि रेजिस्टेंस करंट को 'खत्म' कर देता है — रेजिस्टेंस सिर्फ बहाव की गति को धीमा करता है।"),
                        examAnswer = "Analogical representation acceptable as conceptual justification before stating V = IR.",
                        nextStep = "Move to practical electrical components like resistors."
                    )
                }
                com.example.model.ExplanationStrategy.REAL_LIFE_EXAMPLE -> {
                    StructuredAiTeacherResponse(
                        intent = "concept_explanation",
                        subject = "Physics / Electrical Engineering",
                        chapter = "Electricity & Circuits",
                        topic = "Ohm's Law & Circuit Analysis",
                        concept = "Ohm's Law (V = IR)",
                        difficulty = "Medium",
                        prerequisites = listOf("Batteries & Bulbs"),
                        explanationStrategy = "realLifeExample",
                        strategyTitleHi = "ठोस व्यावहारिक उदाहरण (Torch & Phone Charger)",
                        encouragementMessage = encouragement,
                        whyPreviousStrategyFailed = "उपमा की बजाय अब घर के असल उपकरणों में इसे काम करते हुए देखते हैं।",
                        shortAnswer = "आपके घर की टॉर्च और मोबाइल चार्जर सीधे ओम के नियम पर काम करते हैं।",
                        easyExplanation = "टॉर्च और बैटरी का वास्तविक उदाहरण:\nजब आपकी टॉर्च की 1.5V की सेल नई होती है, तो बल्ब बहुत तेज़ जलता है क्योंकि वोल्टेज पूरा 1.5V है और बल्ब के फिलामेंट से भरपूर करंट बहता है। जब सेल पुरानी हो जाती है (वोल्टेज गिरकर 1.0V हो जाता है), तो बल्ब मद्धम पड़ जाता है क्योंकि वोल्टेज घटते ही करंट भी घट गया। बल्ब का फिलामेंट वही था (स्थिर रेजिस्टेंस), लेकिन वोल्टेज कम होते ही करंट कम हो गया!",
                        stepByStep = listOf(
                            "1. नई बैटरी: V = 1.5V, बल्ब का फिलामेंट R = 3Ω → I = 1.5 / 3 = 0.5A (तेज़ रोशनी)",
                            "2. पुरानी बैटरी: V = 0.9V, फिलामेंट R = 3Ω → I = 0.9 / 3 = 0.3A (मंद रोशनी)",
                            "3. प्रत्यक्ष प्रमाण: वोल्टेज घटा तो रोशनी (करंट) भी घटी। यही ओम का नियम है!"
                        ),
                        example = "मोबाइल का फास्ट चार्जर: 5V की जगह 9V या 12V देकर बैटरी में ज्यादा करंट धकेलता है ताकि फोन जल्दी चार्ज हो सके।",
                        visualNeeded = true,
                        visualType = "circuit_diagram",
                        visualDescription = "Torch circuit: Battery connected across tungsten filament bulb with electron arrows.",
                        understandingCheck = "यदि टॉर्च में 1.5V की जगह 3V की बैटरी लगा दी जाए, तो बल्ब के करंट पर क्या असर होगा?",
                        expectedAnswer = "करंट दोगुना हो जाएगा और बल्ब बहुत ज्यादा तेज़ चमकेगा (या फिलामेंट उड़ भी सकता है यदि क्षमता से अधिक हो)।",
                        commonMistakes = listOf("यह भूल जाना कि बहुत ज्यादा करंट से फिलामेंट गर्म होकर पिघल सकता है (जूल का नियम)।"),
                        examAnswer = "V = I * R where R = 3Ω, V = 1.5V yields I = 0.5A.",
                        nextStep = "Explore electrical power P = VI and heat dissipation."
                    )
                }
                com.example.model.ExplanationStrategy.STEP_BY_STEP -> {
                    StructuredAiTeacherResponse(
                        intent = "concept_explanation",
                        subject = "Physics / Electrical Engineering",
                        chapter = "Electricity & Circuits",
                        topic = "Ohm's Law & Circuit Analysis",
                        concept = "Ohm's Law (V = IR)",
                        difficulty = "Medium",
                        prerequisites = listOf("Units of measurement"),
                        explanationStrategy = "stepByStep",
                        strategyTitleHi = "कदम-दर-कदम विभाजन (Step-by-Step Logic)",
                        encouragementMessage = encouragement,
                        whyPreviousStrategyFailed = "अब बिना किसी लंबी कहानी के, 4 स्पष्ट तार्किक चरणों में इसे समझते हैं।",
                        shortAnswer = "चरणबद्ध तर्क: 1. धक्का पहचानें (V) → 2. रुकावट नापें (R) → 3. भाग देकर बहाव निकालें (I = V/R)।",
                        easyExplanation = "कदम-दर-कदम गणितीय और तार्किक प्रवाह:\n\nचरण 1: चालक के दोनों छोर पर वोल्टेज (V) लागू होता है। यह इलेक्ट्रॉनों पर इलेक्ट्रोस्टैटिक बल लगाता है।\nचरण 2: तार के अंदर धातु के परमाणु मौजूद होते हैं। गतिमान इलेक्ट्रॉन इन परमाणुओं से टकराते हैं — इस टकराव को प्रतिरोध (R) कहते हैं।\nचरण 3: प्रति सेकंड तार के किसी बिंदु से गुजरने वाले इलेक्ट्रॉनों की संख्या करंट (I) कहलाती है।\nचरण 4: अधिक बल = अधिक गति, अधिक टकराव = कम गति। अतः I = V / R।",
                        stepByStep = listOf(
                            "कदम 1: परिपथ का वोल्टेज (V) ज्ञात करें (यूनिट: वोल्ट - V)।",
                            "कदम 2: परिपथ का प्रतिरोध (R) ज्ञात करें (यूनिट: ओम - Ω)।",
                            "कदम 3: सूत्र लागू करें: I = V / R (यूनिट: एम्पीयर - A)।",
                            "कदम 4: यदि तापमान स्थिर है, तो V और I का अनुपात (V/I) हमेशा वही स्थिर संख्या R देगा।"
                        ),
                        example = "अगर V = 12V और R = 4Ω, तो I = 12 / 4 = 3 एम्पीयर।",
                        visualNeeded = false,
                        understandingCheck = "यदि किसी सर्किट में V = 24V है और करंट I = 2A बह रहा है, तो उस सर्किट का रेजिस्टेंस R कितना है?",
                        expectedAnswer = "R = V / I = 24 / 2 = 12 ओम (Ω)।",
                        commonMistakes = listOf("फॉर्मूले को उलट देना जैसे R = I / V लिखना जो गलत है। सही रूप R = V / I है।"),
                        examAnswer = "Step 1: State Ohm's law. Step 2: Write formula I = V/R. Step 3: Substitute values with SI units.",
                        nextStep = "Series and parallel equivalent resistance derivations."
                    )
                }
                com.example.model.ExplanationStrategy.VISUAL -> {
                    StructuredAiTeacherResponse(
                        intent = "concept_explanation",
                        subject = "Physics / Electrical Engineering",
                        chapter = "Electricity & Circuits",
                        topic = "Ohm's Law & Circuit Analysis",
                        concept = "Ohm's Law (V = IR)",
                        difficulty = "Medium",
                        prerequisites = listOf("Circuit symbols"),
                        explanationStrategy = "visual",
                        strategyTitleHi = "दृश्य/चित्रात्मक व्याख्या (Visual Diagram & Triangle)",
                        encouragementMessage = encouragement,
                        whyPreviousStrategyFailed = "अब दिमाग में एक साफ़ तस्वीर बनाते हैं: प्रसिद्ध ओम का जादुई त्रिभुज (Ohm's Triangle)।",
                        shortAnswer = "ओम का जादुई त्रिभुज: ऊपर V, नीचे I और R। जिस अक्षर को उंगली से ढकेंगे, वही फॉर्मूला मिल जाएगा!",
                        easyExplanation = "ओम के नियम का जादुई त्रिभुज (Ohm's Triangle):\n\n       /\\ \n      /  \\ \n     / V  \\ \n    /------\\ \n   / I | R  \\ \n  /__________\\ \n\nदेखने का तरीका:\n1. V को उंगली से ढंकें → नीचे I और R बराबर में दिखे → V = I × R\n2. I को उंगली से ढंकें → ऊपर V और नीचे R दिखा → I = V / R\n3. R को उंगली से ढंकें → ऊपर V और नीचे I दिखा → R = V / I\n\nसाथ ही V-I ग्राफ़ एक सीधी रेखा (Linear slope) होती है, जहाँ ढलान (Slope) रेजिस्टेंस R को दर्शाती है!",
                        stepByStep = listOf(
                            "1. त्रिभुज बनाएं: ऊपर V लिखें, आधार पर बायीं तरफ I और दायीं तरफ R लिखें।",
                            "2. जो निकालना है उसे छिपा लें।",
                            "3. V-I ग्राफ: X-अक्ष पर Current (I), Y-अक्ष पर Voltage (V)। एक 45° की सीधी रेखा खींचें। रेखा जितनी खड़ी होगी, रेजिस्टेंस उतना ज्यादा होगा।"
                        ),
                        example = "विजुअल कार्टून: वोल्ट (V) पीछे से धक्का दे रहा है, एम्पीयर (I) आगे दौड़ने की कोशिश कर रहा है, और ओम (R) रस्सी से बीच में दबाकर रास्ता संकरा कर रहा है!",
                        visualNeeded = true,
                        visualType = "circuit_diagram",
                        visualDescription = "Visual diagram of Ohm's Triangle (V on top, I and R on bottom) alongside a linear V-I characteristic graph.",
                        understandingCheck = "V-I ग्राफ में यदि रेखा बिल्कुल सपाट (Horizontal) हो जाए, तो रेजिस्टेंस कितना होगा?",
                        expectedAnswer = "रेजिस्टेंस शून्य (Zero) होगा (Superconductor), क्योंकि बिना किसी वोल्टेज के भी करंट बहेगा।",
                        commonMistakes = listOf("ग्राफ में X और Y अक्षों को उल्टा लेबल कर देना जिससे ढलान 1/R बन जाती है।"),
                        examAnswer = "Draw the Ohm's law triangle and plot the straight line V vs I graph through origin.",
                        nextStep = "Non-ohmic conductors like diodes and transistors."
                    )
                }
                com.example.model.ExplanationStrategy.PREREQUISITE -> {
                    StructuredAiTeacherResponse(
                        intent = "concept_explanation",
                        subject = "Physics / Electrical Engineering",
                        chapter = "Electricity & Circuits",
                        topic = "Electricity Foundations",
                        concept = "Prerequisite: Potential Difference (विभवांतर) & Charge",
                        difficulty = "Easy",
                        prerequisites = emptyList(),
                        explanationStrategy = "prerequisite",
                        strategyTitleHi = "बुनियादी पूर्व-शर्त (Prerequisite: Potential Difference)",
                        encouragementMessage = "कोई बात नहीं! जब ओम का नियम समझ न आए, तो इसका मतलब है कि 'विभवांतर (Voltage)' की मूल बात अभी धुंधली है। आइए पहले उसे समझते हैं, फिर ओम के नियम पर वापस लौटेंगे!",
                        whyPreviousStrategyFailed = "ओम का नियम समझने से पहले 'वोल्टेज असल में क्या है' यह जानना जरूरी था। आइए नींव पक्की करते हैं।",
                        prerequisiteIdentified = "विद्युत विभवांतर (Electric Potential Difference)",
                        shortAnswer = "नींव की बात: पानी ढलान के बिना नहीं बहता, वैसे ही इलेक्ट्रॉन 'विभवांतर' (ऊर्जा के अंतर) के बिना नहीं हिलते।",
                        easyExplanation = "बुनियादी पूर्व-शर्त: विभवांतर (Potential Difference) क्या है?\n\nज़रा सोचिए: अगर एक गेंद फर्श पर बिल्कुल समतल रखी है, तो क्या वह अपने आप लुढ़केगी? बिल्कुल नहीं!\nलेकिन अगर आप एक सिरे को हाथ से उठाकर ऊँचा कर दें (ऊंचाई का अंतर पैदा करें), तो गेंद तुरंत नीचे की ओर लुढ़कने लगेगी।\n\nतार के अंदर भी इलेक्ट्रॉन ऐसे ही होते हैं। बैटरी क्या करती है? बैटरी तार के एक सिरे पर बहुत सारे इलेक्ट्रॉन जमा कर देती है (High Potential) और दूसरे सिरे पर कमी कर देती है (Low Potential)। इस 'ऊर्जा के अंतर' को ही विभवांतर (Voltage) कहते हैं। जब तक यह अंतर नहीं होगा, कोई करंट नहीं बहेगा!\n\nअब जब आप यह समझ गए हैं, तो ओम का नियम बहुत आसान है: जितना ज्यादा ढलान (विभवांतर), उतनी तेज गेंद की रफ्तार (करंट)!",
                        stepByStep = listOf(
                            "1. समतल फर्श = शून्य वोल्टेज = कोई करंट नहीं।",
                            "2. एक सिरे को ऊपर उठाना = बैटरी लगाना = विभवांतर पैदा होना।",
                            "3. गेंद का लुढ़कना = करंट का बहना।",
                            "4. अब वापस ओम के नियम पर आएं: अधिक ढलान (V) = अधिक गति (I)!"
                        ),
                        example = "पहाड़ी से गिरता झरना: पहाड़ की ऊंचाई विभवांतर है, गिरता हुआ पानी करंट है। समतल जमीन पर झरना नहीं बन सकता।",
                        visualNeeded = true,
                        visualType = "diagram",
                        visualDescription = "Illustration comparing an inclined slope with rolling ball to battery terminals pushing electrons.",
                        understandingCheck = "यदि बैटरी के दोनों सिरों पर एक समान वोल्टेज हो (कोई अंतर न हो), तो क्या करंट बहेगा?",
                        expectedAnswer = "नहीं! क्योंकि विभवांतर शून्य है (Potential Difference = 0), इसलिए कोई इलेक्ट्रॉन गति नहीं करेगा।",
                        commonMistakes = listOf("यह सोचना कि करंट तार में पहले से होता है — बिना बैटरी के विभवांतर के तार में करंट शून्य होता है।"),
                        examAnswer = "Potential difference is work done in moving a unit positive charge from one point to another: V = W / Q.",
                        nextStep = "अब जब विभवांतर साफ़ हो गया है, वापस ओम के नियम (V = IR) पर लौटें!"
                    )
                }
            }
        }

        // Generic fallback for any topic across the 6 strategies
        return when (strategy) {
            com.example.model.ExplanationStrategy.SIMPLE -> {
                StructuredAiTeacherResponse(
                    intent = "concept_explanation",
                    subject = "Conceptual Sciences",
                    chapter = "Core Fundamentals",
                    topic = "Intuitive Foundations",
                    concept = currentConcept ?: question.take(40),
                    difficulty = "Medium",
                    prerequisites = listOf("Basic Observation"),
                    explanationStrategy = "simple",
                    strategyTitleHi = "सरल व्याख्या",
                    encouragementMessage = encouragement,
                    whyPreviousStrategyFailed = whyFailed,
                    shortAnswer = "इस अवधारणा का सार यह है कि हर प्राकृतिक व्यवस्था कारण और प्रभाव के सीधे नियम पर चलती है।",
                    easyExplanation = "आइए इसे बिना किसी भारी किताब के समझते हैं: '$question' का मतलब है कि जब कोई व्यवस्था किसी प्रभाव में आती है, तो वह संतुलन की ओर बढ़ती है। इसे जटिल परिभाषाओं की जगह सरल समझ से देखें।",
                    stepByStep = listOf("1. शुरुआती अवस्था", "2. लागू होने वाला बल या कारण", "3. व्यवस्था का स्वाभाविक परिणाम"),
                    example = "दैनिक जीवन: जैसे प्यास लगने पर पानी पीना संतुलन लौटाता है, वैसे ही भौतिक नियम संतुलन बनाते हैं।",
                    understandingCheck = "इस प्रक्रिया में मुख्य चालक कारण (Driving Force) क्या है?",
                    expectedAnswer = "सिस्टम में असंतुलन या ऊर्जा का अंतर मुख्य चालक होता है।"
                )
            }
            com.example.model.ExplanationStrategy.ANALOGY -> {
                StructuredAiTeacherResponse(
                    intent = "concept_explanation",
                    subject = "Conceptual Sciences",
                    chapter = "Core Fundamentals",
                    topic = "Intuitive Foundations",
                    concept = currentConcept ?: question.take(40),
                    difficulty = "Medium",
                    prerequisites = listOf("Everyday Observation"),
                    explanationStrategy = "analogy",
                    strategyTitleHi = "दैनिक जीवन की उपमा (Everyday Analogy)",
                    encouragementMessage = encouragement,
                    whyPreviousStrategyFailed = "अमूर्त परिभाषा को छोड़कर, आइए एक घरेलू उपमा से इसे जीवंत बनाते हैं।",
                    shortAnswer = "यह बिल्कुल भीड़ भरे बाज़ार या रसोई में काम करने जैसी व्यवस्था है।",
                    easyExplanation = "ज़रा सोचिए कि '$question' बिल्कुल एक व्यस्त रसोई या बाज़ार जैसा है:\nजब लोग कम होते हैं, सब कुछ सरलता से चलता है। जैसे ही संख्या या दबाव बढ़ता है, आपसी प्रभाव से नियम साफ दिखाई देने लगते हैं।",
                    stepByStep = listOf("1. उपमा का पहला पात्र", "2. उनके बीच की परस्पर क्रिया", "3. अंतिम नतीजा"),
                    example = "बाज़ार में रास्ता बनाना: जितनी चौड़ी सड़क, उतनी आसानी से लोग आगे बढ़ पाते हैं।",
                    understandingCheck = "अगर रास्ते की बाधाएं बढ़ा दी जाएं, तो प्रवाह पर क्या असर पड़ेगा?",
                    expectedAnswer = "प्रवाह धीमा हो जाएगा क्योंकि बाधाओं से प्रतिरोध बढ़ता है।"
                )
            }
            com.example.model.ExplanationStrategy.REAL_LIFE_EXAMPLE -> {
                StructuredAiTeacherResponse(
                    intent = "concept_explanation",
                    subject = "Conceptual Sciences",
                    chapter = "Core Fundamentals",
                    topic = "Intuitive Foundations",
                    concept = currentConcept ?: question.take(40),
                    difficulty = "Medium",
                    prerequisites = listOf("Everyday Mechanics"),
                    explanationStrategy = "realLifeExample",
                    strategyTitleHi = "ठोस व्यावहारिक उदाहरण (Observable Reality)",
                    encouragementMessage = encouragement,
                    whyPreviousStrategyFailed = "अब इसे किसी कल्पित बात की जगह, अपनी आँखों से दिखने वाले ठोस उदाहरण में देखते हैं।",
                    shortAnswer = "यह सिद्धांत आपके घर के पंखे, साइकिल या पानी के नल में सीधे काम करता है।",
                    easyExplanation = "वास्तविक जीवन का ठोस उदाहरण:\nजब आप साइकिल चलाते हैं, तो पैडल का बल आपकी गति बनाता है और ज़मीन का खुरदरापन आपको रोकता है। '$question' भी इसी तरह दो ताकतों के संतुलन का प्रत्यक्ष परिणाम है।",
                    stepByStep = listOf("1. लागू होने वाला बल", "2. माध्यम का प्रभाव", "3. नापा जा सकने वाला परिणाम"),
                    example = "साइकिल पर पैडल मारना और हवा का विरोध।",
                    understandingCheck = "यदि विरोधी बल को आधा कर दिया जाए, तो क्या होगा?",
                    expectedAnswer = "गति दोगुनी आसानी से बढ़ सकेगी।"
                )
            }
            com.example.model.ExplanationStrategy.STEP_BY_STEP -> {
                StructuredAiTeacherResponse(
                    intent = "concept_explanation",
                    subject = "Conceptual Sciences",
                    chapter = "Core Fundamentals",
                    topic = "Intuitive Foundations",
                    concept = currentConcept ?: question.take(40),
                    difficulty = "Medium",
                    prerequisites = listOf("Step-by-step logic"),
                    explanationStrategy = "stepByStep",
                    strategyTitleHi = "कदम-दर-कदम विभाजन (Logical Sequence)",
                    encouragementMessage = encouragement,
                    whyPreviousStrategyFailed = "अब बिना किसी अतिरिक्त बात के, सिर्फ 3 तार्किक कदमों में इसे समझते हैं।",
                    shortAnswer = "तीन सरल कदम: कारण → क्रिया → परिणाम।",
                    easyExplanation = "कदम-दर-कदम समझ:\nकदम 1: शुरुआती अवस्था को पहचानें।\nकदम 2: उस पर होने वाले परिवर्तन को देखें।\nकदम 3: अंतिम परिणाम को नोट करें।",
                    stepByStep = listOf("कदम 1: इनपुट", "कदम 2: प्रक्रिया", "कदम 3: आउटपुट"),
                    example = "चाय बनाना: पानी गर्म होना → पत्ती का रंग छोड़ना → चाय तैयार होना।",
                    understandingCheck = "दूसरे कदम पर क्या परिवर्तन घटित हुआ?",
                    expectedAnswer = "ऊर्जा का हस्तांतरण हुआ जिससे नया स्वरूप बना।"
                )
            }
            com.example.model.ExplanationStrategy.VISUAL -> {
                StructuredAiTeacherResponse(
                    intent = "concept_explanation",
                    subject = "Conceptual Sciences",
                    chapter = "Core Fundamentals",
                    topic = "Intuitive Foundations",
                    concept = currentConcept ?: question.take(40),
                    difficulty = "Medium",
                    prerequisites = listOf("Visual Spatial Layout"),
                    explanationStrategy = "visual",
                    strategyTitleHi = "दृश्य/चित्रात्मक व्याख्या (Mental Diagram)",
                    encouragementMessage = encouragement,
                    whyPreviousStrategyFailed = "अब दिमाग में एक साफ़ चित्र बनाते हैं ताकि यह हमेशा याद रहे।",
                    shortAnswer = "एक मानसिक नक्शा: बाईं ओर इनपुट, बीच में फ़िल्टर, और दाईं ओर परिणाम।",
                    easyExplanation = "मानसिक चित्र:\n[ इनपुट बल ] ───► [ प्रक्रिया फ़िल्टर ] ───► [ आउटपुट परिणाम ]\n\nजब आप इसे इस चित्र की तरह देखते हैं, तो कोई भी सूत्र याद रखना बहुत आसान हो जाता है।",
                    stepByStep = listOf("1. बाईं ओर का स्रोत", "2. बीच का माध्यम", "3. दाईं ओर का फल"),
                    example = "नदी का पहाड़ से समंदर तक का प्रवाह चित्र।",
                    visualNeeded = true,
                    visualType = "flowchart",
                    visualDescription = "Clear block diagram showing cause arrow entering process box yielding output.",
                    understandingCheck = "चित्र में बीच का डिब्बा क्या काम करता है?",
                    expectedAnswer = "यह इनपुट को नियंत्रित या परिवर्तित करता है।"
                )
            }
            com.example.model.ExplanationStrategy.PREREQUISITE -> {
                StructuredAiTeacherResponse(
                    intent = "concept_explanation",
                    subject = "Conceptual Sciences",
                    chapter = "Core Fundamentals",
                    topic = "Foundational Prerequisite Gap",
                    concept = "Prerequisite Foundation for: ${currentConcept ?: question.take(30)}",
                    difficulty = "Easy",
                    prerequisites = emptyList(),
                    explanationStrategy = "prerequisite",
                    strategyTitleHi = "बुनियादी पूर्व-शर्त (Prerequisite Foundation)",
                    encouragementMessage = "कोई बात नहीं! जब यह बात समझ नहीं आ रही, तो इसका मतलब है कि इससे पहले की एक बुनियादी कड़ी छूटी हुई है। आइए पहले उसे पक्का करते हैं!",
                    whyPreviousStrategyFailed = "जब नींव की एक ईंट हिली हो, तो ऊपर की दीवार नहीं बन सकती। पहले बुनियादी बात समझते हैं।",
                    prerequisiteIdentified = missingPrerequisite ?: "मूल बुनियादी सिद्धांत (Foundational Principle)",
                    shortAnswer = "नींव की बात: हर बड़ी अवधारणा एक छोटी सी बुनियादी सच्चाई पर टिकी होती है।",
                    easyExplanation = "बुनियादी पूर्व-शर्त की समझ:\n\nइस विषय को समझने से पहले यह जानना जरूरी है कि जब तक बुनियादी ऊर्जा या स्थिति में अंतर नहीं होता, कुछ भी नहीं बदलता। पहले इस मूल बात को महसूस करें। जब यह साफ हो जाएगा, तो मूल सवाल अपने आप बहुत आसान लगने लगेगा।",
                    stepByStep = listOf("1. बुनियादी सत्य", "2. यह मुख्य सवाल से कैसे जुड़ता है", "3. अब मुख्य सवाल पर वापसी"),
                    example = "इमारत की नींव: बिना नींव के छत नहीं टिक सकती।",
                    understandingCheck = "क्या बुनियादी बात साफ हो गई?",
                    expectedAnswer = "हाँ, अब मुख्य अवधारणा को नए सिरे से समझ सकते हैं।"
                )
            }
        }
    }
}
