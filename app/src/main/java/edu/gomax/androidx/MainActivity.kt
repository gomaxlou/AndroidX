package edu.gomax.androidx

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var db = FirebaseFirestore.getInstance()

        val user = HashMap<String, Any>()

        firstNameET.setText("Gx1")
        lastNameET.setText("Lo1")
        bornET.setText("1977")

        val myDataset = ArrayList<String>()
        for (i in 0..50) {
            myDataset.add("A$i")
        }

        var myDataset2 = listOf(
            User("Gx", "Lo", 1976),
            User("Micky", "Chang", 1976),
            User("Aaron", "Wang", 1977),
            User("雲長", "關", 170),
            User("Michi", "Chen", 1981)
        )

        var mAdapter = MyAdapter2(myDataset2.sortedBy { it.born })
//        recv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        recv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        recv.adapter = mAdapter

        with(recv) {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            // 設置格線
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }

        bStoreData.setOnClickListener {
            user["first"] = firstNameET.text.toString()
            user["last"] = lastNameET.text.toString()
            user["born"] = bornET.text.toString().toInt()

            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
                    Toast.makeText(this, "DocumentSnapshot added with ID: ${it.id}", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error adding document", it)
                }
        }

        b_readData.setOnClickListener {
            db.collection("users").get()
                .addOnSuccessListener {
                    textviewInfo.text = it.size().toString()

                    var dataList = arrayListOf<User>()
                    it.forEach { result ->
                        Log.d(TAG, "${result.data}")
//                        dataList.add(User(
//                            result.get("first").toString(),
//                            result.get("last").toString(),
//                            result.get("born").toString().toInt()
//                        ))

                        dataList.add(result
                            .run {
                                User(
                                    get("first").toString(),
                                    get("last").toString(),
                                    get("born").toString().toInt()
                                )
                            })
                    }

                    var mmAdapter = MyAdapter2(dataList.sortedBy { it.born })
                    Log.d(TAG, "size:${dataList.size}")
                    recv.adapter = mmAdapter

                }
                .addOnFailureListener {
                    Log.w(TAG, "Error getting documents.", it)
                }
        }

    }

    private inner class MyAdapter2 constructor(var mData: List<User>) :
        RecyclerView.Adapter<MyAdapter2.ViewHolder>() {
        //        var mData = data
        internal inner class ViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {
            //            var mTextView: TextView = textviewInfo
            var posTextView: TextView = super.itemView.info_text
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var v = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.mTextView.text = mData[position].run {
//                "$first, $last, $born"
//            }
            holder.posTextView.text = mData[position].run {
                "$first, $last"
            }
            holder.itemView.setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Item $position is clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }

            holder.itemView.setOnLongClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Item $position is long clicked.",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
        }
    }

    class User(val first: String, val last: String, val born: Int)
}
