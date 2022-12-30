package com.example.mtchatbotproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley


class MainActivity : AppCompatActivity() {
    private var chatsRV: RecyclerView? = null
    private var sendMsgIB: ImageButton? = null
    private var userMsgEdt: EditText? = null
    private val USER_KEY = "user"
    private val BOT_KEY = "bot"
    private var mRequestQueue: RequestQueue? = null
    private var messageModalArrayList: ArrayList<MessageModal>? = null
    private var messageRVAdapter: MessageRVAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatsRV = findViewById(R.id.idRVChats)
        sendMsgIB = findViewById(R.id.idIBSend)
        userMsgEdt = findViewById(R.id.idEdtMessage)
        mRequestQueue = Volley.newRequestQueue(this@MainActivity)
        mRequestQueue!!.cache.clear()
        messageModalArrayList = ArrayList()

        messageRVAdapter = MessageRVAdapter(messageModalArrayList!!, this)
        val linearLayoutManager =
            LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)

        chatsRV = findViewById(R.id.idRVChats)
        chatsRV.adapter = messageRVAdapter
    }

    fun sendMsgClick(view: View){
        if (userMsgEdt?.getText().toString().isEmpty()) {
            Toast.makeText(this@MainActivity, "Please enter your message..", Toast.LENGTH_SHORT)
                .show()
        }
        sendMessage(userMsgEdt?.text.toString())
        userMsgEdt?.setText("")
    }

    private fun sendMessage(userMsg: String) {
        messageModalArrayList!!.add(MessageModal(userMsg, USER_KEY))
        messageRVAdapter?.notifyDataSetChanged()
        val url =
            "http://api.brainshop.ai/get?bid=171452&key=etMaNoGzYoudDo0u&uid=1&msg=$userMsg"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    Log.d("Message", url)
                    val botResponse = response.getString("cnt")
                    messageModalArrayList!!.add(MessageModal(botResponse, BOT_KEY))
                    messageRVAdapter?.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    messageModalArrayList!!.add(MessageModal("No response", BOT_KEY))
                    messageRVAdapter?.notifyDataSetChanged()
                }
            }) {
            messageModalArrayList!!.add(MessageModal("Sorry no response found", BOT_KEY))
            Toast.makeText(this@MainActivity, "No response from the bot..", Toast.LENGTH_SHORT)
                .show()
        }
        queue.add(jsonObjectRequest)
    }
}
