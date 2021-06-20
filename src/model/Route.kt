package eu.adrianistan.model

data class Route(
    val id: String?,
    val name: String,
    val description: String,
    val points: List<RoutePoint>,
    val userId: String,
    val status: RouteState,
    val tracks: List<TrackInfo>,
) {
    enum class RouteState(val value: String){
        IN_REVIEW("in-review"),
        PUBLISHED("published");
    }
}
