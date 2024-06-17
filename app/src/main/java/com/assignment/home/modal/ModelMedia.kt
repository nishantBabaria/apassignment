package com.assignment.home.modal

//We often create classes to hold some data in it. In such classes, some standard functions are often derivable from the data
data class ModelMedia(
    var id: String? = null,
    var thumbnail: ModelMediaItemInfo? = null
):java.io.Serializable
