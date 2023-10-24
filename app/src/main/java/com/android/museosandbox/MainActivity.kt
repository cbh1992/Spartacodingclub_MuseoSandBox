package com.android.museosandbox

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.museosandbox.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var firestore: FirebaseFirestore? = null
    val itemList = arrayListOf<comment>()
    val adapter = ListAdapter(itemList)
    val db = Firebase.firestore
    @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.RecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.RecyclerView.adapter = adapter


        firestore = FirebaseFirestore.getInstance()
        adapter.notifyDataSetChanged()
        binding.Button.setOnClickListener {

            val text = binding.Content.text.toString()
            val date: LocalDate = LocalDate.now()
            val formatdate = DateTimeFormatter.ISO_DATE
            val formatteddate = date.format(formatdate)
            val time =DateTimeFormatter.ISO_TIME
            val formattime = date.format(time)

            val test = hashMapOf(
                "text" to text,
                "date" to formatteddate,
                "time" to formattime
            )

            //데이터 저장하기
            db.collection("Testing")
                .add(test)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            //데이터 가져오기
            db.collection("Testing")
                .get()
                .addOnSuccessListener { result ->
                    //중복출력 방지용 리사이클러뷰 초기화
                    itemList.clear()
                    //파이어스토어의 데이터를 가져오기
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val item = comment(document["text"] as String,document["date"] as String,document["time"] as String)
                        itemList.add(item)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        db.collection("Testing")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val item = comment(document["text"] as String, document["date"] as String, document["time"] as String)
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}