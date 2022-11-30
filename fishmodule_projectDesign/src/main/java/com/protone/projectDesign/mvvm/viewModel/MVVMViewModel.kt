package com.protone.projectDesign.mvvm.viewModel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class MVVMViewModel : ViewModel() {

    val idRes = ObservableField<Int>()

    val buttonText = ObservableField<String>()

    val listener = ObservableField<View.OnClickListener>()
}