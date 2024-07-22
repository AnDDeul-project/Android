import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import com.umc.anddeul.databinding.ItemFamilylistBinding
import com.umc.anddeul.postbox.model.Family


class FamilyAdapter : SpinnerAdapter {
    var families: List<Family>? = null
    private val dataSetObservers = mutableListOf<DataSetObserver>()

    override fun getCount(): Int {
        return families?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return families?.get(position) ?: Any()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBinding = ItemFamilylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        itemBinding.famNm.text = families?.get(position)?.nickname.toString()
        return itemBinding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemBinding = ItemFamilylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        itemBinding.famNm.text = families?.get(position)?.nickname.toString()
        return itemBinding.root
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        if (observer != null) {
            dataSetObservers.add(observer)
        }
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        dataSetObservers.remove(observer)
    }

    override fun getViewTypeCount(): Int {
        return 1 // Return the number of view types, typically 1
    }

    override fun getItemViewType(position: Int): Int {
        return 0 // Return the view type for the given position
    }

    override fun isEmpty(): Boolean {
        return getCount() == 0
    }

    private fun notifyObserversOnDataSetChanged() {
        for (observer in dataSetObservers) {
            observer.onChanged()
        }
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View, pos: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
}

