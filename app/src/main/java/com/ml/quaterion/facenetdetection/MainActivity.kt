/*
 * Copyright 2023 Shubham Panchal
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ml.quaterion.facenetdetection

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ml.quaterion.facenetdetection.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.bttRecog.setOnClickListener{
            val fragmentLogin = Fragment_login()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container,fragmentLogin).commit()
            goneView()
        }
        activityMainBinding.bttAdmin.setOnClickListener{
            val fragmentAdmin = Fragment_Admin_Login()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,fragmentAdmin).commit()
            goneView()
        }
    }
    fun goneView(){
        activityMainBinding.bttRecog.visibility = View.GONE
        activityMainBinding.bttAdmin.visibility = View.GONE
    }
}
