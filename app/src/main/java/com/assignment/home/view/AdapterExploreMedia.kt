package com.assignment.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment.R
import com.assignment.base.utils.ImageCacheUtils
import com.assignment.databinding.ItemMediaBinding
import com.assignment.home.modal.ModelMedia
import com.assignment.home.modal.ModelMediaItemInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AdapterExploreMedia : RecyclerView.Adapter<AdapterExploreMedia.YourDataViewHolder>() {
    private var list : List<ModelMedia>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<ModelMedia>?) {
        this.list = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourDataViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YourDataViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if(list != null) list!!.size else 0;
    }

    override fun onBindViewHolder(holder: YourDataViewHolder, position: Int) {
        // Set click listener for retry button
        holder.binding?.textRetry?.setOnClickListener { loadImage(data =  list?.get(position),holder) }

        //call methods for load image
        loadImage(data =  list?.get(position), holder)
    }

    //created function for manage retry
    private fun loadImage(data: ModelMedia?,holder : YourDataViewHolder?) {
        // hide retry button and show image view
        holder?.binding?.textRetry?.visibility = View.GONE
        holder?.binding?.imageView?.visibility = View.VISIBLE

        // cancel any ongoing image loading job
        holder?.currentJob?.cancel()

        //load memory form cache for step shuffling on scroll
        val url = getMediaUrl(thumbnail = data?.thumbnail)
        val memoryCache = ImageCacheUtils.memoryCache
        val key = url.hashCode().toString()
        val cachedBitmap = memoryCache.get(key)
        if (cachedBitmap != null) {
            holder?.binding?.imageView?.setImageBitmap(cachedBitmap)
            holder?.binding?.textRetry?.visibility = View.GONE
        }else{
            holder?.binding?.imageView?.setImageResource(R.drawable.placeholder)

            holder?.currentJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val bitmap = loadImageWithCache(getMediaUrl(thumbnail = data?.thumbnail), holder?.itemView?.context)
                    withContext(Dispatchers.Main) {
                        holder?.binding?.imageView?.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // show retry button and hide image view
                        holder?.binding?.textRetry?.visibility = View.VISIBLE
                        holder?.binding?.imageView?.visibility = View.GONE
                    }
                }
            }
        }
    }

    //bind media url for response
    private  fun getMediaUrl(thumbnail : ModelMediaItemInfo?) : String{
        return "${thumbnail?.domain ?: ""}/${thumbnail?.basePath ?: ""}/0/${thumbnail?.key ?: ""}"
    }

    private fun loadImageWithCache(url: String, context: Context?): Bitmap {
        // ------ memory cache ------
        val memoryCache = ImageCacheUtils.memoryCache
        val key = url.hashCode().toString()    // generate a key for the cache
        memoryCache.get(key)?.let {
            return it
        }

        // ------ disk cache ------
        val fileName = url.hashCode().toString() //Generate a file name based on the URL
        val cacheDir = context?.cacheDir
        val cacheFile = File(cacheDir, fileName)
        if (cacheFile.exists()) {    //check if the file exists in the disk cache
            val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath)
            memoryCache.put(key, bitmap)
            return bitmap
        }

        // if not in cache, download the image
        val bitmap = try {
            downloadImageFromServer(url)
        } catch (e: IOException) {
            throw e // Rethrow the exception to be caught in the loadImage function
        }
        // save the image to disk cache
        saveImageToDiskCache(bitmap, cacheFile)

        // save the image to memory cache
        memoryCache.put(key, bitmap)

        return bitmap
    }

    private fun downloadImageFromServer(url: String): Bitmap {
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.connect()
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP error code: ${connection.responseCode}")
            }
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } finally {
            connection.disconnect()
        }
    }

    private fun saveImageToDiskCache(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    //main data render class
    class YourDataViewHolder(binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: ItemMediaBinding? = null
        var currentJob: Job? = null
        init {
            this.binding = binding
        }
    }
}