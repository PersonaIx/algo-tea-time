package org.tea.algos;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMH {

    @Test
    public void executeJmhRunner() throws Exception {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .forks(1) // 1 fork prevents Spring context reloading issues
                .warmupIterations(3)
                .measurementIterations(5)
                .build();

        new Runner(options).run();
    }

    @Benchmark
    public void benchmarkTargetMethod() {
        // Call your service logic or benchmark code here
    }
}

