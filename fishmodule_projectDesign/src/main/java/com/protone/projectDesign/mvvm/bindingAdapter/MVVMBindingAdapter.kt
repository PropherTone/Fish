package com.protone.projectDesign.mvvm.bindingAdapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.protone.common.utils.displayUtils.imageLoader.Image

/* @value 方法参数对应在XML中使用的属性名
*  @requireAll 设置是否需要使用全部属性
* */
@BindingAdapter(value = ["IdRes"], requireAll = false)
fun loadImage(imageView: ImageView, @DrawableRes id: Int) {
    Image.load(id).with(imageView.context).into(imageView)
}

@BindingAdapter(value = ["ClickListener","Text"], requireAll = false)
fun setClickListener(button: MaterialButton, listener: View.OnClickListener, text: CharSequence) {
    button.setOnClickListener(listener)
    button.text = text
}