package com.ml.quaterion.facenetdetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ml.quaterion.facenetdetection.databinding.FragmentAddUserBinding

class Fragment_add_user: Fragment() {
    private var _binding : FragmentAddUserBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddUserBinding.inflate(inflater,container,false)
        

        return binding.root
    }
}