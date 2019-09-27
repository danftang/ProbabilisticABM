package deselby.fockSpaceV1

abstract class AbstractBasis<AGENT> : FockBasis<AGENT> {
    abstract val creations : Map<AGENT,Int>

    abstract fun new(initCreations : Map<AGENT, Int>) : AbstractBasis<AGENT>

    abstract fun groundStateAnnihilate(d: AGENT) : MapFockState<AGENT>

    override fun create(d: AGENT, n : Int): FockBasis<AGENT> {
        val delta = HashMap(creations)
        delta.merge(d, n) {a , b ->
            val newVal = a + b
            if(newVal == 0) null else newVal
        }
        return new(delta)
    }

    override fun create(newCreations: Map<AGENT, Int>): FockBasis<AGENT> {
        val delta = HashMap(creations)
        newCreations.forEach {
            delta.merge(it.key, it.value) {a , b ->
                val newVal = a + b
                if(newVal == 0) null else newVal
            }
        }
        return new(delta)
    }


    // using the commutation relation
    // [a,a*^nAnnihilations] = na*^(nAnnihilations-1)
    // so
    // aa*^nAnnihilations = nAnnihilations.a*^(nAnnihilations-1) + (a*^nAnnihilations)a
    // for all nAnnihilations
    override fun annihilate(d: AGENT): MapFockState<AGENT> {
        val nd = creations[d]?:0
        if(nd == 0) return groundStateAnnihilate(d).create(creations)
        return this.remove(d)*nd.toDouble() + groundStateAnnihilate(d).create(creations)
    }


    override fun count(d: AGENT): Int {
        return creations.getOrDefault(d,0)
    }


    override fun toString() : String {
        var s = ""
        creations.forEach {
            s += "${it.key}:${it.value} "
        }
        return s.dropLast(1)
    }


    override fun hashCode(): Int {
        return creations.hashCode()
    }


    override fun equals(other: Any?): Boolean {
//        if(super.equals(other)) return true
        if(other !is AbstractBasis<*>) return false
        return creations == other.creations
    }

}