package com.protone.layout.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.protone.layout.paging.PagingDataAPI

class GithubViewModel : ViewModel() {

    fun getPagingData() = PagingDataAPI().getPagingSource(20).cachedIn(viewModelScope)

}