package org.kuleuven.distrinet.learnlibexample;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.learnlib.algorithms.lstar.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstar.closing.ClosingStrategies;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.api.SUL;
import de.learnlib.api.algorithm.LearningAlgorithm;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.oracle.equivalence.MealyWMethodEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.visualization.dot.DOT;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

import java.io.File;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {

        // the input alphabet
        Alphabet<String> inputAlphabet = Alphabets.fromCollection(ImmutableSet.of("symbolA", "symbolB", "symbolC"));

        // Load the example SUL
        SUL<String, Integer> sul = new ExampleSUL();
        // Oracle wrapper for SUL
        MealyMembershipOracle<String, Integer> sulOracle = new SULOracle<>(sul);

        //Equivalence oracle
        EquivalenceOracle<MealyMachine<?, String, ?, Integer>, String, Word<Integer>> eqOracle = new MealyWMethodEQOracle<>(sulOracle, 2);
        //Learning algorithm
        LearningAlgorithm<MealyMachine<?, String, ?, Integer>, String, Word<Integer>> learner =
                new ExtensibleLStarMealy<>(inputAlphabet, sulOracle, Lists.newArrayList(), ObservationTableCEXHandlers.CLASSIC_LSTAR, ClosingStrategies.CLOSE_SHORTEST);


        learner.startLearning();
        DefaultQuery<String, Word<Integer>> counterexample = eqOracle.findCounterExample(learner.getHypothesisModel(), inputAlphabet);

        while (counterexample != null) {
            learner.refineHypothesis(counterexample);
            MealyMachine<?, String, ?, Integer> learnedHypothesis = learner.getHypothesisModel();
            counterexample = eqOracle.findCounterExample(learnedHypothesis, inputAlphabet);
        }

        produceOutput(learner.getHypothesisModel(), inputAlphabet);
    }

    /**
     * Produces a dot-file and a PDF (if graphviz is installed)
     */
    public static void produceOutput(MealyMachine<?, String, ?, Integer> model, Alphabet<String> alphabet) {
        try (PrintWriter dotWriter = new PrintWriter("learned-model.dot")) {
            GraphDOT.write(model, alphabet, dotWriter);
            DOT.runDOT(new File("learned-model.dot"), "pdf", new File("learned-model.pdf"));
        } catch (Exception e) {
            System.err.println("Warning: Install graphviz to convert dot-files to PDF");
            System.err.println(e.getMessage());
        }
    }
}