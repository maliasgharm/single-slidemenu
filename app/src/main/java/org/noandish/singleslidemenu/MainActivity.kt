package org.noandish.singleslidemenu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        imageView5.setOnClickListener {
            ssmv.closeAll()
        }

    }
    fun click(@Suppress("UNUSED_PARAMETER") v : View){
        Toast.makeText(this, "Clicked !", Toast.LENGTH_LONG).show()



    }
}
