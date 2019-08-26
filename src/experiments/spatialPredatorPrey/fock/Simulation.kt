package experiments.spatialPredatorPrey.fock

import deselby.fockSpace.*
import deselby.fockSpace.extensions.integrate
import deselby.fockSpace.extensions.times
import deselby.fockSpace.extensions.toAnnihilationIndex
import experiments.phasedMonteCarlo.monteCarlo

class Simulation {
    companion object {
        const val GRIDSIZE = 3
    }

    var samples = ArrayList<MutableCreationBasis<Agent>>()
    var D0: DeselbyGround<Agent>
    val H = calcFullHamiltonian()
    val hIndex = H.toAnnihilationIndex()


    constructor(lambdaPred: Double, lambdaPrey: Double) {
        val lambdas = HashMap<Agent,Double>()
        for(pos in 0 until GRIDSIZE*GRIDSIZE) {
            lambdas[Predator(pos)] = lambdaPred
            lambdas[Prey(pos)] = lambdaPrey
        }
        D0 = DeselbyGround(lambdas)
    }


    fun calcFullHamiltonian(): FockVector<Agent> {
        val H = HashFockVector<Agent>()
        for(x in 0 until GRIDSIZE) {
            for(y in 0 until GRIDSIZE) {
                Predator(x,y).hamiltonian(H)
                Prey(x,y).hamiltonian(H)
            }
        }
        return H
    }


    fun monteCarloIntegrate(startState: CreationBasis<Agent>, nSamples: Int, integrationTime: Double) : CreationVector<Agent> {
        val reducedHamiltonian = H * startState.asGroundedBasis(D0)
        val total = HashCreationVector<Agent>()
        for(i in 1..nSamples) {
            val mcSample = startState.asGroundedBasis(D0).monteCarlo(hIndex, reducedHamiltonian, integrationTime)
            total += mcSample
//            if(i.rem(2000) == 0) println(mcSample)
        }
        return total / nSamples.toDouble()
    }


    fun simulate(nParticles: Int, T: Double) {

    }



}