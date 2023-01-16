package com.protone.fish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.protone.common.context.intent
import com.protone.common.context.newLayoutInflater
import com.protone.fish.databinding.ActivityServiceBinding
import com.protone.fish.service.MyService

class ServiceActivity : AppCompatActivity() {

    private val binding by lazy { ActivityServiceBinding.inflate(newLayoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startService(MyService::class.intent)
        binding.button.setOnClickListener {
            startService(MyService::class.intent.putExtra("233", "2333"))
        }
    }
}