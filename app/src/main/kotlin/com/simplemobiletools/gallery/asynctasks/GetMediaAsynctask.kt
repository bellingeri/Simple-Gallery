package com.simplemobiletools.gallery.asynctasks

import android.content.Context
import android.os.AsyncTask
import com.simplemobiletools.commons.helpers.SORT_BY_DATE_TAKEN
import com.simplemobiletools.gallery.extensions.config
import com.simplemobiletools.gallery.extensions.getFavoritePaths
import com.simplemobiletools.gallery.helpers.MediaFetcher
import com.simplemobiletools.gallery.helpers.SHOW_ALL
import com.simplemobiletools.gallery.models.Medium
import com.simplemobiletools.gallery.models.ThumbnailItem
import java.util.*

class GetMediaAsynctask(val context: Context, val mPath: String, val isPickImage: Boolean = false, val isPickVideo: Boolean = false,
                        val showAll: Boolean, val callback: (media: ArrayList<ThumbnailItem>) -> Unit) :
        AsyncTask<Void, Void, ArrayList<ThumbnailItem>>() {
    private val mediaFetcher = MediaFetcher(context)

    override fun doInBackground(vararg params: Void): ArrayList<ThumbnailItem> {
        val pathToUse = if (showAll) SHOW_ALL else mPath
        val getProperDateTaken = context.config.getFileSorting(pathToUse) and SORT_BY_DATE_TAKEN != 0
        val favoritePaths = context.getFavoritePaths()
        val media = if (showAll) {
            val foldersToScan = mediaFetcher.getFoldersToScan()
            val media = ArrayList<Medium>()
            foldersToScan.forEach {
                val newMedia = mediaFetcher.getFilesFrom(it, isPickImage, isPickVideo, getProperDateTaken, favoritePaths)
                media.addAll(newMedia)
            }

            mediaFetcher.sortMedia(media, context.config.getFileSorting(SHOW_ALL))
            media
        } else {
            mediaFetcher.getFilesFrom(mPath, isPickImage, isPickVideo, getProperDateTaken, favoritePaths)
        }
        return mediaFetcher.groupMedia(media, pathToUse)
    }

    override fun onPostExecute(media: ArrayList<ThumbnailItem>) {
        super.onPostExecute(media)
        callback(media)
    }

    fun stopFetching() {
        mediaFetcher.shouldStop = true
        cancel(true)
    }
}
