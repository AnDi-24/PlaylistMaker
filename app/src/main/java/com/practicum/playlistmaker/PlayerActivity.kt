package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artist: TextView
    private lateinit var progressTime: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var yearName: TextView
    private lateinit var genreName: TextView
    private lateinit var countryName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        cover = findViewById<ImageView>(R.id.cover)
        trackName= findViewById<TextView>(R.id.track_name)
        artist = findViewById<TextView>(R.id.artist_name)
        progressTime = findViewById<TextView>(R.id.progress)
        duration = findViewById<TextView>(R.id.duration_time)
        albumName = findViewById<TextView>(R.id.album_name)
        yearName = findViewById<TextView>(R.id.year_name)
        genreName = findViewById<TextView>(R.id.genre_name)
        countryName = findViewById<TextView>(R.id.country_name)
        val backArrow = findViewById<ImageButton>(R.id.back_button)

        bind(chosenTrack)

        backArrow.setOnClickListener {
            finish()
        }
    }

    fun bind(item: Track){
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val radius = this.resources.getDimensionPixelSize(R.dimen.dp_8)
        Glide.with(this)
            .load(item.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(cover)
        trackName.text = item.trackName
        artist.text = item.artistName
        yearName.text = item.getReleaseYear()
        genreName.text = item.primaryGenreName
        countryName.text = item.country
        duration.text = formatter.format(item.trackTimeMillis.toLong())
        albumName.text = item.collectionName
        progressTime.text = "0:30"
    }
}