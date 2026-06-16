package com.promptarchitect.glasses

/** A saved song "blueprint" the user can review hands-free on the glasses. */
data class SongBlueprint(
    val title: String,
    val genre: String,
    val emotion: String,
    val tempo: String,
    val instruments: String,
    val masterPrompt: String,
)

/** Demo content so the UI is populated without a backend or persistence layer yet. */
object SampleData {
    val blueprints: List<SongBlueprint> = listOf(
        SongBlueprint(
            title = "Midnight Rain",
            genre = "Lo-fi",
            emotion = "Nostalgia",
            tempo = "Slow · 60–80 BPM",
            instruments = "Piano, 808s, vinyl crackle",
            masterPrompt = "Lo-fi hip hop beat recorded on a cassette tape in a dusty attic, " +
                "rainy Tuesday, melancholic piano melody, subtle vinyl crackle.",
        ),
        SongBlueprint(
            title = "Neon Skyline",
            genre = "Synth-pop",
            emotion = "Empowerment",
            tempo = "Upbeat · 118 BPM",
            instruments = "Analog synths, gated drums, bass",
            masterPrompt = "Dreamy synth-pop anthem with bright analog leads, punchy gated-reverb " +
                "drums and a soaring chorus about reinvention.",
        ),
        SongBlueprint(
            title = "Boom-bap Sunrise",
            genre = "Hip-hop",
            emotion = "Focus",
            tempo = "Groovy · 90 BPM",
            instruments = "Drum break, sub bass, jazzy keys",
            masterPrompt = "Gritty 90s boom-bap drum loop, deep sub-bassline locked to the groove, " +
                "jazzy piano melody floating on top.",
        ),
    )
}
