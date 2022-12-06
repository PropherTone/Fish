package com.protone.layout.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.protone.common.baseType.launchDefault
import com.protone.common.entity.BaseResponse
import com.protone.common.utils.TAG
import com.protone.common.utils.json.toEntity
import com.protone.layout.entity.GithubHot
import com.protone.layout.paging.PagingDataAPI
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.HttpURLConnection

class GithubViewModel : ViewModel() {

    fun getPagingData() = PagingDataAPI().getPagingSource(20).cachedIn(viewModelScope)

}