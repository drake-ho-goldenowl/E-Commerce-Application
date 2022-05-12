package com.goldenowl.ecommerceapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnLogin).setOnClickListener{
            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }

        view.findViewById<Button>(R.id.btnRegister).setOnClickListener{
            parentFragmentManager.commit {
                replace<SignUpFragment>(R.id.host_fragment_log)
                setReorderingAllowed(true)
            }
        }
    }
}