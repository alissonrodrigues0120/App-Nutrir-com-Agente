# ============================================================
# Nutrir com a Gente — ProGuard / R8 rules
# ============================================================

# ── Crashlytics: preserva linha/arquivo para stack traces legíveis ──────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Firestore: modelos usados com reflexão na deserialização ────────────────
# Sem esse keep, o R8 remove campos e o Firestore retorna objetos vazios.
-keep class com.example.nutriragente.data.model.** { *; }

# ── kotlinx.serialization (LmsRepository — parsing do lms_tables.json) ─────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class **$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Hilt / Dagger ───────────────────────────────────────────────────────────
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
}

# ── Firebase (regras complementares às do plugin google-services) ───────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.internal.** { *; }

# ── App Startup ─────────────────────────────────────────────────────────────
-keep class * implements androidx.startup.Initializer { *; }

# ── Kotlin coroutines ────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
