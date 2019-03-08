package deselby.std

import kotlin.math.max

class DoubleNDArray {
    private val strideArray    : IntArray
    private val data      : DoubleArray
    val indexSet : NDIndexSet
    val dimension : List<Int>
        get() = indexSet.dimension

    val stride : List<Int>
        get() = strideArray.asList()
    val size : Int
        get() = data.size


    constructor(dimension : List<Int>, init : (IntArray) -> Double) {
        indexSet = NDIndexSet(IntArray(dimension.size, {dimension[it]}))
        strideArray = dimensionToStride(dimension)
        data = DoubleArray(strideArray.last()*dimension.last(), {init(toNDIndex(it))})
    }

    constructor(dimension : IntArray, init : (IntArray) -> Double) : this(dimension.asList(), init)


    protected constructor(indexSet : NDIndexSet, stride : IntArray, init : (IntArray) -> Double) {
        this.indexSet = indexSet
        strideArray = stride
        data = DoubleArray(strideArray.last()*dimension.last(), {init(toNDIndex(it))})
    }


    protected constructor(indexSet : NDIndexSet, stride : IntArray, data : DoubleArray) {
//        this.dim = dimension
        this.indexSet = indexSet
        this.strideArray = stride
        this.data = data
    }


    open fun map(mapFunc : (Double) -> Double) =
        DoubleNDArray(indexSet, strideArray, DoubleArray(data.size, { i -> mapFunc(data[i])}))


    open fun mapIndexed(mapFunc : (IntArray, Double) -> Double) =
        DoubleNDArray(indexSet, strideArray, DoubleArray(data.size, { i -> mapFunc(toNDIndex(i),data[i])}))


    fun binaryOp(other : DoubleNDArray, mapFunc : (Double, Double) -> Double) : DoubleNDArray {
        if(dimension.size != other.dimension.size)
            throw(IllegalArgumentException("Can't perform binary operation on DoubleNDArrays of differing dimensionality"))
        val dimensionUnion = IntArray(dimension.size,{ d -> max(dimension[d], other.dimension[d]) })
        val thisNDArray = this
        return DoubleNDArray(dimensionUnion.asList(), { unionIndex ->
                    mapFunc(thisNDArray.getOrNull(unionIndex)?:0.0, other.getOrNull(unionIndex)?:0.0)
                })
    }


    operator fun plus(other : DoubleNDArray) = binaryOp(other, Double::plus)

    operator fun minus(other : DoubleNDArray) = binaryOp(other, Double::minus)

    operator fun times(c : Double) = DoubleNDArray(indexSet, strideArray, DoubleArray(data.size, {data[it]*c}))

    operator fun div(c : Double) = DoubleNDArray(indexSet, strideArray, DoubleArray(data.size, {data[it]/c}))

    operator fun timesAssign(other : Double) {
        for(i in 0..data.lastIndex) {
            data[i] *= other
        }
    }

    operator fun divAssign(other : Double) {
        for(i in 0..data.lastIndex) {
            data[i] /= other
        }
    }

    fun <R> fold(initial : R, operation : (R, Double) -> R) : R =
        data.fold(initial, operation)


//    open fun sum() = data.sum()

    operator fun get(index : IntArray) : Double {
        val flatI = toFlatIndex(index)
        return if(flatI != null) data[flatI] else throw(ArrayIndexOutOfBoundsException())
    }

    fun getOrNull(index : IntArray) : Double? {
        val flatI = toFlatIndex(index)
        return if(flatI != null) data[flatI] else null
    }

    fun getOrElse(index : IntArray, default : (IntArray) -> Double) : Double {
        val flatI = toFlatIndex(index)
        return if(flatI != null) data[flatI] else default(index)
    }

    operator fun set(index : IntArray, c : Double) {
        val flatI = toFlatIndex(index)
        return if(flatI != null) data[flatI] = c else throw(ArrayIndexOutOfBoundsException())
    }


    fun asDoubleArray() = data


    private fun dimensionToStride(dim : List<Int>) : IntArray {
        val stride = IntArray(dim.size, {1})
        for(i in 1 until stride.size) {
            stride[i] = stride[i-1]*dim[i-1]
        }
        return stride
    }


//    fun forEachIndexed(func : (IntArray, Double) -> Unit) {
//        data.forEachIndexed { flatIndex, d -> func(toNDIndex(flatIndex), d) }
//    }


    open fun toFlatIndex(index : IntArray) : Int? {
        var i = 0
        for(d in 0 until index.size) {
            val id = index[d]
            if(id >= indexSet.dimension[d]) return null
            i += id*strideArray[d]
        }
        return i
    }


    fun toNDIndex(i : Int) : IntArray {
        val nd = IntArray(strideArray.size, {0})
        var v = i
        for(d in strideArray.size - 1 downTo 0) {
            nd[d] = v.div(strideArray[d])
            v = v.rem(strideArray[d])
        }
        return nd
    }

    fun slice(sliceDirection : Int, sliceOffset : Int) = DoubleNDArraySlice(this, sliceDirection, sliceOffset)

    override fun toString() : String {
        var s = ""
        for(ndi in indexSet) {
            s += "${ndi.asList()} -> ${this[ndi]}\n"
        }
        return s
    }
}
