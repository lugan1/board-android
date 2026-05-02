-keep class com.example.geminiapp.core.network.model.** { *; }

# Keep classes annotated with @Serializable to prevent R8 from stripping serialization logic
-keep @kotlinx.serialization.Serializable class * { *; }
