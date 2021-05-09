package eu.adrianistan.config

object Settings {
    val MONGODB_URI: String by lazy { System.getenv("MONGODB_URI").takeUnless { it.isNullOrEmpty() } ?: "mongodb://localhost:27017" }
    val GOOGLE_CLIENT_ID: String by lazy { System.getenv("GOOGLE_CLIENT_ID") }
    val GOOGLE_CLIENT_SECRET: String by lazy { System.getenv("GOOGLE_CLIENT_SECRET") }
}