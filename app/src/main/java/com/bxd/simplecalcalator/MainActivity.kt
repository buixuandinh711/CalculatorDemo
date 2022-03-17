package com.bxd.simplecalcalator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bxd.simplecalcalator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val MSG = "Value out of range"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val viewModel: MainViewModel by viewModels()
        binding.mainViewModel = viewModel
        viewModel.isShowErrorToast.observe(this, ) {
            if (it) {
                Toast.makeText(this, MSG, Toast.LENGTH_SHORT).show()
                viewModel.resetErrorToast()
            }
        }
    }
}