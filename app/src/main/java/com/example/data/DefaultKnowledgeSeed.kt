package com.example.data

import com.example.model.*

object DefaultKnowledgeSeed {

    // --- SUBJECTS ---
    val subjects = listOf(
        SubjectEntity(
            subjectId = "sub_maths",
            name = "Mathematics (गणित)",
            nameEn = "Mathematics",
            nameHi = "गणित",
            category = "Core Science & Foundation",
            description = "From foundational algebra and equations to advanced calculus and continuous change.",
            iconKey = "calculate",
            order = 1,
            chaptersCount = 1,
            conceptsCount = 6,
            accentHex = 0xFF1976D2
        ),
        SubjectEntity(
            subjectId = "sub_ee",
            name = "Electrical Engineering (विद्युत अभियांत्रिकी)",
            nameEn = "Electrical Engineering",
            nameHi = "विद्युत अभियांत्रिकी",
            category = "Applied Science & Engineering",
            description = "Potential, charge dynamics, Kirchhoff's laws, and comprehensive network theorems.",
            iconKey = "bolt",
            order = 2,
            chaptersCount = 1,
            conceptsCount = 6,
            accentHex = 0xFFE65100
        )
    )

    // --- CHAPTERS ---
    val chapters = listOf(
        // Chapter for Mathematics
        ChapterEntity(
            chapterId = "ch_math_calc",
            subjectId = "sub_maths",
            title = "Foundation to Calculus (बीजगणित से कलन तक)",
            titleEn = "Foundation to Calculus",
            titleHi = "बीजगणित से कलन तक",
            description = "Mastering variable balance, functional mapping, and infinite summation of instantaneous rates.",
            order = 1,
            topicsCount = 3
        ),
        // Chapter for Electrical Engineering
        ChapterEntity(
            chapterId = "ch_ee_circuits",
            subjectId = "sub_ee",
            title = "Circuit Fundamentals & Network Analysis (परिपथ एवं नेटवर्क विश्लेषण)",
            titleEn = "Circuit Fundamentals & Network Analysis",
            titleHi = "परिपथ एवं नेटवर्क विश्लेषण",
            description = "Electrical potential gradients, charge transport, energy balance, and multi-mesh solving.",
            order = 1,
            topicsCount = 3
        )
    )

    // --- TOPICS ---
    val topics = listOf(
        // Math Topics
        TopicEntity(
            topicId = "top_math_algebra",
            chapterId = "ch_math_calc",
            subjectId = "sub_maths",
            title = "Algebraic Foundations & Equations",
            titleEn = "Algebraic Foundations & Equations",
            titleHi = "बीजगणितीय आधार एवं समीकरण संतुलन",
            description = "Abstract variable reasoning, balancing scale invariants, and structural solutions.",
            order = 1,
            estimatedMinutes = 25
        ),
        TopicEntity(
            topicId = "top_math_functions_limits",
            chapterId = "ch_math_calc",
            subjectId = "sub_maths",
            title = "Functions & Limits",
            titleEn = "Functions & Limits",
            titleHi = "फलन एवं सीमाओं की संकल्पना",
            description = "Mathematical machines mapping domains, and exploring what happens when approaching zero.",
            order = 2,
            estimatedMinutes = 30
        ),
        TopicEntity(
            topicId = "top_math_calculus",
            chapterId = "ch_math_calc",
            subjectId = "sub_maths",
            title = "Differential & Integral Calculus",
            titleEn = "Differential & Integral Calculus",
            titleHi = "अवकलन एवं समाकलन की मूल भावना",
            description = "Measuring zoom-in local slopes (differentiation) and cumulative area under curves (integration).",
            order = 3,
            estimatedMinutes = 35
        ),

        // Electrical Engineering Topics
        TopicEntity(
            topicId = "top_ee_charge_dynamics",
            chapterId = "ch_ee_circuits",
            subjectId = "sub_ee",
            title = "Potential, Charge & Resistance",
            titleEn = "Potential, Charge & Resistance",
            titleHi = "विभव, आवेश प्रवाह एवं प्रतिरोध",
            description = "The mechanical pressure of electrical charges and microscopic lattice collisions.",
            order = 1,
            estimatedMinutes = 25
        ),
        TopicEntity(
            topicId = "top_ee_circuit_laws",
            chapterId = "ch_ee_circuits",
            subjectId = "sub_ee",
            title = "Fundamental Circuit Laws (Ohm, KCL & KVL)",
            titleEn = "Fundamental Circuit Laws (Ohm, KCL & KVL)",
            titleHi = "परिपथ के मौलिक नियम (ओम एवं किरचॉफ नियम)",
            description = "Conservation of charge at junctions and conservation of energy around closed loops.",
            order = 2,
            estimatedMinutes = 30
        ),
        TopicEntity(
            topicId = "top_ee_network_analysis",
            chapterId = "ch_ee_circuits",
            subjectId = "sub_ee",
            title = "Network Analysis & Mesh Methods",
            titleEn = "Network Analysis & Mesh Methods",
            titleHi = "नेटवर्क विश्लेषण एवं मेश विधियाँ",
            description = "Systematic matrix solutions for multi-loop, multi-source interconnected networks.",
            order = 3,
            estimatedMinutes = 35
        )
    )

    // --- CONCEPTS ---
    val concepts = listOf(
        // === MATHEMATICS SEQUENCE ===
        // 1. Algebra
        ConceptEntity(
            conceptId = "c_algebra",
            subjectId = "sub_maths",
            chapterId = "ch_math_calc",
            topicId = "top_math_algebra",
            title = "Algebra (बीजगणित: अज्ञात राशियों का तर्क)",
            description = "The language of generalizing arithmetic using symbols to capture mathematical relationships that remain true regardless of specific numbers.",
            difficulty = "Beginner",
            prerequisites = emptyList(),
            order = 1,
            estimatedMinutes = 10,
            whyThisMatters = "बिना बीजगणित के हम हर संख्या के लिए अलग नियम लिखते। Algebra हमें प्रकृति और अर्थव्यवस्था के सार्वभौमिक नियम एक सूत्र में बाँधने की शक्ति देता है।",
            explanationPlaceholder = "Algebra is not about manipulating cryptic letters (x, y); it is about identifying patterns that stay invariant. When you write 2x + 3 = 11, 'x' is just a hidden mystery box whose value maintains perfect numerical balance.",
            examplePlaceholder = "तराजू (Balance Scale): एक पलड़े में 2 समान डिब्बे और 3 रुपये हैं, और दूसरे में 11 रुपये। दोनों ओर से 3 रुपये हटा दो, फिर बचे 8 रुपये को 2 डिब्बों में बाँट दो। प्रत्येक डिब्बे में 4 रुपये निकलेंगे!",
            practice = "यदि 3y - 7 = 14 हो, तो तराजू के संतुलन सिद्धांत का उपयोग करते हुए y का मान ज्ञात करें।",
            understandingCheck = UnderstandingCheckData(
                question = "बीजगणित (Algebra) में चर (Variable 'x') का मूल उद्देश्य क्या है?",
                options = listOf(
                    "केवल कठिन सूत्र बनाने के लिए",
                    "किसी अज्ञात या परिवर्तनशील मान को सामान्य रूप से दर्शाने के लिए",
                    "गणित को जटिल करने के लिए",
                    "केवल ज्यामिति में रेखा खींचने के लिए"
                ),
                correctOptionIndex = 1,
                explanation = "चर (Variable) अज्ञात मात्राओं और सामान्य संबंधों को एक सूत्र में व्यक्त करने का सार्वभौमिक प्रतीक है।"
            ),
            commonMistakes = listOf(
                "3x का अर्थ 3 + x समझ लेना (वास्तव में यह 3 गुणा x है)",
                "चिन्ह बदलते समय दोनों पक्षों में समान संक्रिया न करना"
            ),
            examAnswer = "Step 1: चर (Variable) को पहचानें।\nStep 2: पक्षांतरण करते समय (+ को -, × को ÷) में बदलें।\nStep 3: दोनों पक्षों को संतुलित रखते हुए अज्ञात का मान निकालें।",
            revision = "• Algebra = Generalizing arithmetic.\n• Golden Rule: Whatever operation you apply to the left side, you must apply to the right side."
        ),

        // 2. Equations
        ConceptEntity(
            conceptId = "c_equations",
            subjectId = "sub_maths",
            chapterId = "ch_math_calc",
            topicId = "top_math_algebra",
            title = "Equations (समीकरण: संतुलन और समानता)",
            description = "A mathematical statement asserting that two algebraic expressions represent the exact same quantity.",
            difficulty = "Beginner",
            prerequisites = listOf("c_algebra"),
            order = 2,
            estimatedMinutes = 12,
            whyThisMatters = "समीकरण वह नींव है जिससे वैज्ञानिक गतियों की भविष्यवाणी करते हैं, इंजीनियर पुलों का भार मापते हैं और कंप्यूटर एल्गोरिदम निर्णय लेते हैं।",
            explanationPlaceholder = "An equation is a scale in equilibrium. The equals sign (=) is not a command to compute; it is a declaration of identity between the left hand side and right hand side.",
            examplePlaceholder = "बैंक पासबुक (Bank Balance): Balance = Deposits - Withdrawals. यदि आपको कुल राशि और जमा पता है, तो समीकरण आपको निकासी की सटीक राशि तुरंत बता देता है।",
            practice = "हल करें: 4(x - 2) = 2x + 10. दोनों पक्षों के संतुलन को बनाए रखते हुए x का मान निकालें।",
            understandingCheck = UnderstandingCheckData(
                question = "समीकरण में बराबर (=) चिन्ह का सही दार्शनिक अर्थ क्या है?",
                options = listOf(
                    "यह बताता है कि उत्तर क्या आने वाला है",
                    "यह दोनों पक्षों के बीच परिमाण की पूर्ण समानता और संतुलन दर्शाता है",
                    "यह केवल संख्याओं को अलग करता है",
                    "इसका कोई विशेष अर्थ नहीं है"
                ),
                correctOptionIndex = 1,
                explanation = "समीकरण का '=' चिन्ह यह घोषित करता है कि बायाँ और दायाँ पक्ष एक ही वास्तविक मान के दो अलग-अलग रूप हैं।"
            ),
            commonMistakes = listOf(
                "कोष्ठक (Parentheses) खोलते समय चिन्हों (Signs) का गलत गुणन",
                "चरों को एक तरफ और अचरों को दूसरी तरफ लाते समय पक्षांतरण नियम भूलना"
            ),
            examAnswer = "Step 1: Simplify brackets on both sides.\nStep 2: Group variable terms on LHS and constants on RHS.\nStep 3: Divide by coefficient to isolate unknown.",
            revision = "• Balance Invariant: LHS = RHS.\n• Addition/Subtraction on both sides preserves equality."
        ),

        // 3. Functions
        ConceptEntity(
            conceptId = "c_functions",
            subjectId = "sub_maths",
            chapterId = "ch_math_calc",
            topicId = "top_math_functions_limits",
            title = "Functions (फलन: इनपुट और आउटपुट की मशीन)",
            description = "A relationship where each distinct input from a domain is mapped to exactly one unique output in the codomain.",
            difficulty = "Standard",
            prerequisites = listOf("c_equations"),
            order = 3,
            estimatedMinutes = 14,
            whyThisMatters = "हर कारण का एक प्रभाव होता है। फ़ंक्शन ब्रह्मांड के कारण और प्रभाव (Cause & Effect) का गणितीय स्वरूप है।",
            explanationPlaceholder = "Think of a function f(x) as an automatic vending machine: you drop in a coin or selection (input x), the internal mechanism processes it, and out comes a single snack (output f(x)). A true function never gives two contradictory outputs for the same input.",
            examplePlaceholder = "तापमान और थर्मामीटर (Thermometer): एक ही समय पर एक कमरे का तापमान दो अलग-अलग मान नहीं हो सकता। समय (Time) इनपुट है और तापमान (Temp) आउटपुट f(t) है।",
            practice = "यदि f(x) = x² - 3x + 2 हो, तो f(0), f(2), और f(-1) ज्ञात करें। क्या यह एक वैध फलन है?",
            understandingCheck = UnderstandingCheckData(
                question = "कौन सा गुण किसी संबंध को फलन (Function) बनाता है?",
                options = listOf(
                    "प्रत्येक इनपुट के लिए कम से कम दो आउटपुट होना",
                    "प्रत्येक इनपुट के लिए सटीक एक अद्वितीय आउटपुट होना",
                    "इनपुट और आउटपुट हमेशा ऋणात्मक होना",
                    "ग्राफ हमेशा एक सीधी रेखा होना"
                ),
                correctOptionIndex = 1,
                explanation = "फलन की बुनियादी परिभाषा है: हर मान्य इनपुट के लिए बिल्कुल एक आउटपुट मौजूद होना चाहिए (Vertical Line Test)."
            ),
            commonMistakes = listOf(
                "f(x) को f गुणा x समझना (यह फलन का मान है, गुणनफल नहीं)",
                "वृत्त (Circle) x² + y² = r² को एक एकल फलन मान लेना (यह वर्टिकल लाइन टेस्ट में फेल होता है)"
            ),
            examAnswer = "Domain: All permissible inputs. Range: Resulting outputs. Verify that no single input maps to multiple distinct outputs.",
            revision = "• Function = Deterministic Input-Output Rule.\n• Vertical Line Test: Any vertical line cuts graph at most once."
        ),

        // 4. Limits
        ConceptEntity(
            conceptId = "c_limits",
            subjectId = "sub_maths",
            chapterId = "ch_math_calc",
            topicId = "top_math_functions_limits",
            title = "Limits (सीमा: अनंत सूक्ष्म के करीब पहुँचना)",
            description = "The value that a function approaches as the input gets infinitely close to some point, even if the function is undefined at that exact point.",
            difficulty = "Standard",
            prerequisites = listOf("c_functions"),
            order = 4,
            estimatedMinutes = 15,
            whyThisMatters = "0/0 गणित में अवैध है, लेकिन सीमाओं की मदद से हम 0/0 के ठीक मुहाने पर खड़े होकर उसके भीतर छिपा सच देख सकते हैं। इसी से कैलकुलस का जन्म हुआ।",
            explanationPlaceholder = "A limit asks: 'Where are you heading?' rather than 'Where are you right now?'. Even if a bridge has a missing plank at x = 2, looking from the left and looking from the right tells you exactly where the bridge was intended to meet.",
            examplePlaceholder = "कार का स्पीडोमीटर (Speedometer): तात्कालिक चाल (Instantaneous speed) ज्ञात करने के लिए समय अंतराल Δt को शून्य के इतना करीब लाते हैं कि गति की दिशा स्पष्ट हो जाए।",
            practice = "सीमा निकालें: lim (x -> 3) of (x² - 9)/(x - 3). ध्यान दें कि x=3 रखने पर 0/0 बनता है!",
            understandingCheck = UnderstandingCheckData(
                question = "जब lim (x -> a) f(x) = L लिखा जाता है, तो इसका क्या तात्पर्य है?",
                options = listOf(
                    "x को बिल्कुल a के बराबर होना पड़ेगा",
                    "जैसे-जैसे x मान a के अत्यंत समीप जाता है, f(x) मान L के समीप पहुँचता है",
                    "फलन का मान हमेशा अपरिभाषित होता है",
                    "L हमेशा 0 के बराबर होता है"
                ),
                correctOptionIndex = 1,
                explanation = "सीमा उस प्रवृत्तिमूलक मान (trend value) को पकड़ती है जिसके निकट फलन लगातार खिंचता चला जाता है।"
            ),
            commonMistakes = listOf(
                "0/0 देखकर तुरंत उत्तर शून्य या अनंत घोषित कर देना (यह अनिर्धार्य रूप है, गुणनखंड करना चाहिए)",
                "Left Hand Limit (LHL) और Right Hand Limit (RHL) की समानता जांचे बिना सीमा घोषित करना"
            ),
            examAnswer = "Step 1: Check form (e.g., 0/0). Step 2: Factorize numerator and denominator (x²-9 = (x-3)(x+3)). Step 3: Cancel (x-3) as x ≠ 3. Step 4: Substitute x = 3 to get 6.",
            revision = "• Limit = Target destination as input nears point.\n• Factor out (x - a) singularities before direct evaluation."
        ),

        // 5. Differentiation
        ConceptEntity(
            conceptId = "c_diff",
            subjectId = "sub_maths",
            chapterId = "ch_math_calc",
            topicId = "top_math_calculus",
            title = "Differentiation (अवकलन: तात्कालिक परिवर्तन की दर)",
            description = "Measuring how sensitive a function is to a tiny change in its input; the local slope of the tangent line.",
            difficulty = "Advanced",
            prerequisites = listOf("c_limits"),
            order = 5,
            estimatedMinutes = 16,
            whyThisMatters = "दुनिया में सब कुछ बदल रहा है—शेयर बाजार, रॉकेट की रफ्तार, बीमारी का फैलाव। अवकलन हमें बताता है कि 'इस ठीक पल में' परिवर्तन की दर क्या है।",
            explanationPlaceholder = "Differentiation is zooming in with an infinite microscope on a curve until it looks like a flat straight line. The slope of that straight line at that precise instant is the derivative f'(x) = dy/dx.",
            examplePlaceholder = "दौड़ता हुआ धावक (Sprinting Athlete): औसत गति = 100 मीटर / 10 सेकंड = 10 m/s। लेकिन 4वें सेकंड के ठीक बीच में उसकी रफ्तार 12 m/s थी—यह अवकलन है!",
            practice = "प्रथम सिद्धांत (First Principles) से f(x) = x² का अवकलन निकालें: lim (h->0) [(x+h)² - x²]/h.",
            understandingCheck = UnderstandingCheckData(
                question = "ज्यामितीय दृष्टि से किसी बिंदु पर dy/dx क्या दर्शाता है?",
                options = listOf(
                    "वक्र के नीचे का कुल क्षेत्रफल",
                    "उस बिंदु पर स्पर्श रेखा (Tangent line) का ढाल (Slope)",
                    "मूल बिंदु से दूरी",
                    "वक्र का कुल परिमाप"
                ),
                correctOptionIndex = 1,
                explanation = "dy/dx उस बिंदु पर वक्र को छूकर निकलने वाली स्पर्श रेखा (Tangent) की प्रवणता या ढाल (Slope) होती है।"
            ),
            commonMistakes = listOf(
                "dy/dx को d गुना y भाग d गुना x समझना (यह एक अवकलज संकारक है)",
                "चेन रूल (Chain rule) में आंतरिक फलन का अवकलन करना भूल जाना"
            ),
            examAnswer = "Definition: dy/dx = lim(h->0) [f(x+h) - f(x)] / h. For power rule: d/dx(x^n) = n * x^(n-1). Always show step-by-step limits.",
            revision = "• Derivative = Instantaneous rate of change = Tangent slope.\n• Power Rule: d/dx(x^n) = n * x^(n-1)."
        ),

        // 6. Integration
        ConceptEntity(
            conceptId = "c_integration",
            subjectId = "sub_maths",
            chapterId = "ch_math_calc",
            topicId = "top_math_calculus",
            title = "Integration (समाकलन: अनंत सूक्ष्म टुकड़ों का संचय)",
            description = "The inverse operation of differentiation; accumulating continuous quantities to find total area, volume, or accumulated growth.",
            difficulty = "Advanced",
            prerequisites = listOf("c_diff"),
            order = 6,
            estimatedMinutes = 18,
            whyThisMatters = "यदि अवकलन कांच को सूक्ष्म कणों में तोड़ना है, तो समाकलन उन सभी कणों को वापस जोड़कर पूरी खिड़की तैयार करना है। यह क्षेत्रफल और कुल ऊर्जा की गणना करता है।",
            explanationPlaceholder = "Integration adds up an infinite number of infinitely thin rectangles under a curved graph. The elongated S symbol (∫) represents 'Sum'. The Fundamental Theorem of Calculus proves that accumulation is the exact reverse of taking slopes!",
            examplePlaceholder = "पानी की टंकी (Filling Tank): यदि नल से पानी अलग-अलग गति (Flow rate r(t)) से गिर रहा हो, तो कुल भरा पानी ∫ r(t) dt द्वारा प्राप्त होता है।",
            practice = "मूल्यांकन करें: ∫ (3x² + 2x) dx. अनिश्चित समाकलन में स्थिरांक C जोड़ना न भूलें।",
            understandingCheck = UnderstandingCheckData(
                question = "कैलकुलस का मौलिक प्रमेय (Fundamental Theorem of Calculus) किन दो संक्रियाओं को एक-दूसरे का विलोम सिद्ध करता है?",
                options = listOf(
                    "जोड़ और घटाना",
                    "अवकलन (Differentiation) और समाकलन (Integration)",
                    "गुणा और भाग",
                    "त्रिकोणमिति और लघुगणक"
                ),
                correctOptionIndex = 1,
                explanation = "मौलिक प्रमेय सिद्ध करता है कि संचय (समाकलन) और ढाल (अवकलन) गणितीय रूप से एक-दूसरे के विपरीत हैं।"
            ),
            commonMistakes = listOf(
                "अनिश्चित समाकलन (Indefinite Integral) में + C लगाना भूल जाना",
                "क्षेत्रफल निकालते समय यदि वक्र x-अक्ष के नीचे हो, तो ऋणात्मक चिन्ह का ध्यान न रखना"
            ),
            examAnswer = "Step 1: Apply power rule for integration: ∫ x^n dx = (x^(n+1))/(n+1) + C. Step 2: Integrate term by term. Step 3: Add integration constant C.",
            revision = "• Integration = Continuous accumulation of area.\n• Fundamental Theorem: ∫ f'(x) dx = f(x) + C."
        ),

        // === ELECTRICAL ENGINEERING SEQUENCE ===
        // 1. Voltage
        ConceptEntity(
            conceptId = "c_voltage",
            subjectId = "sub_ee",
            chapterId = "ch_ee_circuits",
            topicId = "top_ee_charge_dynamics",
            title = "Voltage (विद्युत विभव: विद्युत दबाव और ऊर्जा)",
            description = "Electric potential difference; the work needed per unit charge to move a test charge between two points.",
            difficulty = "Beginner",
            prerequisites = emptyList(),
            order = 1,
            estimatedMinutes = 10,
            whyThisMatters = "जैसे ऊंचाई के बिना पानी नीचे नहीं बह सकता, वैसे ही वोल्टेज के बिना तार में करंट कभी नहीं दौड़ सकता। यह विद्युत दुनिया का मूल 'प्रेशर' है।",
            explanationPlaceholder = "Voltage is electrical potential energy per coulomb (V = W / Q). One volt means each coulomb of charge carries one joule of usable energy from the battery into the circuit.",
            examplePlaceholder = "पानी की टंकी की ऊँचाई (Water Tower): टंकी जितनी ऊँची होगी, नीचे नल में पानी का दबाव उतना ही तीव्र होगा। बैटरी का वोल्टेज इस टंकी की ऊँचाई की तरह है।",
            practice = "यदि 2 कूलॉम आवेश को बिंदु A से B तक ले जाने में 24 जूल कार्य करना पड़े, तो विभवांतर कितना होगा?",
            understandingCheck = UnderstandingCheckData(
                question = "1 वोल्ट (1 Volt) की सटीक भौतिक परिभाषा क्या है?",
                options = listOf(
                    "1 एम्पीयर प्रति सेकंड",
                    "1 जूल कार्य प्रति 1 कूलॉम आवेश (1 Joule / 1 Coulomb)",
                    "तार में 100 इलेक्ट्रॉन का प्रवाह",
                    "1 न्यूटन प्रति मीटर"
                ),
                correctOptionIndex = 1,
                explanation = "V = W / Q; 1 वोल्ट वह विभवांतर है जिसमें 1 कूलॉम आवेश को स्थानांतरित करने में 1 जूल कार्य संपन्न होता है।"
            ),
            commonMistakes = listOf(
                "वोल्टेज को तार में 'बहने वाली' चीज समझना (करंट बहता है, वोल्टेज दो बिंदुओं के बीच का 'दबाव अंतर' होता है)",
                "समानांतर क्रम में वोल्टेज को विभाजित कर देना"
            ),
            examAnswer = "Formula: V = W / Q. Unit: Volt (V) = Joule/Coulomb. State clearly that voltage is always measured 'across' two nodes, never 'through' a wire.",
            revision = "• Voltage = Electrical Pressure = Work done per unit charge (V = W / Q).\n• Measured across two terminals."
        ),

        // 2. Current
        ConceptEntity(
            conceptId = "c_current",
            subjectId = "sub_ee",
            chapterId = "ch_ee_circuits",
            topicId = "top_ee_charge_dynamics",
            title = "Current (विद्युत धारा: आवेश प्रवाह की दर)",
            description = "The rate at which electric charge flows past a given cross-section of a conductor per unit time (I = dQ/dt).",
            difficulty = "Beginner",
            prerequisites = listOf("c_voltage"),
            order = 2,
            estimatedMinutes = 11,
            whyThisMatters = "विद्युत धारा ही बल्ब को रोशनी देती है, मोटर को घुमाती है और फोन को चार्ज करती है। यह चलती हुई ऊर्जा का प्रत्यक्ष रूप है।",
            explanationPlaceholder = "Current is the traffic count of electrons moving through a cross-section of wire per second (I = Q / t). 1 Ampere means 6.24 × 10¹⁸ electrons pass through every single second.",
            examplePlaceholder = "नदी का जल प्रवाह (River Flow Rate): प्रति सेकंड कितने लीटर पानी पुल के नीचे से गुजरा। पानी के कण इलेक्ट्रॉन हैं और बहाव की दर करंट है।",
            practice = "यदि किसी बल्ब के तंतु से 10 सेकंड में 30 कूलॉम आवेश प्रवाहित होता है, तो परिपथ में धारा का मान ज्ञात करें।",
            understandingCheck = UnderstandingCheckData(
                question = "परंपरागत धारा (Conventional Current) की दिशा इलेक्ट्रॉनों के प्रवाह के सापेक्ष क्या मानी जाती है?",
                options = listOf(
                    "समान दिशा में",
                    "इलेक्ट्रॉनों के प्रवाह की ठीक विपरीत दिशा में (+ से - की ओर)",
                    "लंबवत दिशा में",
                    "इसकी कोई निश्चित दिशा नहीं होती"
                ),
                correctOptionIndex = 1,
                explanation = "इलेक्ट्रॉन ऋणात्मक होते हैं और - से + की ओर गति करते हैं, जबकि ऐतिहासिक परंपरा अनुसार करंट + से - की ओर माना जाता है।"
            ),
            commonMistakes = listOf(
                "यह सोचना कि करंट तार में खर्च हो जाता है (जितना करंट अंदर जाता है उतना ही बाहर आता है; केवल ऊर्जा खर्च होती है)",
                "अमीटर (Ammeter) को समानांतर क्रम में जोड़ देना (अमीटर हमेशा श्रेणीक्रम में लगता है)"
            ),
            examAnswer = "Definition: I = dQ/dt. Unit: Ampere (A) = Coulomb / second. Measured with an ammeter in series.",
            revision = "• Current = Rate of charge flow (I = Q/t).\n• 1 Ampere = 1 Coulomb/second."
        ),

        // 3. Resistance
        ConceptEntity(
            conceptId = "c_resistance",
            subjectId = "sub_ee",
            chapterId = "ch_ee_circuits",
            topicId = "top_ee_charge_dynamics",
            title = "Resistance (प्रतिरोध: आवेश गति में अवरोध)",
            description = "A measure of the opposition that a material offers to the flow of electric current, converting electrical energy into heat.",
            difficulty = "Beginner",
            prerequisites = listOf("c_voltage", "c_current"),
            order = 3,
            estimatedMinutes = 12,
            whyThisMatters = "प्रतिरोध के बिना करंट अनियंत्रित होकर तारों को पिघला देगा। यही हीटर में ऊष्मा और बल्ब में रोशनी पैदा करता है।",
            explanationPlaceholder = "As electrons drift through a conductor under voltage, they collide with vibrating atoms of the metal lattice. These collisions impede the flow—this atomic friction is electrical resistance (R = ρL / A).",
            examplePlaceholder = "भीड़भाड़ वाला बाजार (Crowded Market): पतली और भीड़भरी संकरी गली (लम्बा व पतला तार) में चलने में ज्यादा रुकावट होगी, जबकि चौड़े राजमार्ग (मोटा तार) पर चलना आसान होगा।",
            practice = "एक तार की लंबाई दोगुनी और अनुप्रस्थ काट का क्षेत्रफल आधा कर दिया जाए, तो नया प्रतिरोध मूल प्रतिरोध का कितने गुना होगा?",
            understandingCheck = UnderstandingCheckData(
                question = "तार का प्रतिरोध किन भौतिक कारकों पर निर्भर करता है?",
                options = listOf(
                    "केवल बैटरी के रंग पर",
                    "लंबाई, अनुप्रस्थ काट क्षेत्रफल, पदार्थ की प्रकृति और तापमान पर (R = ρL/A)",
                    "केवल स्विच की स्थिति पर",
                    "तार के बाहर की हवा पर"
                ),
                correctOptionIndex = 1,
                explanation = "R = ρ * (L / A). लंबाई बढ़ने पर प्रतिरोध बढ़ता है, और मोटाई बढ़ने पर प्रतिरोध घटता है।"
            ),
            commonMistakes = listOf(
                "प्रतिरोध (R) और प्रतिरोधकता (Resistivity ρ) को एक ही समझना (प्रतिरोधकता पदार्थ का आंतरिक गुण है)",
                "यह भूलना कि तापमान बढ़ने पर शुद्ध धातुओं का प्रतिरोध बढ़ जाता है"
            ),
            examAnswer = "Law of Resistance: R = ρL/A. State proportionality to length and inverse proportionality to area. Unit: Ohm (Ω).",
            revision = "• Resistance = Collision friction against charge flow.\n• R = ρ * (Length / Area). Unit: Ohm (Ω)."
        ),

        // 4. Ohm’s Law
        ConceptEntity(
            conceptId = "c_ohms_law",
            subjectId = "sub_ee",
            chapterId = "ch_ee_circuits",
            topicId = "top_ee_circuit_laws",
            title = "Ohm’s Law (ओम का नियम: V = I × R)",
            description = "The foundational relationship stating that current through a conductor between two points is directly proportional to the voltage across them at constant temperature.",
            difficulty = "Standard",
            prerequisites = listOf("c_voltage", "c_current", "c_resistance"),
            order = 4,
            estimatedMinutes = 14,
            whyThisMatters = "यह विद्युत अभियांत्रिकी का सबसे मौलिक नियम है। हर सर्किट डिज़ाइन की शुरुआत इसी त्रिकोण (V, I, R) से होती है।",
            explanationPlaceholder = "Ohm's Law unites our three fundamental concepts: Voltage pushes, Resistance resists, and Current is the resulting flow rate. If temperature is constant, doubling the voltage doubles the current: V = I * R.",
            examplePlaceholder = "पानी की पाइप और नल का वाल्व: यदि आप नल का वाल्व आधा बंद कर दें (प्रतिरोध R बढ़ाएं), तो पानी का बहाव (करंट I) घट जाएगा, जब तक कि आप टंकी का दबाव (वोल्टेज V) न बढ़ा दें।",
            practice = "एक 220V के हीटर का प्रतिरोध 44Ω है। हीटर द्वारा ली जाने वाली धारा ज्ञात करें।",
            understandingCheck = UnderstandingCheckData(
                question = "ओम का नियम किन परिस्थितियों में पूरी तरह लागू होता है?",
                options = listOf(
                    "किसी भी तापमान और किसी भी अर्धचालक पर",
                    "धात्विक चालकों (Metallic conductors) पर जब तापमान और भौतिक अवस्थाएं स्थिर हों",
                    "केवल डायोड और ट्रांजिस्टर पर",
                    "केवल खाली वैक्यूम में"
                ),
                correctOptionIndex = 1,
                explanation = "ओम का नियम ओमिक चालकों पर लागू होता है जहां V-I ग्राफ एक मूल बिंदु से गुजरने वाली सीधी रेखा होती है।"
            ),
            commonMistakes = listOf(
                "सभी इलेक्ट्रॉनिक उपकरणों (जैसे LED या डायोड) को ओमिक मान लेना (डायोड नॉन-ओमिक होते हैं)",
                "सूत्र परिवर्तन में गलती करना: I = V/R और R = V/I"
            ),
            examAnswer = "Statement: At constant temperature, the current (I) flowing through a conductor is directly proportional to potential difference (V) across its ends: V ∝ I ⇒ V = IR.",
            revision = "• V = I * R, I = V / R, R = V / I.\n• V-I characteristic graph is linear for ohmic devices."
        ),

        // 5. KCL / KVL
        ConceptEntity(
            conceptId = "c_kcl_kvl",
            subjectId = "sub_ee",
            chapterId = "ch_ee_circuits",
            topicId = "top_ee_circuit_laws",
            title = "Kirchhoff’s Laws (KCL & KVL: किरचॉफ के नियम)",
            description = "Kirchhoff’s Current Law (conservation of charge at nodes) and Voltage Law (conservation of energy in closed loops).",
            difficulty = "Advanced",
            prerequisites = listOf("c_ohms_law"),
            order = 5,
            estimatedMinutes = 16,
            whyThisMatters = "जटिल परिपथों में जहां कई बैटरियां और शाखाएं होती हैं, साधारण ओम का नियम अकेला काम नहीं करता। किरचॉफ के नियम पूरे जाल को सुलझाते हैं।",
            explanationPlaceholder = "KCL: No charge can accumulate at a wire junction; sum of currents entering equals sum of currents leaving (Σ I = 0). KVL: Energy is conserved; if you take a walk around any closed circuit loop and return to where you started, the net voltage change is zero (Σ V = 0).",
            examplePlaceholder = "सड़क का चौराहा (Traffic Junction): चौराहे पर जितनी गाड़ियाँ अंदर आती हैं, उतनी ही बाहर निकलेंगी (KCL)। रोलरकोस्टर की सवारी (Rollercoaster loop): जितनी ऊंचाई ऊपर चढ़े, नीचे उतरते हुए कुल गिरावट उतनी ही होगी (KVL)।",
            practice = "एक जंक्शन पर तीन धाराएं 2A, 3A और 4A अंदर आ रही हैं और दो तार i1 और 5A बाहर जा रहे हैं। i1 का मान क्या होगा?",
            understandingCheck = UnderstandingCheckData(
                question = "KCL (किरचॉफ धारा नियम) और KVL (किरचॉफ वोल्टेज नियम) क्रमशः किन भौतिक संरक्षण नियमों पर आधारित हैं?",
                options = listOf(
                    "द्रव्यमान और संवेग",
                    "आवेश संरक्षण (Conservation of Charge) और ऊर्जा संरक्षण (Conservation of Energy)",
                    "बल और त्वरण",
                    "तापमान और एन्ट्रॉपी"
                ),
                correctOptionIndex = 1,
                explanation = "KCL आवेश संरक्षण (junction पर आवेश जमा नहीं हो सकता) और KVL ऊर्जा संरक्षण (closed loop में ऊर्जा शून्य) का प्रत्यक्ष प्रमाण हैं।"
            ),
            commonMistakes = listOf(
                "KVL में लूप की दिशा चुनते समय वोल्टेज ड्रॉप और वोल्टेज राइज़ के चिन्ह (+ / -) में गड़बड़ी",
                "KCL में अंदर आने वाली और बाहर जाने वाली धाराओं के चिन्ह को उल्टा मिला देना"
            ),
            examAnswer = "KCL (Junction Rule): Σ I_in = Σ I_out (Based on charge conservation).\nKVL (Loop Rule): In any closed loop, Σ ΔV = 0 (Based on energy conservation). Write loop equations with clear sign conventions.",
            revision = "• KCL: Σ I = 0 at node (Charge conserved).\n• KVL: Σ V = 0 in closed loop (Energy conserved)."
        ),

        // 6. Network Analysis
        ConceptEntity(
            conceptId = "c_network_analysis",
            subjectId = "sub_ee",
            chapterId = "ch_ee_circuits",
            topicId = "top_ee_network_analysis",
            title = "Network Analysis (नेटवर्क विश्लेषण: मेश एवं नोडल विधियाँ)",
            description = "Systematic multi-loop and multi-node techniques to solve for currents and voltages across complex interconnected circuits.",
            difficulty = "Advanced",
            prerequisites = listOf("c_kcl_kvl"),
            order = 6,
            estimatedMinutes = 18,
            whyThisMatters = "स्मार्टफोन मदरबोर्ड से लेकर राष्ट्रीय पावर ग्रिड तक, अरबों तारों वाले नेटवर्क का विश्लेषण इन्हीं विधियों से कंप्यूटर सिमुलेट करता है।",
            explanationPlaceholder = "Network Analysis converts complex electrical circuits into systems of linear equations. Mesh analysis applies KVL to independent circuit windows, while Nodal analysis applies KCL to reference ground nodes, producing solvable matrix equations [G][V] = [I].",
            examplePlaceholder = "शहर का जल आपूर्ति नेटवर्क (City Water Grid): कई टंकियों और पाइपों का एक दूसरे से जुड़ा जाल, जहाँ हर घर में सटीक दबाव और पानी का प्रवाह सुनिश्चित करने के लिए संयुक्त समीकरण हल किए जाते हैं।",
            practice = "दो मेश वाले परिपथ के लिए मेश समीकरण लिखें जिसमें एक 10V स्रोत और तीन प्रतिरोध R1=2Ω, R2=4Ω, R3=6Ω जुड़े हों।",
            understandingCheck = UnderstandingCheckData(
                question = "मेश विश्लेषण (Mesh Analysis) में मुख्यतः किस मौलिक नियम का उपयोग किया जाता है?",
                options = listOf(
                    "केवल फैराडे का नियम",
                    "किरचॉफ का वोल्टेज नियम (KVL) प्रत्येक स्वतंत्र लूप में",
                    "आर्किमिडीज का सिद्धांत",
                    "गॉस का नियम"
                ),
                correctOptionIndex = 1,
                explanation = "मेश विश्लेषण में प्रत्येक स्वतंत्र लूप में क्लॉकवाइज करंट मानकर KVL समीकरण बनाए जाते हैं।"
            ),
            commonMistakes = listOf(
                "साझा प्रतिरोध (Mutual resistance) में दोनों मेश धाराओं के परस्पर प्रभाव को छोड़ देना",
                "नोडल विश्लेषण में संदर्भ नोड (Ground / Datum node = 0V) सही से न चुनना"
            ),
            examAnswer = "Step 1: Identify independent meshes and assign mesh currents i1, i2.\nStep 2: Apply KVL around each mesh, accounting for shared branch currents.\nStep 3: Solve simultaneous algebraic equations using substitution or matrices.",
            revision = "• Nodal Analysis uses KCL at unknown node voltages.\n• Mesh Analysis uses KVL around loop currents."
        )
    )

    // --- PREREQUISITE RELATIONSHIPS ---
    val prerequisites = listOf(
        // Math Chain: Algebra -> Equations -> Functions -> Limits -> Differentiation -> Integration
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_eq_alg",
            sourceConceptId = "c_algebra",
            targetConceptId = "c_equations",
            sourceTitle = "Algebra",
            targetTitle = "Equations",
            relationshipType = "FOUNDATIONAL",
            reason = "Equations require familiarity with algebraic variables and symbols before balancing two expressions."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_func_eq",
            sourceConceptId = "c_equations",
            targetConceptId = "c_functions",
            sourceTitle = "Equations",
            targetTitle = "Functions",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Functions are equations structured specifically as deterministic mappings y = f(x)."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_lim_func",
            sourceConceptId = "c_functions",
            targetConceptId = "c_limits",
            sourceTitle = "Functions",
            targetTitle = "Limits",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Limits explore how function outputs behave as input approaches a singularity."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_diff_lim",
            sourceConceptId = "c_limits",
            targetConceptId = "c_diff",
            sourceTitle = "Limits",
            targetTitle = "Differentiation",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Derivatives are defined strictly as the limit of the difference quotient as interval h approaches zero."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_int_diff",
            sourceConceptId = "c_diff",
            targetConceptId = "c_integration",
            sourceTitle = "Differentiation",
            targetTitle = "Integration",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Integration is the inverse operation of differentiation as proven by the Fundamental Theorem of Calculus."
        ),

        // EE Chain: Voltage -> Current -> Resistance -> Ohm’s Law -> KCL/KVL -> Network Analysis
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_curr_volt",
            sourceConceptId = "c_voltage",
            targetConceptId = "c_current",
            sourceTitle = "Voltage",
            targetTitle = "Current",
            relationshipType = "FOUNDATIONAL",
            reason = "Current (charge movement) cannot occur without an electric potential difference (voltage) driving it."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_res_curr",
            sourceConceptId = "c_current",
            targetConceptId = "c_resistance",
            sourceTitle = "Current",
            targetTitle = "Resistance",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Resistance is understood as the atomic opposition encountered by moving charge carriers."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_ohm_res",
            sourceConceptId = "c_resistance",
            targetConceptId = "c_ohms_law",
            sourceTitle = "Resistance",
            targetTitle = "Ohm's Law",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Ohm's law directly defines the mathematical ratio between voltage, current, and resistance (V=IR)."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_kcl_ohm",
            sourceConceptId = "c_ohms_law",
            targetConceptId = "c_kcl_kvl",
            sourceTitle = "Ohm's Law",
            targetTitle = "KCL / KVL",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Applying Kirchhoff's loop and nodal laws requires Ohm's law to convert branch currents into branch voltages."
        ),
        ConceptPrerequisiteEntity(
            prerequisiteId = "pre_net_kcl",
            sourceConceptId = "c_kcl_kvl",
            targetConceptId = "c_network_analysis",
            sourceTitle = "KCL / KVL",
            targetTitle = "Network Analysis",
            relationshipType = "DIRECT_PREREQUISITE",
            reason = "Network analysis methods (Mesh & Nodal) are formal systematic matrix implementations of KCL and KVL."
        )
    )
}
