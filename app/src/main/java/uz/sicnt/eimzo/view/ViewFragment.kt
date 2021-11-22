package uz.sicnt.eimzo.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.model.TreeNode.TreeNodeClickListener
import com.unnamed.b.atv.model.TreeNode.TreeNodeLongClickListener
import com.unnamed.b.atv.view.AndroidTreeView
import kotlinx.android.synthetic.main.comment_dialog.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_resolution.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import uz.sicnt.eimzo.*
import uz.sicnt.eimzo.holder.IconTreeItemHolder
import uz.sicnt.eimzo.holder.SelectableHeaderHolder
import uz.sicnt.eimzo.holder.SelectableItemHolder
import uz.sicnt.horcrux.Constants
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ViewFragment : Fragment(), OnLoadCompleteListener {
    private var tView: AndroidTreeView? = null
    private var tView2: AndroidTreeView? = null
//    private var root: DefaultTreeNode<String>? = null

    internal var expandableListView2: ExpandableListView? = null
    internal var adapterFile: ExpandableListAdapter? = null

    internal var titleList2: List<String>? = null
    var GLOBALCOMMENT: String? = null;

    var bundle: Bundle? = null
    var pdfView: PDFView? = null
    var reaction_show: String? = null
    var reaction_status: String? = null
    var action_type_id: String? = null
    var document_id: String? = null
    var access_token: String? = null
    var base64: String? = null
    var pdf64: String? = null
    var decodedPDF: ByteArray? = null
    var documentStatus: Int? = null
    var status = ""
    var commentsId=""
    var employee_id=""
    var resolution_show:Boolean=false
    var document_files:String=""

    var firstname_uz_latin: String? = null
    var assignment: String? = null
    var due_at: String? = null
    var resolution_fio: String? = null
    var Due_date:String?=null
    var IzohResolution:String?=null

    val listDataFile = HashMap<String, List<StringWithFileID>>()
    val hujjatTarixi : ArrayList<StringWithFileID> = ArrayList<StringWithFileID>()
    var filesList: ArrayList<StringWithFileID> = ArrayList<StringWithFileID>()

    var hodimlar: ArrayList<StringWithHodimTag> = ArrayList<StringWithHodimTag>()
    var resolutionType: ArrayList<StringWithTag> = ArrayList<StringWithTag>()
    var ResolutiontypeTag: Object? = null
    var ResolutiontypeAssignment = ""
    var EmploeesID = ""
    var adapterHodim: ArrayAdapter<StringWithHodimTag>? = null
    var adapterResolutionType: ArrayAdapter<StringWithTag>? = null
    val root = TreeNode.root()
    var containerView: ViewGroup? = null
    var containerView2: ViewGroup? = null

    private val nodeClickListener =
        TreeNodeClickListener { node, value ->
            val item: IconTreeItemHolder.IconTreeItem = value as IconTreeItemHolder.IconTreeItem
            if (item.fileid != "") {

                var pdfFrag = PDFFrag()
                val bundle = Bundle()
                bundle.putString("pdffileid", item.fileid)
                pdfFrag.arguments = bundle
                pdfFrag?.show(childFragmentManager,"PDF")
            }
            true
        }

    private val nodeLongClickListener =
        TreeNodeLongClickListener { node, value ->
            val item: IconTreeItemHolder.IconTreeItem = value as IconTreeItemHolder.IconTreeItem

            if (item.fileid != "") {
                var pdfFrag = PDFFrag()
                val bundle = Bundle()
                bundle.putString("pdffileid", item.fileid)
                pdfFrag.arguments = bundle
                pdfFrag?.show(childFragmentManager,"PDF")
            }
            Toast.makeText(context, "Long click: " + item.fileid, Toast.LENGTH_SHORT).show()
            true
        }

    private fun fillcomment(jsonObject: JSONObject, treeNode: TreeNode){
        val jsonArray = jsonObject.getJSONArray("comments")

        for (i in 0 until jsonArray.length()) {
            var status_id = jsonArray.getJSONObject(i).optString("status").replace("null", "30").toInt()
            var comment = jsonArray.getJSONObject(i).optString("comment")
                .replace("null", "")
                .replace("created", "Yangi yaratish")
                .replace("processing", "Jarayonda")
                .replace("changed", "O'zgartirildi")
                .replace("published", "E'lon qilindi")
            var comment_time = jsonArray.getJSONObject(i).optString("signed_at").replace("null", "")
            var commentfull = comment + "\n" + comment_time

            if (comment != "" && comment != "Jarayonda" && status_id != 11 && status_id != 9 && status_id != 0 && status_id != 4) {
                val child = TreeNode(
                    IconTreeItemHolder.IconTreeItem(
                        R.string.ic_message,
                        commentfull
                        , "", "#12558C"
                    )
                ).setViewHolder(SelectableItemHolder(activity))
                treeNode.addChild(child)
            }

//            ------  Status
            if (status_id == 1 ||  status_id == 3 || status_id == 11 || status_id == 21 || status_id == 9 || status_id == 0 || status_id == 4) {

                    var comment_status = ""
                    when (status_id) {
                        0 -> { comment_status = comment_time
                            val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_check_box_outline, comment_status, "","#4DAA51")).setViewHolder(SelectableItemHolder(activity))
                            treeNode.addChild(child)
                        }
                        1 -> { comment_status = comment_time
                            val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_check_box_outline, comment_status, "","#12558C")).setViewHolder(SelectableItemHolder(activity))
                            treeNode.addChild(child)
                            }
                        3 -> { comment_status = "Jarayonda" +"\t" +"|" +"\t"+ comment_time
                            val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_timer, comment_status, "","#2196F3")).setViewHolder(SelectableItemHolder(activity))
                            treeNode.addChild(child)
                            }
                        4 -> { comment_status = comment + "\n"+ comment_time
                            val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_warning, comment_status, "","#FFEB3B")).setViewHolder(SelectableItemHolder(activity))
                            treeNode.addChild(child)
                        }
                        9 -> { comment_status = "E'lon Qilindi" +"\t" +"|" +"\t"+ comment_time
                            val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_keyboard_arrow_up, comment_status, "","#4DAA51")).setViewHolder(SelectableItemHolder(activity))
                            treeNode.addChild(child)
                            }
                        11 -> {if (comment == ""){
                            comment_status = comment_time
                                val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_check_box, comment_status, "","#4DAA51")).setViewHolder(SelectableItemHolder(activity))
                                treeNode.addChild(child)
                            }else{
                                comment_status = comment + "\n"+ comment_time
                                val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_check_box, comment_status, "","#4DAA51")).setViewHolder(SelectableItemHolder(activity))
                                treeNode.addChild(child)
                            }
                        }
                        21 -> { comment_status = comment_time
                            val child = TreeNode( IconTreeItemHolder.IconTreeItem(R.string.ic_cancel, comment_status, "","#FF1100")).setViewHolder(SelectableItemHolder(activity))
                            treeNode.addChild(child)}
                    }
                }

//            --------

            var jsonfiles =  jsonArray.getJSONObject(i).getJSONArray("files")

            for(j in 0 until jsonfiles.length()){

                var file_name=jsonfiles.getJSONObject(j).optString("file_name")
                var file_name_id=jsonfiles.getJSONObject(j).optString("id")

                val child = TreeNode(
                    IconTreeItemHolder.IconTreeItem(
                        R.string.ic_drive_file,
                        file_name
                        , file_name_id, "#12558C"
                    )
                ).setViewHolder(SelectableItemHolder(activity))
                treeNode.addChild(child)
            }
        }
    }

    private fun filldata(jsonObject: JSONObject, treeNode: TreeNode){
        fillcomment(jsonObject, treeNode)
        val jsonArray = jsonObject.getJSONArray("children")
        for (i in 0 until jsonArray!!.length()) {
            val child = TreeNode(IconTreeItemHolder.IconTreeItem(
                R.string.ic_person,
                jsonArray?.getJSONObject(i)?.optString("fio"),
                "",
                "#12558C"
            )).setViewHolder(SelectableHeaderHolder(activity))
            filldata(jsonArray.getJSONObject(i), child)
            treeNode.addChild(child)
        }
    }

    private fun history(jsonArray: JSONArray) {
        for (i in 0 until jsonArray!!.length()) {
            var outtext = jsonArray?.getJSONObject(i)?.optString("fio")
            val child = TreeNode(IconTreeItemHolder.IconTreeItem(
                R.string.ic_person,
                outtext,
                "",
                "#12558C"
            )).setViewHolder(SelectableHeaderHolder(activity))

            val jsonObject= jsonArray?.getJSONObject(i)
            if (jsonArray != null && jsonArray.length() > 0) {
                filldata(jsonObject, child)
            }
            root?.addChild(child)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        containerView = view.findViewById(R.id.containerreal) as ViewGroup
        containerView2 = view.findViewById(R.id.containerreal) as ViewGroup

        val createButton = view.createButton
        val checkButton = view.checkButton
        val jarayondaButton = view.jarayondaButton
        val izohlarButton = view.izohlarButton
        val asoslabBeringButton = view.asoslabBeringButton
        val resolution_showButton = view.resolution_showButton
        val pdfExpandButton = view.pdfExpandButton
        val tvTuzuvchi = view.tvTuzuvchi

        val tv_parent_employee = view.tv_parent_employee
        val tv_assignment = view.tv_assignment
        val tv_due_at = view.tv_due_at
        val tvMuddati = view.tvMuddati
        val tvDocumentStatus = view.tvDocumentStatus

        pdfView = view.pdfView
        bundle = arguments

        if (bundle != null) {
            pdf64 = bundle!!.getString("pdf64", "").toString()
            base64 = bundle!!.getString("base64", "").toString()
            reaction_show = bundle!!.getString("reaction_show", "")
            reaction_status = bundle!!.getString("reaction_status", "").toString()
            action_type_id = bundle!!.getString("action_type_id", "").toString()
            document_id = bundle!!.getString("id", "")
            access_token = bundle!!.getString("access_token", "").toString()
            tvTuzuvchi.text = bundle!!.getString("tuzuvch_fio").toString().replace("null","")
            firstname_uz_latin = bundle!!.getString("firstname_uz_latin", "")
            assignment = bundle!!.getString("assignment", "")
            due_at = bundle!!.getString("due_at", "")
            resolution_fio = bundle!!.getString("resolution_fio", "")
            commentsId = bundle!!.getString("commentsId", "")
            employee_id = bundle!!.getString("employee_id", "")
            resolution_show = bundle!!.getBoolean("resolution_show", false)
            document_files=bundle!!.getString("document_files", "")

            var document_filesArray=JSONArray(document_files)

            for(file_i in 0 until document_filesArray.length()){
                var file_name=document_filesArray.getJSONObject(file_i).optString("file_name")
                var file_name_id=document_filesArray.getJSONObject(file_i).optString("id")
                filesList.add(StringWithFileID(file_name,file_name_id))
            }

            RequsetWithHistory()

//            val handler = Handler()
//            val runnable: Runnable = object : Runnable {
//                override fun run() {
//                    tView = AndroidTreeView(context, root)
//                    tView!!.setDefaultAnimation(true)
//                    tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
//                    tView!!.setDefaultNodeClickListener(nodeClickListener)
//                    tView!!.setDefaultNodeLongClickListener(nodeLongClickListener)
//                    containerView?.removeAllViews()
//                    containerView?.addView(tView?.view)
//                    tView?.expandAll()
//                }
//            }
//            handler.postDelayed(runnable, 1100)

            if (firstname_uz_latin != null) {
                tv_parent_employee.text = firstname_uz_latin.toString().replace("null","")
                tv_assignment.text = assignment.toString().replace("null","")
                tv_due_at.text = due_at.toString().replace("null","")
                resolution_fio = resolution_fio.toString().replace("null","")

                tv_parent_employee.visibility = View.VISIBLE
                tv_assignment.visibility = View.VISIBLE
                tv_due_at.visibility = View.VISIBLE
                tvMuddati.visibility = View.VISIBLE
            } else {
                tv_parent_employee.visibility = View.GONE
                tv_assignment.visibility = View.GONE
                tv_due_at.visibility = View.GONE
                tvMuddati.visibility = View.GONE
            }

            tvDocumentStatus?.text = when (documentStatus) {
                0 -> "Yangi"
                1 -> "E'lon qiliish"
                2 -> "Qayta ishlash"
                3 -> "Imzolandi"
                4 -> "Bajarildi"
                5 -> "Yakunlandi"
                6 -> "Bekor qilindi"
                else -> ""
            }

            var rangi = when (tvDocumentStatus.text) {
                "Yangi" -> "#008080"
                "E'lon qilindi" -> "#D8CDD1"
                "Qayta ishlash" -> "#1976d2"
                "Imzolandi" -> "#008080"
                "Bajarildi" -> "#FFA726"
                "Yakunlandi" -> "#FFA726"
                "Bekor qilindi" -> "#FF5252"
                else -> "#FF5252"
            }

            tvDocumentStatus.setBackgroundColor(Color.parseColor(rangi))
            expandableListView2 = view.expandableListView
            expandableListView2?.visibility=View.VISIBLE

            if (expandableListView2 != null) {

                val kutilayotganHarakat = ArrayList<StringWithFileID>()

                listDataFile["1 Kutilayotgan harakat"]=kutilayotganHarakat

//                filesList.add(StringWithFileID("Pdffile.pdf","83170"))
//                filesList.add(StringWithFileID("Pdffile2.pdf","83170"))

                listDataFile["2 Files"] = filesList
                listDataFile["3 Hujjat tarixi"] = hujjatTarixi


                val sortedMap = HashMap<String, List<StringWithFileID>>()
                listDataFile.entries.sortedBy { it.key }.forEach { sortedMap[it.key] = it.value }

                titleList2 = ArrayList(sortedMap.keys)
                adapterFile = context?.let { CustomExpandableListAdapterFile(it, titleList2 as ArrayList<String>,listDataFile) }
                expandableListView2!!.setAdapter(adapterFile)

                for (i in 0 until (adapterFile as CustomExpandableListAdapterFile).groupCount)
                    expandableListView2?.expandGroup(i)

                expandableListView2!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

                    var pdfFile: String? = null
                    pdfFile = listDataFile[(titleList2 as ArrayList<String>)[groupPosition]]!!.get(
                        childPosition
                    ).toString()
                    var pdfFileId =
                        listDataFile[(titleList2 as ArrayList<String>)[groupPosition]]!!.get(
                            childPosition
                        ).tag.toString()

                    println("pdfFileId "+ pdfFileId)

                    if (pdfFile.contains("pdf")) {
                        var pdfFrag = PDFFrag()
                        val bundle = Bundle()
                        bundle.putString("pdffileid", pdfFileId)
                        pdfFrag.arguments = bundle
                        pdfFrag?.show(childFragmentManager,"PDF")
                    }
                    false
                }
            }
        }

        if (reaction_show?.equals("true") == true && (reaction_status?.equals("0") == true || reaction_status?.equals(
                "3"
            ) == true || reaction_status?.equals("4") == true)
        ) {
            createButton.isEnabled = true
            checkButton.isEnabled = true
            jarayondaButton.isEnabled = true
            izohlarButton.isEnabled = true
            asoslabBeringButton.isEnabled = true
            jarayondaButton.visibility = View.VISIBLE
            izohlarButton.visibility = View.VISIBLE
            asoslabBeringButton.visibility = View.VISIBLE
            createButton.visibility = View.VISIBLE
            checkButton.visibility = View.VISIBLE
        } else {
            createButton.isEnabled = false
            checkButton.isEnabled = false
            jarayondaButton.isEnabled = false
            izohlarButton.isEnabled = false
            asoslabBeringButton.isEnabled = false
            resolution_showButton.isEnabled = false
            jarayondaButton.visibility = View.GONE
            izohlarButton.visibility = View.GONE
            asoslabBeringButton.visibility = View.GONE
            createButton.visibility = View.GONE
            checkButton.visibility = View.GONE
            resolution_showButton.visibility = View.GONE
        }


        if (reaction_show?.equals("true") == true && reaction_status?.equals("3") == false) {
            jarayondaButton.isEnabled = true
            jarayondaButton.visibility = View.VISIBLE
        } else {
            jarayondaButton.isEnabled = false
            jarayondaButton.visibility = View.GONE
        }

        if (employee_id.isNotBlank()) {
            asoslabBeringButton.isEnabled = true
            asoslabBeringButton.visibility = View.VISIBLE
        } else {
            asoslabBeringButton.isEnabled = false
            asoslabBeringButton.visibility = View.GONE
        }

        if (resolution_show==true) {
            resolution_showButton.isEnabled = true
            resolution_showButton.visibility = View.VISIBLE
        } else {
            resolution_showButton.isEnabled = false
            resolution_showButton.visibility = View.GONE
        }

        if (pdf64 != null) {
            decodedPDF = Base64.decode(pdf64?.toString(), Base64.NO_WRAP)

            pdfView?.fromBytes(decodedPDF)
                ?.password(null)
                ?.defaultPage(0)
                ?.enableSwipe(true)
                ?.swipeHorizontal(false)
                ?.enableSwipe(true)
                ?.onLoad(this)
                ?.enableDoubletap(true)
                ?.enableAntialiasing(true)
                ?.enableAnnotationRendering(false)
                ?.spacing(5)
                ?.load()
        }


        pdfView?.minimumHeight = 1550

//        Buttons
        pdfExpandButton.setOnClickListener {
            if(decodedPDF != null ) {

                var pdfFrag = PDFFrag()
                val bundle = Bundle()
                bundle.putByteArray("pdffile", decodedPDF)
                pdfFrag.arguments = bundle
                pdfFrag?.show(childFragmentManager,"PDF")

//                val pdfFrag = PDFFrag()
//                val bundle = Bundle()
//                bundle.putByteArray("pdffile", decodedPDF)
//                pdfFrag.arguments = bundle
//                val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
//                transaction?.addToBackStack(null)?.replace(R.id.fragment, pdfFrag)?.commit()
            }
        }

        createButton.setOnClickListener {

            val mDialogView = LayoutInflater.from(context).inflate(R.layout.comment_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
            mDialogView.dialogBtnAccept.text = "Tasqidlayman"

            val mAlertDialog = mBuilder.show()
            mDialogView.dialogBtnAccept.setOnClickListener {
                mAlertDialog.dismiss()

                val comment = mDialogView.dialogNameEt.text.toString()
                GLOBALCOMMENT = mDialogView.dialogNameEt.text.toString()

//                if (GLOBALCOMMENT.isNullOrBlank() == false) {
                Toast.makeText(context, "$document_id", Toast.LENGTH_LONG).show()
                status = "1"
                if (!base64.toString().equals("null")) {
                    MyApplication.horcrux.appendPkcs7(context as Activity, base64.toString(), MyApplication.horcrux.getSerialNumber())
                } else {
                    MyApplication.horcrux.createPKCS7(context as Activity, pdf64.toString(), MyApplication.horcrux.getSerialNumber())
                }
//                } else {
//                    Toast.makeText(context, "Izoh kiriting!!!", Toast.LENGTH_LONG).show()
//                }
            }
        }

        checkButton.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.comment_dialog, null)
            mDialogView.dialogBtnAccept.setBackgroundResource(R.drawable.shape_custom2)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
            mDialogView.dialogBtnAccept.text = "Bekor qilish"
            mDialogView.dialogBtnAccept.setTextColor(Color.RED)

            val mAlertDialog = mBuilder.show()
            mDialogView.dialogBtnAccept.setOnClickListener {
                mAlertDialog.dismiss()

                val comment = mDialogView.dialogNameEt.text.toString()
                GLOBALCOMMENT = mDialogView.dialogNameEt.text.toString()

                //               if (GLOBALCOMMENT.isNullOrBlank() == false) {
                Toast.makeText(context, "$document_id", Toast.LENGTH_LONG).show()
                status = "2"
                if (!base64.toString().equals("null")) {
                    MyApplication.horcrux.appendPkcs7(context as Activity, base64.toString(), MyApplication.horcrux.getSerialNumber())
                } else {
                    MyApplication.horcrux.createPKCS7(context as Activity, pdf64.toString(), MyApplication.horcrux.getSerialNumber())
                }
                //               } else {
                //                   Toast.makeText(context, "Izoh kiriting!!!", Toast.LENGTH_LONG).show()
                //               }
            }
        }

        jarayondaButton.setOnClickListener {
            Requset().RequsetWithJarayonda(document_id, access_token)

            jarayondaButton.isEnabled = false
            jarayondaButton.visibility = View.GONE
        }

        izohlarButton.setOnClickListener {

            var mDialogView = LayoutInflater.from(context).inflate(R.layout.comment_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
            mDialogView.dialogBtnAccept.text = "Yuborish"
            mDialogView.dialogBtnAccept.setBackgroundResource(R.drawable.shape_custom)
            val mAlertDialog = mBuilder.show()
            mDialogView.dialogBtnAccept.setOnClickListener {
                mAlertDialog.dismiss()

                val comment = mDialogView.dialogNameEt.text.toString()
                GLOBALCOMMENT = mDialogView.dialogNameEt.text.toString()

                //               if (GLOBALCOMMENT.isNullOrBlank() == false) {
                Requset().RequsetWithIzoh(document_id, access_token, GLOBALCOMMENT!!)
                izohlarButton.isEnabled = false
                izohlarButton.visibility = View.GONE
                //               } else {
                //                   Toast.makeText(context, "Izoh kiriting!!!", Toast.LENGTH_LONG).show()
                //               }
            }

        }

        asoslabBeringButton.setOnClickListener {
            Requset().RequsetWithAsoslabBering(commentsId, access_token, employee_id)
            asoslabBeringButton.isEnabled = false
            asoslabBeringButton.visibility = View.GONE
        }

        resolution_showButton.setOnClickListener {
            var search = ""
            val mDialogResolution =
                LayoutInflater.from(context).inflate(R.layout.fragment_resolution, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogResolution)
            hodimlar.add(StringWithHodimTag("Hodimlarni tanlang", "0"))
            resolutionType.add(StringWithTag("Topshiriqni tanlang", "0"))

            RequsetWithHodim(search)

            adapterHodim = context?.let { it1 -> ArrayAdapter<StringWithHodimTag>(it1, android.R.layout.simple_list_item_1,hodimlar) }
//            resolutionType.add(StringWithTag("Topshiriqni tanlang", "0"))
            adapterResolutionType = context?.let { it1 -> ArrayAdapter<StringWithTag>(it1, android.R.layout.simple_list_item_1,resolutionType) }

            mDialogResolution.spHodimlar?.adapter = adapterHodim
            mDialogResolution.spTypeResolution?.adapter = adapterResolutionType
            val mAlertDialog = mBuilder.show()

//            mDialogResolution.spHodimlar.prompt="Hodimlarni tanlang"
//            mDialogResolution.spHodimlar.setOnSearchTextChangedListener {
//                RequsetWithHodim(search)
//                adapterHodim?.notifyDataSetChanged()
//            }

            mDialogResolution.spHodimlar.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val s: StringWithHodimTag =
                            parent.getItemAtPosition(position) as StringWithHodimTag
                        EmploeesID = (s.tag as Object).toString()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }

            mDialogResolution.spTypeResolution.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val s: StringWithTag = parent.getItemAtPosition(position) as StringWithTag
                        ResolutiontypeTag = s.tag as Object
                        ResolutiontypeAssignment = s.string
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }

            mDialogResolution.addAcceptBtn.setOnClickListener {

                Due_date=mDialogResolution.etDue_date.text.toString()
                IzohResolution=mDialogResolution.etIzohResolution.text.toString()

                if (ResolutiontypeTag.toString().isNotBlank()
                    && !ResolutiontypeTag.toString().equals("0")
                    &&  Due_date.isNullOrBlank() == false
                    && EmploeesID.isNullOrBlank() == false
                    && !EmploeesID.equals("0")
                ) {

                    RequestWithQoshish()
                    mAlertDialog.dismiss()
                } else {
                    Toast.makeText(context, "To'gri toldiring!!!", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val createButton = view.createButton
//        val checkButton = view.checkButton
//        val jarayondaButton = view.jarayondaButton
//        val izohlarButton = view.izohlarButton
//        val asoslabBeringButton = view.asoslabBeringButton
//        val resolution_showButton = view.resolution_showButton
//        val pdfExpandButton = view.pdfExpandButton
//
//
//    }



    private fun RequestWithQoshish() {

        val URL = ConnectURL().QOSHISHURL
        var okHttpClient: OkHttpClient = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("action_type", "$ResolutiontypeTag")
            .addFormDataPart("assignment", "$IzohResolution")
            .addFormDataPart("document_id", "$document_id")
            .addFormDataPart("due_date", "$Due_date")
            .addFormDataPart("employee", "$EmploeesID")
            .addFormDataPart("sequence", "0")
            .build()
        val request: Request = Request
            .Builder()
            .url(URL)
            .method("POST", body)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response?) {
            }
        })
    }

    private fun RequsetWithHodim(search: String) {

        val URL = ConnectURL().HODIMURL
        var okHttpClient: OkHttpClient = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("search", "$search")
            .addFormDataPart("locale", "uz_latin")
            .build()
        val request: Request = Request
            .Builder()
            .url(URL)
            .method("POST", body)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {

                try {
                    val jsonResponse: JSONObject
                    if (response != null) {
                        val jsonResponse = JSONObject(response.body()?.string())
                        var jsonResponseDataArray = jsonResponse.getJSONArray("Employees")
                        hodimlar.clear()
                        hodimlar.add(StringWithHodimTag("Hodimlarni tanlang", "0"))

                        for (i in 0 until jsonResponseDataArray.length()) {
                            var EmploeesID = jsonResponseDataArray.getJSONObject(i).optString("id")
                            var tabel = jsonResponseDataArray.getJSONObject(i).optString("tabel")
                            var firstname_uz_latin =
                                jsonResponseDataArray.getJSONObject(i).optString("firstname")
                            var lastname_uz_latin =
                                jsonResponseDataArray.getJSONObject(i).optString("lastname")
                            var middlename_uz_latin =
                                jsonResponseDataArray.getJSONObject(i).optString("middlename")
                            var main_staff =
                                jsonResponseDataArray.getJSONObject(i).getJSONArray("main_staff")
                            var name = main_staff.getJSONObject(0).optJSONObject("position")
                                .optString("name")
                            hodimlar.add(
                                StringWithHodimTag(
                                    "$firstname_uz_latin $lastname_uz_latin $middlename_uz_latin $tabel \n $name ",
                                    EmploeesID
                                )
                            )
                        }

                        var Resolution_typesArray = jsonResponse.getJSONArray("Resolution_types")
                        resolutionType.clear()
                        resolutionType.add(StringWithTag("Topshiriqni tanlang", "0"))
                        for (k in 0 until Resolution_typesArray.length()) {
                            var name = Resolution_typesArray.getJSONObject(k).optString("name")
                            var id = Resolution_typesArray.getJSONObject(k).optString("id")
                            resolutionType.add(StringWithTag("$name", "$id"))
                        }

                    } else {
                        println("JAVOB NULL")
                    }
                } catch (er: Exception) {
                    println("HATO " + er.toString())
                }
            }
        })
    }

    private fun RequsetWithHistory() {

        val URL = ConnectURL().HISTORYURL + document_id
        var okHttpClient: OkHttpClient = OkHttpClient()
        val mediaType = MediaType.parse("text/plain")
        val request: Request = Request
            .Builder()
            .url(URL)
            .method("GET", null)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {


                val jsonResponse = JSONObject(response?.body()?.string())


                //       println("jsonResponse $jsonResponse")

                var historyArray = jsonResponse.optJSONArray("history")
                history(historyArray)


//                if (tView != null) {
//                    (tView?.view?.getParent() as FrameLayout).removeView(tView?.view)
////                    (view!!.parent.parent as ViewGroup).removeView(view!!.parent as ViewGroup)
//                }
//        if(tView == null ) {
//                (containerView?.parent as? ViewGroup)?.removeView(tView?.view)




                    requireActivity()?.runOnUiThread() {
                        tView = AndroidTreeView(context, root)
                        tView!!.setDefaultAnimation(true)
                        tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
                        tView!!.setDefaultNodeClickListener(nodeClickListener)
                        tView!!.setDefaultNodeLongClickListener(nodeLongClickListener)
                        containerView?.removeAllViews()
                        containerView?.addView(tView?.view)
                        tView?.expandAll()
                    }


//                for (i in 0 until historyArray.length()) {
//
//                    //                  var document_signer = historyArray.getJSONObject(i).getJSONObject("document_signer")
//                    var history_comments = historyArray.getJSONObject(i).optJSONArray("comments")
//
//                    var history_signed_at = historyArray.getJSONObject(i).optString("signed_at")
//                        history_signed_at=history_signed_at.replace("null","")
//
//                    var history_childrn = historyArray.getJSONObject(i).optJSONArray("children")
//
//                    var document_signer_fio = historyArray.getJSONObject(i).optString("fio")
//                        document_signer_fio=document_signer_fio.replace("null","")
//
//                    if (history_comments.length()>0) {
//                         history_comment_add(document_signer_fio,history_signed_at,history_comments)
//                    }
//
//                    if (historyArray.getJSONObject(i).has("files")) {
//                        var history_files = historyArray.getJSONObject(i).optJSONArray("files")
//
//                        for (files_i in 0 until history_files.length()) {
//                            var history_file_name =
//                                history_files.getJSONObject(files_i).optString("file_name").replace("null","")
//                            var history_file_id =
//                                history_files.getJSONObject(files_i).optString("id")
//                            hujjatTarixi.add(StringWithFileID("\t\t$history_file_name","$history_file_id"))
//                            //hujjatTarixi2.add("$history_file_id")
//                        }
//                    }
//                    if (history_childrn.toString().equals("[]") != true) {
//                        history_analiz(history_childrn)
//                    }
//                }
            }
        })
    }

    //    private fun setchildnode(jsonObject: JSONObject, child: DefaultTreeNode<String>): DefaultTreeNode<String>? {
//        try {
//            val jsonArray = jsonObject.getJSONArray("children")
//            for (i in 0 until jsonArray.length()) {
//                val child2 = DefaultTreeNode(jsonArray.getJSONObject(i).optString("fio"))
//                child.addChild(setchildnode(jsonArray.getJSONObject(i), child2))
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        return child
//    }

    private fun history_comment_add(document_signer_fio:String,history_signed_at:String,history_comments: JSONArray?) {

        var history_comment: String
        var history_comment_all: String = ""

        for (i_comments in 0 until history_comments!!.length()) {
            history_comment = history_comments.getJSONObject(i_comments).optString("comment")

            if (history_comment.equals("null")==false) {
                history_comment_all += "\n\t\t$history_comment".replace("null","")
            }
        }

//        hujjatTarixi.add("$document_signer_fio $history_comment_all \n\t\t" +
//                "$history_signed_at")
        hujjatTarixi.add(StringWithFileID("$document_signer_fio $history_comment_all \n $history_signed_at","0000"))

//        activity?.runOnUiThread {
//          var  child1=DefaultTreeNode("$document_signer_fio $history_comment_all \n$history_signed_at")
//           root?.addChild(child1)
//           adapter!!.notifyDataSetChanged()
//        }

    }

    fun onRestart() {
        RequsetWithHistory()
        this.onRestart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            Constants.CREATE_PKCS7 -> {
                onCreate(resultCode, data, document_id)
            }
            Constants.APPEND_CODE -> {
                onAppend(resultCode, data, document_id)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun onCreate(resultCode: Int, data: Intent?, document_id: String?) {

        when (resultCode) {
            Constants.RESULT_ACCESS_DENIED -> {
                Toast.makeText(context, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                Log.e(MyApplication.horcrux.tag, "Проверьте, правильно ли вы ввели API_KEY")
                return
            }

            Activity.RESULT_OK -> {
                MyApplication.horcrux.parsePFX(data)
                var getpkcs = MyApplication.horcrux.getPKCS()
                if (document_id != null && GLOBALCOMMENT != null) {
                    var signer_serial_number = data?.getCharSequenceExtra(Constants.EXTRA_RESULT_SERIAL_NUMBER)
                    RequsetWithReaction(GLOBALCOMMENT, signer_serial_number.toString())
                    RequsetWithDocument(getpkcs, document_id)
                    Thread.sleep(3000)
                    (activity as Dashboard).RequsetWithNotification(token_type, access_token)
                    fragmentManager?.popBackStack()
                }
                return
            }
            Constants.RESULT_ERROR -> return
            else -> return
        }
    }

    //
    private fun onAppend(resultCode: Int, data: Intent?, document_id: String?) {
        when (resultCode) {
            Constants.RESULT_ACCESS_DENIED -> {
                Toast.makeText(context, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                Log.e(MyApplication.horcrux.tag, "Проверьте, правильно ли вы ввели API_KEY")
                return
            }
            Activity.RESULT_OK -> {
                MyApplication.horcrux.parsePFX(data)
                var signer_serial_number = data?.getCharSequenceExtra(Constants.EXTRA_RESULT_SERIAL_NUMBER)
                var getpkcs = MyApplication.horcrux.getPKCS()

                if (document_id != null && GLOBALCOMMENT  != null) {
                    RequsetWithReaction(GLOBALCOMMENT , signer_serial_number.toString())
                    RequsetWithDocument(getpkcs, document_id)
                    Thread.sleep(3000)
                    (activity as Dashboard).RequsetWithNotification(token_type, access_token)
                    fragmentManager?.popBackStack()
                }
                return
            }
            Constants.RESULT_ERROR -> return
            else -> return
        }
    }

    private fun RequsetWithReaction(etdescription: String?, signer_serial_number: String) {

        val URL = ConnectURL().REACTIONURL
        var okHttpClient: OkHttpClient = OkHttpClient()

        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("description", GLOBALCOMMENT)
            .addFormDataPart("document_id", document_id)
            .addFormDataPart("sign_type", "1")
            .addFormDataPart("signer_serial_number", signer_serial_number)
            .addFormDataPart("status", status)
            .build()
        val request: Request = Request
            .Builder()
            .url(URL)
            .method("POST", body)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
            }
        })
    }

    private fun RequsetWithDocument(getpkcs: String, document_id: String) {

        val URL = ConnectURL().EIMZOLURL
        var okHttpClient: OkHttpClient = OkHttpClient()

        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("document_id", document_id)
            .addFormDataPart(
                "base64",
                "$getpkcs"
            )
            .build()
        val request: Request = Request
            .Builder()
            .url(URL)
            .method("POST", body)
            .addHeader(
                "Authorization",
                "Bearer $access_token"
            )
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
                //val json = response?.body()?.string()
            }
        })
    }

    private fun onAttached(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Constants.RESULT_ACCESS_DENIED -> {
                Toast.makeText(context, "Доступ запрещен", Toast.LENGTH_SHORT).show()
                Log.e(MyApplication.horcrux.tag, "Проверьте, правильно ли вы ввели API_KEY")
                return
            }
            Activity.RESULT_OK -> {
                //  Ваш готовый подписанный PKCS7. Готов к отправке
                //  val pkcs = data?.getByteArrayExtra(EXTRA_RESULT_PKCS7) //  ByteArray
                return
            }
            Constants.RESULT_ERROR -> return
            else -> return
        }
    }

    override fun loadComplete(nbPages: Int) {



//        if (nbPages!! >= 2)
//        {
//           pdfView?.minimumHeight = 1550 * 2
//        }
//        else
//        {
//            pdfView?.minimumHeight = dpToPx(1900, context);
//        }
    }

    fun dpToPx(dp: Int, context: Context?): Int {
        val density: Float = context?.getResources()?.getDisplayMetrics()?.density!!
        return Math.round(dp.toFloat() * density)
    }
}
