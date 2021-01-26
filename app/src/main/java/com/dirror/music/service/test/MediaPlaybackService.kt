package com.dirror.music.service.test

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

/**
 * MediaPlaybackService
 * @author Moriafly
 * @since 2021年1月25日19:19:58
 */
private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, "DsoMusicServiceTag").apply {

            // Enable callbacks from MediaButtons and TransportControls
//            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
//                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
//            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(MediaSessionCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {

        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
//        return if (allowBrowsing(clientPackageName, clientUid)) {
//            // Returns a root ID that clients can use with onLoadChildren() to retrieve
//            // the content hierarchy.
//            BrowserRoot(MY_MEDIA_ROOT_ID, null)
//        } else {
//            // Clients can connect, but this BrowserRoot is an empty hierachy
//            // so onLoadChildren returns nothing. This disables the ability to browse for content.
//            BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
//        }
        return BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        //  Browsing not allowed
        if (MY_EMPTY_MEDIA_ROOT_ID == parentMediaId) {
            result.sendResult(null)
            return
        }

        // Assume for example that the music catalog is already loaded/cached.

        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>()

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID == parentMediaId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems)
    }

}


