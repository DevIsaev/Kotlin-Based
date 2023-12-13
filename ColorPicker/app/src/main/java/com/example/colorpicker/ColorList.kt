package com.example.colorpicker

class ColorList {
    private val blackHex="000000"
    private val whiteHex="FFFFFF"
    val defaultColor:ColorsObject=basicColors()[0]

    fun colorPosition(colorsObject: ColorsObject):Int{
        for (i in basicColors().indices){
            if(colorsObject==basicColors()[i])
                return i
        }
        return 0
    }
    fun basicColors():List<ColorsObject>{
        return listOf(
            ColorsObject("Black", blackHex, whiteHex),
            ColorsObject("Silver", "C0C0C0", blackHex),
            ColorsObject("Gray", "808080", whiteHex),
            ColorsObject("Maroon", "800000", whiteHex),
            ColorsObject("Red", "FF0000", whiteHex),
            ColorsObject("Fuchsia", "FF00FF", whiteHex),
            ColorsObject("Green", "008000", whiteHex),
            ColorsObject("Lime", "00FF00", blackHex),
            ColorsObject("Olive", "808000", whiteHex),
            ColorsObject("Yellow", "FFFF00", blackHex),
            ColorsObject("Navy", "000080", whiteHex),
            ColorsObject("Blue", "0000FF", whiteHex),
            ColorsObject("Teal", "008080", whiteHex),
            ColorsObject("Aqua", "00FFFF", blackHex)
        )
    }
}