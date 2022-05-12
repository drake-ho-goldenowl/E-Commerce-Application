package com.goldenowl.ecommerceapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.goldenowl.ecommerceapp.utilities.PASSWORD
import com.goldenowl.ecommerceapp.utilities.USER_NAME

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
        val txtUserName = view.findViewById<EditText>(R.id.txtUserName)
        val txtPassword = view.findViewById<EditText>(R.id.txtPassword)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val userName =  sharedPref.getString(USER_NAME,null)
        val userPassword = sharedPref.getString(PASSWORD,null)
        println("$userName $userPassword")
        if(userName != null && userPassword != null){
            txtUserName.setText(userName.toString())
            txtPassword.setText(userPassword.toString())
        }

        view.findViewById<Button>(R.id.btnLogin).setOnClickListener{

//            with (sharedPref.edit()) {
//                this.putString(USER_NAME, txtUserName.text.toString())
//                this.putString(PASSWORD, txtPassword.text.toString())
//            }

            sharedPref.edit().apply{
                putString(USER_NAME,txtUserName.text.toString())
                putString(PASSWORD,txtPassword.text.toString())
            }.apply()


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