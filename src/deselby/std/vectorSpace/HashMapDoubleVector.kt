package deselby.std.vectorSpace

import java.util.function.BiFunction

class HashMapDoubleVector<BASIS>(val coeffs : HashMap<BASIS,Double>) : AbstractMutableMap<BASIS,Double>(), MutableDoubleVector<BASIS> {
    override val entries
            get() = coeffs.entries

    constructor(vararg mappings : Pair<BASIS,Double>) : this(HashMap(mappings.size)) {
        coeffs.putAll(mappings)
    }

    override fun put(key: BASIS, value: Double) = coeffs.put(key,value)

    override fun get(key: BASIS) = coeffs.get(key)

    override fun remove(key: BASIS) = coeffs.remove(key)

    override fun remove(key: BASIS, value: Double) = coeffs.remove(key, value)

    override fun merge(key: BASIS, value: Double, remappingFunction: BiFunction<in Double, in Double, out Double?>): Double?
        = coeffs.merge(key, value, remappingFunction)

    constructor() : this(HashMap())

    constructor(vecToCopy : Vector<BASIS,Double>) : this(HashMap(vecToCopy))

    override fun toMutableVector() = HashMapDoubleVector(HashMap(coeffs))

    override fun zero() = HashMapDoubleVector<BASIS>(HashMap())

    override fun toString() : String {
        if(coeffs.isEmpty()) return "{ }"
        var s = ""
        coeffs.forEach {
            s += "%+fP[%s] ".format(it.value, it.key)// ""${it.coeff}P[${it.key}] "
        }
        return s
    }
}