data class Track(
    val trackId: String,         // Уникальный ID трека
    val trackName: String,       // Название композиции
    val artistName: String,      // Имя исполнителя
    val trackTimeMillis: Long,   // Продолжительность трека
    val artworkUrl100: String    // Ссылка на изображение обложки
)