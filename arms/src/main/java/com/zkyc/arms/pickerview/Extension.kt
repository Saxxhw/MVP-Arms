package com.zkyc.arms.pickerview

import android.app.Activity
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.contrarywind.interfaces.IPickerViewData
import com.zkyc.arms.extension.emptyString

/**
 *
 */
inline fun <T> Activity.showOptionsPicker(
    targetView: TextView,
    items: List<T>,
    crossinline onSelected: (T, Int) -> Unit = { _, _ -> }
) {
    if (items.isNullOrEmpty()) return
    val pvOptions = OptionsPickerBuilder(this) { options1, _, _, _ ->
        val selected = items[options1]
        targetView.tag = options1
        targetView.text = when (selected) {
            is String -> selected
            is IPickerViewData -> selected.pickerViewText
            else -> emptyString()
        }
        onSelected.invoke(selected, options1)
    }
        .setSelectOptions(targetView.tag as? Int ?: 0)
        .build<T>()
    pvOptions.setPicker(items)
    pvOptions.show()
}