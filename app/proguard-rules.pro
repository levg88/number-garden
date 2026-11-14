# Number Garden collects no data and uses no reflection-heavy libraries.
# kotlinx.serialization generates serializers at compile time; keep them.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Keep generated serializers for bundled content models.
-keepclassmembers class com.numbergarden.app.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.numbergarden.app.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
