package org.tea.algos.search;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BinarySearchBenchmarkTest {

    private Searchable binarySearch;

    @Param({"100", "10000", "1000000"})
    public int arraySize;

    public int[] array;
    public Integer targetBestCase;
    public Integer targetAvgCase;
    public Integer targetWorstCase;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        TestContextManager contextManager = new TestContextManager(getClass());
        contextManager.prepareTestInstance(this);

        ApplicationContext ctx = contextManager.getTestContext().getApplicationContext();
        binarySearch = ctx.getBean(BinarySearch.class);

        array = new int[arraySize];

        targetBestCase  = array[0];
        targetAvgCase   = array[arraySize / 2];
        targetWorstCase = arraySize + 999;
    }

    @Test
    public void executeJmhRunner() throws Exception {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")  // auto-discovers all @Benchmark in this class
                .forks(1)                                    // 1 fork avoids Spring context reload issues
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(1))
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(options).run();
    }

    @Benchmark
    public Object bestCase() {
        return binarySearch.search(array, targetBestCase);
    }

    @Benchmark
    public Object averageCase() {
        return binarySearch.search(array, targetAvgCase);
    }

    @Benchmark
    public Object worstCase() {
        return binarySearch.search(array, targetWorstCase);
    }
}