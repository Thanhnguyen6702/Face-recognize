package com.ml.quaterion.facenetdetection

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ml.quaterion.facenetdetection.databinding.FragmentLoginAdminBinding


class Fragment_Admin_Login: Fragment() {
    private var _binding : FragmentLoginAdminBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginAdminBinding.inflate(inflater,container,false)
        val textWatcher = object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val account = binding.taikhoan.text.trim()
                val password = binding.matkhau.text.trim()
                if(account != "" && password != ""){
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                }
                else{
                    binding.buttonLogin.isEnabled = false
                    binding.buttonLogin.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
        binding.taikhoan.addTextChangedListener(textWatcher)
        binding.matkhau.addTextChangedListener(textWatcher)
        binding.buttonLogin.setOnClickListener{

            if(binding.taikhoan.text.toString() == "admin" && binding.matkhau.text.toString() == "admin"){
                val fragmentAdmin = Fragment_Admin()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container,fragmentAdmin).commit()
            }
        }
        return binding.root;
    }
}