package uz.sicnt.eimzo

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExampleAdapter(private val examleList: List<ExamleItem>,
                     private val listener:OnItemClickListener
) :
    RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.example_item,
            parent, false
        )
        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = examleList[position]

        //holder.imageView.setImageResource(currentItem.imageResource)
        holder.txtDocument_number.text = currentItem.txtDocument_number.toString()
        holder.txtDocument_date.text = currentItem.txtDocument_date.toString()
        holder.txtLastname_uz_latin.text = currentItem.txtLastname_uz_latin
        holder.txtName_uz_latin.text = currentItem.txtName_uz_latin
        holder.txtFrom_department.text = currentItem.txtFrom_department
        holder.txtTo_department.text = currentItem.txtTo_department
        holder.txtDocument_signers.text = currentItem.txtDocument_signers.trim()
        holder.txtPdf_file_name.text = currentItem.txtPdf_file_name
        holder.txtStatus.text = currentItem.txtStatus
        holder.txtStatus.setTextColor(Color.WHITE)


        var rangi = when(holder.txtStatus.text){
            "Yangi"->"#008080"
            "E'lon qilindi"->"#D8CDD1"
            "Qayta ishlash"->"#1976d2"
            "Imzolandi"->"#008080"
            "Bajarildi"->"#FFA726"
            "Yakunlandi"->"#FFA726"
            "Bekor qilindi"->"#FF5252"
            else ->"#FF5252"
        }

        holder.txtStatus.setBackgroundColor(Color.parseColor(rangi))
    }

    override fun getItemCount() = examleList.size

    inner class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        //val imageView:ImageView=itemView.findViewById(R.id.image_view)

        val txtDocument_number: TextView = itemView.findViewById(R.id.txtDocument_number)
        val txtDocument_date: TextView = itemView.findViewById(R.id.txtDocument_date)
        val txtName_uz_latin: TextView = itemView.findViewById(R.id.txtName_uz_latin)
        val txtLastname_uz_latin: TextView = itemView.findViewById(R.id.txtLastname_uz_latin)
        val txtFrom_department: TextView = itemView.findViewById(R.id.txtFrom_department)
        val txtTo_department: TextView = itemView.findViewById(R.id.txtTo_department)
        val txtDocument_signers: TextView = itemView.findViewById(R.id.txtDocument_signers)
        val txtStatus: TextView =itemView.findViewById(R.id.txtStatus)
        val txtPdf_file_name: TextView =itemView.findViewById(R.id.txtPdf_file_name)

        init{
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position=adapterPosition
            if(position!=RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

}
