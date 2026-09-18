package com.example.data

import com.example.model.*

/**
 * Standard curriculum repository providing real academic concepts
 * for Physics, Chemistry, Biology, and Mathematics.
 */
object CurriculumRepository {

    val subjects: List<Subject> = listOf(
        Subject(
            id = "sub_physics",
            nameEn = "Physics",
            nameHi = "भौतिक विज्ञान",
            category = "Science",
            chaptersCount = 5,
            conceptsCount = 28,
            iconKey = "bolt",
            accentHex = 0xFF00838F
        ),
        Subject(
            id = "sub_chem",
            nameEn = "Chemistry",
            nameHi = "रसायन विज्ञान",
            category = "Science",
            chaptersCount = 5,
            conceptsCount = 24,
            iconKey = "science",
            accentHex = 0xFF2E7D32
        ),
        Subject(
            id = "sub_math",
            nameEn = "Mathematics",
            nameHi = "गणित",
            category = "Mathematics",
            chaptersCount = 7,
            conceptsCount = 35,
            iconKey = "calculate",
            accentHex = 0xFF1565C0
        ),
        Subject(
            id = "sub_bio",
            nameEn = "Biology",
            nameHi = "जीव विज्ञान",
            category = "Science",
            chaptersCount = 4,
            conceptsCount = 22,
            iconKey = "eco",
            accentHex = 0xFF00695C
        )
    )

    val chapters: List<Chapter> = listOf(
        Chapter(
            id = "ch_elec",
            subjectId = "sub_physics",
            number = 1,
            titleEn = "Electricity & Circuits",
            titleHi = "विद्युत एवं परिपथ",
            description = "Understanding electric charge, current flow, potential difference, and Ohm's law through water pipe analogies.",
            topicsCount = 4
        ),
        Chapter(
            id = "ch_light",
            subjectId = "sub_physics",
            number = 2,
            titleEn = "Light - Reflection & Refraction",
            titleHi = "प्रकाश - परावर्तन तथा अपवर्तन",
            description = "Wavefront intuition, ray diagrams, refractive index, and lens behavior.",
            topicsCount = 5
        ),
        Chapter(
            id = "ch_chem_react",
            subjectId = "sub_chem",
            number = 1,
            titleEn = "Chemical Reactions & Equations",
            titleHi = "रासायनिक अभिक्रियाएँ एवं समीकरण",
            description = "Conservation of mass, bond reorganization, oxidation-reduction fundamentals.",
            topicsCount = 4
        ),
        Chapter(
            id = "ch_trig",
            subjectId = "sub_math",
            number = 1,
            titleEn = "Introduction to Trigonometry",
            titleHi = "त्रिकोणमिति का परिचय",
            description = "Ratios derived from right-angled triangles and unit circles without memorizing tables.",
            topicsCount = 4
        )
    )

    val topics: List<Topic> = listOf(
        Topic(
            id = "top_ohms_law",
            chapterId = "ch_elec",
            titleEn = "Electric Potential & Ohm's Law",
            titleHi = "विद्युत विभव एवं ओम का नियम",
            estimatedMinutes = 15
        ),
        Topic(
            id = "top_resistance",
            chapterId = "ch_elec",
            titleEn = "Resistance in Series & Parallel",
            titleHi = "प्रतिरोधकों का संयोजन",
            estimatedMinutes = 20
        ),
        Topic(
            id = "top_refraction",
            chapterId = "ch_light",
            titleEn = "Snell's Law & Refractive Index",
            titleHi = "स्नेल का नियम एवं अपवर्तनांक",
            estimatedMinutes = 18
        ),
        Topic(
            id = "top_redox",
            chapterId = "ch_chem_react",
            titleEn = "Redox Reactions Intuition",
            titleHi = "ऑक्सीकरण एवं अपचयन",
            estimatedMinutes = 15
        )
    )

    val concepts: List<Concept> = listOf(
        Concept(
            id = "c_ohms_law",
            topicId = "top_ohms_law",
            subjectName = "Physics",
            titleEn = "Ohm's Law: Potential Difference vs Current",
            titleHi = "ओम का नियम: विभव और धारा का संबंध",
            oneLinerIntuition = "Voltage is water pressure; Current is water flow rate; Resistance is pipe constriction.",
            realWorldExample = "Like pushing water through a narrow pipe: higher pressure = more water flowing.",
            difficulty = "Foundation",
            mastery = MasteryLevel.NOT_STARTED
        ),
        Concept(
            id = "c_pot_diff",
            topicId = "top_ohms_law",
            subjectName = "Physics",
            titleEn = "Electric Potential (Work done per charge)",
            titleHi = "विद्युत विभव: प्रति इकाई आवेश पर किया गया कार्य",
            oneLinerIntuition = "Charges do not move on their own; an energy hill (battery) is needed to give them a push.",
            realWorldExample = "A ball rolling down an inclined ramp from high height to ground.",
            difficulty = "Standard",
            mastery = MasteryLevel.NOT_STARTED
        ),
        Concept(
            id = "c_snell",
            topicId = "top_refraction",
            subjectName = "Physics",
            titleEn = "Refraction: Why Light Bends",
            titleHi = "प्रकाश का मुड़ना (अपवर्तन क्यों होता है?)",
            oneLinerIntuition = "Light takes the path of least time; when one side slows down in dense medium, the beam pivots.",
            realWorldExample = "A shopping cart pushing from smooth asphalt into thick mud pivots when one wheel slows.",
            difficulty = "Intermediate",
            mastery = MasteryLevel.NOT_STARTED
        ),
        Concept(
            id = "c_trig_ratio",
            topicId = "top_redox",
            subjectName = "Mathematics",
            titleEn = "Trigonometric Ratios as Invariant Ratios",
            titleHi = "त्रिकोणमितीय अनुपात: त्रिभुज के कोणों का अनुपात",
            oneLinerIntuition = "No matter how big or small the triangle is, if the angle is 30°, the opposite side is always half the hypotenuse.",
            realWorldExample = "Looking up at a mountain peak vs looking up at a flagpole at the same angle.",
            difficulty = "Foundation",
            mastery = MasteryLevel.NOT_STARTED
        )
    )

    val sampleQuiz: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q_ohm_1",
            conceptTitle = "Ohm's Law Intuition",
            questionEn = "If you double the battery voltage across a fixed resistor, what happens to the current?",
            questionHi = "यदि किसी निश्चित प्रतिरोधक पर बैटरी का वोल्टेज दोगुना कर दिया जाए, तो विद्युत धारा पर क्या प्रभाव पड़ेगा?",
            options = listOf(
                "Current doubles (धारा दोगुनी हो जाएगी)",
                "Current halves (धारा आधी हो जाएगी)",
                "Current stays the same (धारा समान रहेगी)",
                "Resistance increases (प्रतिरोध बढ़ जाएगा)"
            ),
            correctIndex = 0,
            intuitionExplanation = "V = I × R. Higher voltage provides double the electrical push, forcing double the charge flow rate through unchanged resistance."
        ),
        QuizQuestion(
            id = "q_light_1",
            conceptTitle = "Refraction Principle",
            questionEn = "Why does light bend towards the normal when entering glass from air?",
            questionHi = "हवा से काँच में प्रवेश करते समय प्रकाश अभिलंब की ओर क्यों मुड़ता है?",
            options = listOf(
                "Light slows down in optical denser glass (काँच में प्रकाश की गति धीमी हो जाती है)",
                "Glass has magnetic attraction (काँच में चुंबकीय आकर्षण होता है)",
                "Light loses its color (प्रकाश का रंग बदल जाता है)",
                "Gravity pulls the photons down (गुरुत्वाकर्षण प्रकाश को खींचता है)"
            ),
            correctIndex = 0,
            intuitionExplanation = "Fermat's principle of least time: Light travels slower in denser medium, so turning closer to the normal minimizes total transit time."
        )
    )

    val commonMistakes: List<MistakeInsight> = listOf(
        MistakeInsight(
            id = "m_1",
            conceptTitle = "Ohm's Law (V = IR)",
            subject = "Physics",
            misconception = "Thinking resistance changes when voltage increases.",
            clarification = "Resistance is an inherent physical property of the conductor (length, area, material, temp), not changed by varying the applied voltage.",
            frequencyTag = "Top Exam Confusion"
        ),
        MistakeInsight(
            id = "m_2",
            conceptTitle = "Electric Current Flow",
            subject = "Physics",
            misconception = "Believing electrons travel from positive terminal to negative.",
            clarification = "Conventional current was historically defined positive to negative, but real electron flow is negative to positive.",
            frequencyTag = "Clarified in NCERT"
        )
    )

    val revisionItems: List<RevisionItem> = listOf(
        RevisionItem(
            id = "rev_1",
            conceptId = "c_ohms_law",
            conceptTitle = "Ohm's Law Intuitive Relation",
            subject = "Physics",
            keyRule = "V = I × R (Current depends on voltage push vs resistive obstruction)",
            dueInDays = 1
        ),
        RevisionItem(
            id = "rev_2",
            conceptId = "c_snell",
            conceptTitle = "Snell's Law of Refraction",
            subject = "Physics",
            keyRule = "n1 * sin(θ1) = n2 * sin(θ2) (Ratio of speeds in optical media)",
            dueInDays = 3
        )
    )

    val sampleStudyPlan: List<StudyPlanItem> = listOf(
        StudyPlanItem(
            id = "sp_1",
            title = "Ohm's Law & Circuit Analogies",
            subject = "Physics",
            targetMinutes = 15,
            isCompleted = false
        ),
        StudyPlanItem(
            id = "sp_2",
            title = "Why Light Bends - Snell's Law",
            subject = "Physics",
            targetMinutes = 20,
            isCompleted = false
        ),
        StudyPlanItem(
            id = "sp_3",
            title = "Redox Reactions - Electron Transfer",
            subject = "Chemistry",
            targetMinutes = 15,
            isCompleted = false
        )
    )
}
