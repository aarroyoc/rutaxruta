package eu.adrianistan.config

object Settings {
    val MONGODB_URI: String by lazy { System.getenv("MONGODB_URI").takeUnless { it.isNullOrEmpty() } ?: "mongodb://localhost:27017" }
}